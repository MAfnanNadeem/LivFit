/*
 *  Created by Sumeet Kumar on 1/14/20 4:45 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/14/20 4:43 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.zxing.BarcodeFormat
import com.kroegerama.kaiteki.bcode.ui.BarcodeFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import life.mibo.hardware.AlarmManager
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.constants.CommunicationConstants.*
import life.mibo.hardware.core.DataParser
import life.mibo.hardware.events.*
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceConstants.*
import life.mibo.hardware.models.UserSession
import life.mibo.hardware.network.CommunicationListener
import life.mibo.hexa.BuildConfig
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.ScanComplete
import life.mibo.hexa.ui.base.BaseActivity
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.base.PermissionHelper
import life.mibo.hexa.ui.home.HomeItem
import life.mibo.hexa.ui.login.LoginActivity
import life.mibo.hexa.ui.main.Navigator.Companion.CLEAR_HOME
import life.mibo.hexa.ui.main.Navigator.Companion.CONNECT
import life.mibo.hexa.ui.main.Navigator.Companion.DISCONNECT
import life.mibo.hexa.ui.main.Navigator.Companion.HOME
import life.mibo.hexa.ui.main.Navigator.Companion.HOME_VIEW
import life.mibo.hexa.ui.main.Navigator.Companion.SCAN
import life.mibo.hexa.ui.main.Navigator.Companion.SELECT_PROGRAM
import life.mibo.hexa.ui.main.Navigator.Companion.SESSION
import life.mibo.hexa.utils.Toasty
import org.greenrobot.eventbus.EventBus
import java.net.InetAddress
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity(), Navigator {

    //private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    private lateinit var commHandler: CommHandler
    private var bottomBarHelper = BottomBarHelper()
    //private var navigator: ScreenNavigator? = null
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val toolbar: Toolbar? = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            drawerLayout = findViewById(R.id.drawer_layout)
            val navigation: NavigationView = findViewById(R.id.nav_view)
            //navigator = ScreenNavigator(FragmentHelper(this, R.id.nav_host_fragment, supportFragmentManager))


            navController = findNavController(R.id.nav_host_fragment)
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
            //val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
            // bottomNavView.setupWithNavController(navController)
            //setBottomBar()
            setDrawerIcon(drawerLayout)

            setNavigationView(navigation)

//        drawer.setupWithNavController(navController)

            //getScanned()
            commHandler = CommHandler(this)
            checkPermissions()
            //startManager()
            commHandler.regisiter()

            setBottomBar()

            log("OnCreate end")
        } else {
            log("OnCreate savedInstanceState end")
        }

    }


    private fun setBottomBar() {
        bottomBarHelper.register(item1, item2, item3, item4)
        bottomBarHelper.listener = object : ItemClickListener<Any> {
            override fun onItemClicked(item: Any?, position: Int) {
                bottomBarClicked(position)
            }
        }

        bottomBarHelper.bind(bottom_bar)
    }

    private fun setNavigationView(navigation: NavigationView) {
        navigation.setNavigationItemSelectedListener {
            drawerItemClicked(it.itemId)
            if (::drawerLayout.isInitialized)
                drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        val member = Prefs.get(this).member ?: return
        //drawer_user_email?.text = member.imageThumbnail
        navigation.getHeaderView(0).findViewById<TextView?>(R.id.drawer_user_name)?.text =
            "${member.firstName} ${member.lastName}"
//        /drawer.getHeaderView(0).findViewById<TextView?>(R.id.drawer_user_email)?.text = "${member.email}"
        navigation.getHeaderView(0).findViewById<TextView?>(R.id.drawer_user_email)?.text =
            Prefs.get(this@MainActivity).get("user_email")

        if (!BuildConfig.DEBUG) {
            navigation.menu.clear()
            navigation.inflateMenu(R.menu.activity_drawer_drawer_release)
        }
    }

    private fun setDrawerIcon(drawer: DrawerLayout) {
        //supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setDisplayShowHomeEnabled(true)
        drawerToggle =
            ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.app_name,
                R.string.app_name
            )
//        drawerToggle = ActionBarDrawerToggle(this, toolbar, drawer, null, R.string.app_name, R.string.app_name){
//        }
        drawerToggle.drawerArrowDrawable = DrawerArrowDrawable(this)
        drawerToggle.drawerArrowDrawable?.color = Color.WHITE
        drawerToggle.setToolbarNavigationClickListener {
            if (!drawer.isDrawerOpen(GravityCompat.START))
                drawer.openDrawer(GravityCompat.START)
        }
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.isDrawerIndicatorEnabled = true;
        drawerToggle.syncState();
        //drawerToggle.isDrawerIndicatorEnabled = true
        setupActionBarWithNavController(navController, drawer)
        toolbar!!.setupWithNavController(navController, drawer)
        //too.setupWithNavController(navController, config)
        drawer.post {
            drawerToggle.syncState()
        }

    }

    fun setBottomBar(navController: NavController) {
        //val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        // bottomNavView.setupWithNavController(navController)
    }

    @SuppressLint("CheckResult")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (::drawerToggle.isInitialized)
            drawerToggle.syncState()
        log("PostCreate")
        // Single.timer(1, TimeUnit.SECONDS)
        Single.just(R.id.nav_home).delay(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe { a ->
                //EventBus.getDefault().postSticky(NotifyEvent(a, null))
                //navigateFragment(a)
            }
    }


    fun shoBarcode(){
        val barcodeFragment = BarcodeFragment.makeInstance(
            formats = listOf(BarcodeFormat.QR_CODE),
            barcodeInverted = false
        )
    }

    fun setToolbar(drawerLayout: DrawerLayout) {
        val toolbar = supportActionBar
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true)
            drawerToggle =
                ActionBarDrawerToggle(
                    this, drawerLayout,
                    R.string.app_name,
                    R.string.app_name
                )
        }

    }

    private fun updateMenu() {
       // bottom_nav_view.menu.clear()
        try {
            Thread.sleep(100)
        } catch (e: Exception) {
        }
        if (bottom_nav_view.maxItemCount < 5)
            bottom_nav_view?.menu?.add(
                Menu.NONE,
                R.id.navigation_devices,
                Menu.NONE,
                "Home"
            )?.setIcon(R.drawable.ic_home_black_24dp);
        //bottom_nav_view?.inflateMenu(R.menu.bottom_nav_menu_rxl)
        //val menu = bottom_nav_view.menu as BottomNavigationMenu

//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_discover,
//                R.id.navigation_create,
//                R.id.navigation_analytic,
//                R.id.navigation_more
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        bottom_nav_view.setupWithNavController(navController)
    }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )

    private fun checkPermissions() {

        SessionManager.initUser()
        PermissionHelper.requestPermission(this@MainActivity, permissions) {
            startManager()
        }
    }

    val REQUEST_ENABLE_BT = 1022
    var manager: CommunicationManager? = null
    private fun startManager() {
        log("getScanning started")


        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifi?.isWifiEnabled = true
        val lock = wifi.createMulticastLock("MulticastMibo")
        lock.setReferenceCounted(true)
        lock?.acquire()

        val bl = BluetoothAdapter.getDefaultAdapter()
        if (bl != null && !bl.isEnabled) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BT
            )
        }



        manager = CommunicationManager.getInstance(object : CommunicationListener {
            override fun onCommandReceived(code: Int, command: ByteArray, uid: String?) {
                parseCommands(code, command, uid)
            }

            override fun onDeviceDiscoveredEvent(device: Device?) {
                log("onDeviceDiscoveredEvent Device $device")
                EventBus.getDefault().post(NewDeviceDiscoveredEvent(device))
            }

            override fun onBluetoothDeviceFound(result: ScanResult?) {
                log("onBluetoothDeviceFound " + result)
                EventBus.getDefault()
                    .post(NewDeviceDiscoveredEvent(result?.device))
            }

            override fun udpDeviceReceiver(msg: ByteArray?, ip: InetAddress?) {
                log("udpDeviceReceiver " + String(msg!!))
                //EventBus.getDefault().post(NewDeviceDiscoveredEvent(ip))

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
                    EventBus.getDefault().postSticky(GetMainLevelEvent(mainLevel, uid))
                    EventBus.getDefault().postSticky(SendMainLevelEvent(1, uid));

                }

                override fun GetLevelsEvent(uid: String?) {
                    log("GetLevelsEvent  $uid")
                    EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid))
                }

                override fun ProgramStatusEvent(
                    time: Int,
                    action: Int,
                    pause: Int,
                    currentBlock: Int,
                    currentProgram: Int,
                    uid: String?
                ) {
                    log("ProgramStatusEvent  $uid")
                    EventBus.getDefault().postSticky(
                        ProgramStatusEvent(time, action, pause, currentBlock, currentProgram, uid)
                    )

                }

                override fun DevicePlayPauseEvent(uid: String?) {
                    log("GetLevelsEvent  $uid")
                    EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid))
                }

            })

        log("getScanning finished")
    }

    private fun parseCommands(code: Int, command: ByteArray, uid: String?) {
        logw("parseCommands $code : data " + command.contentToString())
        when (code) {
            COMMAND_PING_RESPONSE -> {
                logw("parseCommands COMMAND_PING_RESPONSE")
            }
            COMMAND_DEVICE_STATUS_RESPONSE -> {
                logw("parseCommands COMMAND_DEVICE_STATUS_RESPONSE")
                val d = SessionManager.getInstance().userSession.device
                if (d != null && d.uid == uid) {
                    logw(
                        "COMMAND_DEVICE_STATUS_RESPONSE UID MATCHED battery " + DataParser.getStatusBattery(
                            command
                        )
                    )
                    d.batteryLevel = DataParser.getStatusBattery(command)
                    d.signalLevel = DataParser.getStatusSignal(command)
                    SessionManager.getInstance().userSession.device.batteryLevel =
                        DataParser.getStatusBattery(command)
                    SessionManager.getInstance().userSession.device.signalLevel =
                        DataParser.getStatusSignal(command)
                    // updated device status/line
                    EventBus.getDefault().postSticky(DeviceStatusEvent(d))
                    if (d.statusConnected != DEVICE_WAITING && d.statusConnected != DEVICE_CONNECTED) {
                        if (d.statusConnected == DEVICE_DISCONNECTED) {
                            EventBus.getDefault().postSticky(ChangeColorEvent(d, d.uid))
                        }
                        d.statusConnected = DEVICE_CONNECTED
                        SessionManager.getInstance().userSession.device.statusConnected =
                            DEVICE_CONNECTED

                        EventBus.getDefault().postSticky(NewConnectionStatus(uid))
                    } else {
                        d.statusConnected = DEVICE_CONNECTED
                        SessionManager.getInstance().userSession.device.statusConnected =
                            DEVICE_CONNECTED
                    }
                    if (SessionManager.getInstance().userSession.currentSessionStatus == 1 || SessionManager.getInstance().userSession.currentSessionStatus == 2) {
                        if (SessionManager.getInstance().userSession.getRegisteredDevicebyUid(uid).setNewDeviceChannelAlarms(
                                DataParser.getChannelAlarms(command)
                            )
                        ) {
                            AlarmManager.getInstance().alarms.AddDeviceChannelAlarm(
                                SessionManager.getInstance().userSession.getRegisteredDevicebyUid(
                                    uid
                                ).deviceChannelAlarms, d.uid
                            )
                            EventBus.getDefault().postSticky(NewAlarmEvent())
                        }
                    }
                    // TODO check later functionality remaining
                    //SessionManager.getInstance().userSession.checkDeviceStatus(DataParser.getStatusFlags(command), uid)
                    checkDeviceStatus(SessionManager.getInstance().userSession, DataParser.getStatusFlags(command), uid)
                    logw(
                        "signal:" + DataParser.getStatusSignal(command) + " bat:" + DataParser.getStatusBattery(
                            command
                        )
                    )
                } else {
                    logw("COMMAND_DEVICE_STATUS_RESPONSE UID NOT MATCHED")
                }
            }
            COMMAND_FIRMWARE_REVISION_RESPONSE -> {
                logw("parseCommands COMMAND_FIRMWARE_REVISION_RESPONSE")
            }
            COMMAND_SET_DEVICE_COLOR_RESPONSE -> {
                logw("parseCommands COMMAND_SET_DEVICE_COLOR_RESPONSE")
            }
            COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE -> {
                logw("parseCommands COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE")
            }
            COMMAND_SET_MAIN_LEVEL_RESPONSE -> {
                logw("parseCommands COMMAND_SET_MAIN_LEVEL_RESPONSE")
                EventBus.getDefault()
                    .postSticky(GetMainLevelEvent(DataParser.getMainLevel(command), uid))
                SessionManager.getInstance().userSession.user.mainLevel =
                    DataParser.getMainLevel(command)
                //EventBus.getDefault().postSticky(new SendMainLevelEvent(1,uid));
            }
            COMMAND_SET_CHANNELS_LEVELS_RESPONSE -> {
                logw("parseCommands COMMAND_SET_CHANNELS_LEVELS_RESPONSE")
                EventBus.getDefault().postSticky(GetLevelsEvent(uid))
            }
            COMMAND_START_CURRENT_CYCLE_RESPONSE -> {
                logw("parseCommands COMMAND_START_CURRENT_CYCLE_RESPONSE")
                SessionManager.getInstance().userSession.device.isStarted = true
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid))
            }
            COMMAND_PAUSE_CURRENT_CYCLE_RESPONSE -> {
                logw("parseCommands COMMAND_PAUSE_CURRENT_CYCLE_RESPONSE")
                SessionManager.getInstance().userSession.device.isStarted = false
                //SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setIsStarted(false);
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid))
            }
            COMMAND_RESET_CURRENT_CYCLE_RESPONSE -> {
                logw("parseCommands COMMAND_RESET_CURRENT_CYCLE_RESPONSE")
            }
            ASYNC_PROGRAM_STATUS -> {
                logw("parseCommands ASYNC_PROGRAM_STATUS")
                SessionManager.getInstance().userSession.device.deviceSessionTimer =
                    DataParser.getProgramStatusTime(command)
                EventBus.getDefault().postSticky(
                    ProgramStatusEvent(
                        DataParser.getProgramStatusTime(command),
                        DataParser.getProgramStatusAction(command),
                        DataParser.getProgramStatusPause(command),
                        DataParser.getProgramStatusCurrentBlock(command),
                        DataParser.getProgramStatusCurrentProgram(command),
                        uid
                    )
                )
                SessionManager.getInstance().userSession.getRegisteredDevicebyUid(uid)
                    .deviceSessionTimer =
                    DataParser.getProgramStatusTime(command)
            }
            COMMAND_ASYNC_SET_MAIN_LEVEL -> {
                logw("parseCommands COMMAND_ASYNC_SET_MAIN_LEVEL")
                EventBus.getDefault()
                    .postSticky(GetMainLevelEvent(DataParser.getMainLevelAsync(command), uid));
                SessionManager.getInstance().userSession.user.mainLevel =
                    DataParser.getMainLevelAsync(command)
            }
            COMMAND_ASYNC_PAUSE -> {
                logw("parseCommands COMMAND_ASYNC_PAUSE")
                SessionManager.getInstance().userSession.device.isStarted = false
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid));
            }
            COMMAND_ASYNC_START -> {
                logw("parseCommands COMMAND_ASYNC_START")
                SessionManager.getInstance().userSession.device.isStarted = true
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid))

            }
            else -> {
                logw("parseCommands DEFAULTS")
            }
        }

        //  Log.e("commManager", "Receiver PING");
        //Log.e("commManager", "Receiver PROGRAM");
        //EventBus.getDefault().postSticky(new SendMainLevelEvent(1,uid));
        //EventBus.getDefault().postSticky(new GetLevelsEvent(uid));
        //  Log.e("commManager", "Receiver LEVELS");
        //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
        //Log.e("commManager", "Receiver START");
        //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
        //Log.e("commManager", "Receiver PAUSE");
        //Log.e("commManager", "Receiver RESET");
        //  Log.e("commManager", "Receiver ASYNC PROGRAM STATUS");
        //Log.e("commManager", "Receiver ASYNC MAINLEVEL");
        //  EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
        //Log.e("commManager", "Receiver ASYNC Pause");
        //EventBus.getDefault().postSticky(new DevicePlayPauseEvent(uid));
        //Log.e("commManager", "Receiver ASYNC Pause");
    }

    private fun checkDeviceStatus(user: UserSession, status: BooleanArray, uid: String?) {
        logw("checkDeviceStatus wifi " + status[0] + " ble " + status[1] + " program " + status[2] + " runn " + status[3] + " color " + status[4])

        if (user.currentSessionStatus == 2 || user.currentSessionStatus == 1) {
            if (user.device.uid == uid) {
                logw("checkDeviceStatus UID MATCH")
                if (!status[2]) {//check if program
                    logw("checkDeviceStatus SendProgramEvent")
                    EventBus.getDefault()
                        .postSticky(SendProgramEvent(user.currentSessionProgram, uid));
                }
                if (!status[4]) {//check if color if (listener != null)
                    logw("checkDeviceStatus ChangeColorEvent")
                    EventBus.getDefault().postSticky(ChangeColorEvent(user.device, uid));
                }
                if (status[3]) {//check if run
                    //logw("checkDeviceStatus status is 3")
                }
                if (status.size >= 6) {
                    if (!status[5]) {//check if channels are loaded
                        EventBus.getDefault().postSticky(
                            SendChannelsLevelEvent(
                                user.user.currentChannelLevels,
                                uid
                            )
                        );
                        logw("checkDeviceStatus SendChannelsLevelEvent")
                    }
                }

            } else {
                logw("checkDeviceStatus UID NOT MATCH")
            }
        } else {
            logw("checkDeviceStatus currentSessionStatus "+user.currentSessionStatus)
        }
    }

    var scanDisposable: Disposable? = null
    private fun startScanning(rescan: Boolean, wifi: Boolean = true) {
        log("Scanning.......")
        scanDisposable?.dispose()
        if (manager == null)
            startManager()
        if (rescan)
            manager?.reScanning(this, wifi)
        else
            manager?.startScanning(this, wifi)
        scanDisposable =
            Single.just("").delay(15, TimeUnit.SECONDS).subscribe { i -> stopScanning() }

    }

    fun stopScanning() {
        manager?.stopScanning()
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
                    SessionManager.getInstance().deviceBatteryLevel,
                    SessionManager.getInstance().isDeviceCharging
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

    // TODO Navigation
    override fun navigateTo(type: Int, data: Any?) {
        log("Call $type || $data")
        when (type) {
            CONNECT -> {
                if (data is Device)
                    manager?.connectDevice(data)
            }
            DISCONNECT -> {
                if (data is Device)
                    manager?.deviceDisconnect(data)
            }
            SCAN -> {
                //stopScanning()
                //manager.stopScanning()
                if (data is Boolean)
                    startScanning(true, data)
            }
            HOME -> {
                if (data != null && data is HomeItem)
                    homeItemClicked(data)
            }
            CLEAR_HOME -> {
                popup(R.id.navigation_home)
                isHome = true
            }
            HOME_VIEW -> {
                if (data != null && data is Boolean)
                    updateBar(data)
            }
            SELECT_PROGRAM -> {
                navigate(0, R.id.navigation_select_program)
            }
            SESSION -> {
                navigate(0, R.id.navigation_channels)
                updateBar(true)
            }

            else -> {
                drawerItemClicked(type)
            }
        }
    }

    private fun updateBar(hide: Boolean) {
        if (hide) {
            bottomBarHelper.hide()
        } else {
            bottomBarHelper.show()
        }
    }

    private fun getNavOptions(): NavOptions {

        return NavOptions.Builder().setExitAnim(R.anim.exit_to_left)
            .setEnterAnim(R.anim.enter_from_right).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

    }
    // TODO Dashboard click events
    private fun homeItemClicked(item: HomeItem?) {
        item?.let {
            navigate(it.type)
        }
    }

    var lastId: Int = -1
    private fun drawerItemClicked(id: Int) {
        if (lastId == id)
            return
        lastId = id
        isHome = false
        log("drawerItemClicked $id")

        when (id) {
            R.id.nav_home -> {
                navigate(0, R.id.navigation_home)
                isHome = true
            }
            R.id.nav_ch6 -> {
                navigate(HomeItem.Type.BOOSTER)
            }
            R.id.navigation_channels -> {
                navigate(HomeItem.Type.BOOSTER)
            }
            R.id.nav_scan -> {
                //startScanning(false)
                navigate(0, R.id.navigation_devices)
            }
            R.id.nav_rxl -> {
                //startScanning(false)
                //updateMenu()
                navigate(0, R.id.navigation_reflex)

            }

            R.id.nav_test3 -> {
                //startScanning(false)
                //updateMenu()
                navigate(0, R.id.navigation_reflex2)

            }
            R.id.navigation_add_product -> {
                navigate(0, R.id.navigation_add_product)

            }
            R.id.navigation_barcode -> {
                navigate(0, R.id.navigation_barcode)

            }
            R.id.navigation_rxl_test -> {
                navigate(0, R.id.navigation_rxl_test)

            }
            R.id.nav_send -> {
                navigateTo(CLEAR_HOME, null)

            }
            R.id.nav_logout -> {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
            else -> {
                //Snackbar.make(drawer, "item clicked " + it.itemId, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun bottomBarClicked(position: Int) {
        //Toasty.warning(this@MainActivity, "click $position").show()
        lastId = position
        when (position) {
            1 -> {
                navigateTo(CLEAR_HOME, null)
            }
            2 -> {

            }
            3 -> {

            }
            4 -> {

            }
        }
    }

    private fun navigate(type: HomeItem.Type) {
        lastId = -1
        when (type) {
            HomeItem.Type.HEART -> {
                navigate(R.id.action_navigation_home_to_navigation_heart_rate, 0)
                //navigateFragment(R.id.navigation_heart_rate)
            }
            HomeItem.Type.WEIGHT -> {
                navigate(R.id.action_navigation_home_to_navigation_weight, 0)
                // navigateFragment(R.id.navigation_weight)
            }
            HomeItem.Type.ADD -> {
                navigate(
                    R.id.action_navigation_home_to_navigation_add_product,
                    0
                )
                //navigateFragment(R.id.navigation_add_product)
            }
            HomeItem.Type.CALENDAR -> {
                navigate(
                    R.id.action_navigation_home_to_navigation_calendar,
                    0
                )
                //navigateFragment(R.id.navigation_calendar)
            }
            HomeItem.Type.CALORIES -> {
                navigate(R.id.action_navigation_home_to_navigation_calories, 0)
                //navigateFragment(R.id.navigation_calories)
            }

            HomeItem.Type.BOOSTER -> {
                //navController.navigate(R.id.action_navigation_home_pop)
                //navController.navigate(R.id.navigation_home)
                navigate(
                    R.id.action_navigation_home_to_navigation_scan,
                    R.id.navigation_devices
                )
                // navigate(0, R.id.navigation_devices)
                //navigateFragment(R.id.navigation_calories)
            }
            HomeItem.Type.SCHEDULE -> {
                navigate(
                    R.id.action_navigation_home_to_schedule,
                    R.id.navigation_select_program
                )
                //navigateFragment(R.id.navigation_schedule)
            }

            HomeItem.Type.PROGRAMS -> {
                //  navigateFragment(R.id.navigation_program)
            }

            HomeItem.Type.RXL_TEST -> {
                drawerItemClicked(R.id.navigation_rxl_test)
            }

            else -> {
                Toasty.warning(this, "ItemClicked - $type").show()
            }
        }
    }

    private fun navigate(actionId: Int, fragmentId: Int, args: Bundle? = null) {
        try {
            if (actionId != 0) {
                val action = navController.currentDestination?.getAction(actionId)
                    ?: navController.graph.getAction(actionId)
                if (action != null && navController.currentDestination?.id != action.destinationId) {
                    navController.navigate(actionId, args, getNavOptions(), null)
                    return
                }
            }
            if (fragmentId != 0 && fragmentId != navController.currentDestination?.id)
                navController.navigate(fragmentId, args, getNavOptions(), null)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //IllegalAccessException when action id not match in fragment
            try {
                Toasty.info(this, R.string.error_occurred, Toasty.LENGTH_SHORT, false).show()
            } catch (ex2: java.lang.Exception) {
                ex2.printStackTrace()
            }
        }
    }

    private fun popup(fragmentId: Int, args: Bundle? = null) {
        navController.popBackStack(fragmentId, false)
        lastId = -1
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Toasty.info(this, "drawer clicked").show()
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if(item.itemId == R.id.home){

        }
        log("onOptionsItemSelected $item")
        return super.onOptionsItemSelected(item)
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

    val TIME_INTERVAL = 2000
    var mBackPressed: Long = 0;

    var isHome = false
    override fun onNavigateUp(): Boolean {
        log("onNavigateUp")
        return super.onNavigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        log("onSupportNavigateUp")
        return super.onSupportNavigateUp()
    }


    override fun onBackPressed() {
        log("onBackPressed")
        if (drawerLayout?.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        val base =
            supportFragmentManager?.primaryNavigationFragment?.childFragmentManager?.fragments?.get(
                0
            )
        if (base is BaseFragment) {
            if (!base.onBackPressed())
                return
        }
        Navigation.findNavController(this, R.id.nav_host_fragment)
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()) {
            lastId = -1
            return
        }
//        if (navController.popBackStack(R.id.navigation_home, true))
//            return;
        isHome = true
        if (isHome) {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed()
                return
            }
            Toasty.info(this, "Tap back again to exit", Toast.LENGTH_SHORT, false).show()
            mBackPressed = System.currentTimeMillis();
            return
        }
        //navigateFragment(R.id.nav_home)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (::drawerToggle.isInitialized)
            drawerToggle.onConfigurationChanged(newConfig)
    }

}
