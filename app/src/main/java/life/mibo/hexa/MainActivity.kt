package life.mibo.hexa

import android.Manifest
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.AppStatusEvent
import life.mibo.hardware.models.Device
import life.mibo.hardware.network.CommunicationListener
import life.mibo.hexa.Callback.Companion.CONNECT
import life.mibo.hexa.Callback.Companion.DISCONNECT
import life.mibo.hexa.Callback.Companion.SCAN
import life.mibo.hexa.models.ScanComplete
import life.mibo.hexa.ui.base.BaseActivity
import life.mibo.hexa.ui.base.FragmentHelper
import life.mibo.hexa.ui.base.PermissionHelper
import life.mibo.hexa.ui.base.ScreenNavigator
import life.mibo.hexa.utils.Toasty
import org.greenrobot.eventbus.EventBus
import java.net.InetAddress
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity(), Callback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var commHandler: CommHandler
    private var navigator: ScreenNavigator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val drawer: NavigationView = findViewById(R.id.nav_view)
        navigator =
            ScreenNavigator(FragmentHelper(this, R.id.nav_host_fragment, supportFragmentManager))

        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)

        drawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_test1 -> {
                    navController.navigate(R.id.navigation_channels)
                }
                R.id.nav_test2 -> {
                    startScanning(false)
                    navController.navigate(R.id.navigation_devices)
                }
                else -> {
                    Snackbar.make(drawer, "item clicked " + it.itemId, Snackbar.LENGTH_LONG).show()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true;
        }
//        drawer.setupWithNavController(navController)

        //getScanned()
        commHandler = CommHandler()
        checkPermissions()
        //startManager()
        commHandler.regisiter()
    }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )

    fun checkPermissions() {

        dummySession()
        PermissionHelper.requestPermission(this@MainActivity, permissions) {
            startManager()
        }
    }

    fun dummySession() {
        SessionManager.getInstance().createDummySession()
    }

    lateinit var manager: CommunicationManager
    fun startManager() {
        log("getScanning started")


        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifi?.isWifiEnabled = true
        val lock = wifi.createMulticastLock("MulticastMibo")
        lock.setReferenceCounted(true)
        lock?.acquire()

        manager = CommunicationManager.getInstance(object : CommunicationListener {
            override fun onDeviceDiscoveredEvent(device: Device?) {
                log("onDeviceDiscoveredEvent Device $device")
                EventBus.getDefault().post(life.mibo.hardware.events.NewDeviceDiscoveredEvent(device))
            }

            override fun onBluetoothDeviceFound(result: ScanResult?) {
                log("onBluetoothDeviceFound " + result)
                EventBus.getDefault()
                    .post(life.mibo.hardware.events.NewDeviceDiscoveredEvent(result?.device))
            }

            override fun udpDeviceReceiver(msg: ByteArray?, ip: InetAddress?) {
                log("udpDeviceReceiver " + String(msg!!))
                EventBus.getDefault()
                    .post(life.mibo.hardware.events.NewDeviceDiscoveredEvent(ip))
                }

            override fun onConnectionStatus(getname: String?) {
                log("onConnectionStatus $getname")
                }

            override fun onAlarmEvent() {
                log("onAlarmEvent ")
            }

            override fun onDeviceDiscoveredEvent(s: String?) {
                log("onDeviceDiscoveredEvent String $s")
               // EventBus.getDefault().post(life.mibo.hardware.events.NewDeviceDiscoveredEvent(s))
                //manager.discoveredDevices

                }

                override fun HrEvent(hr: Int, uid: String?) {
                    log("HrEvent $hr : $uid")
                }

                override fun DeviceStatusEvent(uid: String?) {
                    log("DeviceStatusEvent $uid")
                }

                override fun ChangeColorEvent(d: Device?, uid: String?) {
                    log("DeviceStatusEvent $d : $uid")
                }

                override fun GetMainLevelEvent(mainLevel: Int, uid: String?) {
                    log("GetMainLevelEvent $mainLevel : $uid")
                }

                override fun GetLevelsEvent(uid: String?) {
                    log("GetLevelsEvent  $uid")
                }

                override fun ProgramStatusEvent(
                    time: Int,
                    action: Int,
                    pause: Int,
                    currentBlock: Int,
                    currentProgram: Int,
                    uid: String?
                ) {

                }

                override fun DevicePlayPauseEvent(uid: String?) {
                    log("GetLevelsEvent  $uid")
                }

            })

        log("getScanning finished")
    }

    fun startScanning(rescan: Boolean, wifi: Boolean = true) {
        log("Scanning.......")
        if (rescan)
            manager.reScanning(this, wifi)
        else
            manager.startScanning(this, wifi)
        Observable.timer(15, TimeUnit.SECONDS).doOnComplete { stopScanning() }.subscribe()
    }

    fun stopScanning() {
        manager.stopScanning()
        EventBus.getDefault().post(ScanComplete())
    }

    private val batteryLevelReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

            //int rawLevel = intent.getIntExtra("level", -1);
            val rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            //int scale = intent.getIntExtra("scale", -1);
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            var level = -1
            if (rawLevel >= 0 && scale > 0) {
                level = rawLevel * 100 / scale
            }
            //Log.e("main","Battery Level Remaining  :" + level + "% "+isCharging);
            SessionManager.getInstance().deviceBatteryLevel = level
            SessionManager.getInstance().isDeviceCharging = isCharging
            EventBus.getDefault().postSticky(
                AppStatusEvent(
                    SessionManager.getInstance().deviceWifiLevel,
                    level,
                    isCharging
                )
            )
        }

    }

    private val wifiLevelReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val wifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val state = wifiManager.wifiState
            val numberOfLevels = 5
            val wifiInfo = wifiManager.connectionInfo
            val level = WifiManager.calculateSignalLevel(wifiInfo.rssi, numberOfLevels)



            SessionManager.getInstance().deviceWifiLevel = level
            SessionManager.getInstance().deviceWifiName =
                wifiManager.connectionInfo.ssid.replace("\"", "")
            SessionManager.getInstance().deviceWifiState = state
            EventBus.getDefault().postSticky(
                AppStatusEvent(
                    level,
                    SessionManager.getInstance().getDeviceBatteryLevel(),
                    SessionManager.getInstance().isDeviceCharging()
                )
            )
            //   Log.e("main","WIFI Level:" + level + " state:"+state);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionHelper.permissionsResult(
            this@MainActivity,
            permissions as Array<String>,
            requestCode,
            grantResults,
            {
                startManager()
            },
            {
                Toasty.error(this@MainActivity, "Permission Denied")
            })
    }

    override fun onCall(type: Int, data: Any?) {
        log("Call $type || $data")
        when (type) {
            CONNECT -> {
                if (data is Device)
                    manager.connectDevice(data)
            }
            DISCONNECT -> {
                if (data is Device)
                    manager.deviceDisconnect(data)
            }
            SCAN -> {
                //stopScanning()
                //manager.stopScanning()
                if (data is Boolean)
                    startScanning(true, data)
            }
        }
    }



    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        commHandler.unregisiter()
        super.onDestroy()
        manager?.onDestroy()
    }

}
