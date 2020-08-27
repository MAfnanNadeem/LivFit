/*
 *  Created by Sumeet Kumar on 1/14/20 4:45 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/14/20 4:43 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main_cordinator.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.database.Database
import life.mibo.android.events.EventBusEvent
import life.mibo.android.models.ScanComplete
import life.mibo.android.models.base.FirebaseTokenPost
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.login.Member
import life.mibo.android.models.rxl.RxlProgram
import life.mibo.android.ui.base.*
import life.mibo.android.ui.body_measure.MeasurementFragment
import life.mibo.android.ui.body_measure.MeasurementFragmentDialog
import life.mibo.android.ui.ch6.Channel6Fragment
import life.mibo.android.ui.devices.DeviceScanFragment
import life.mibo.android.ui.home.HomeItem
import life.mibo.android.ui.login.LoginActivity
import life.mibo.android.ui.main.Navigator.Companion.CLEAR_HOME
import life.mibo.android.ui.main.Navigator.Companion.CONNECT
import life.mibo.android.ui.main.Navigator.Companion.DISCONNECT
import life.mibo.android.ui.main.Navigator.Companion.HOME
import life.mibo.android.ui.main.Navigator.Companion.HOME_DRAWER
import life.mibo.android.ui.main.Navigator.Companion.HOME_VIEW
import life.mibo.android.ui.main.Navigator.Companion.LOGOUT
import life.mibo.android.ui.main.Navigator.Companion.RXL_COURSE_CREATE
import life.mibo.android.ui.main.Navigator.Companion.RXL_COURSE_SELECT
import life.mibo.android.ui.main.Navigator.Companion.RXL_DETAILS
import life.mibo.android.ui.main.Navigator.Companion.RXL_EXERCISE
import life.mibo.android.ui.main.Navigator.Companion.RXL_HOME
import life.mibo.android.ui.main.Navigator.Companion.RXL_QUICKPLAY_DETAILS
import life.mibo.android.ui.main.Navigator.Companion.RXL_TABS
import life.mibo.android.ui.main.Navigator.Companion.RXL_TABS_2
import life.mibo.android.ui.main.Navigator.Companion.SCAN
import life.mibo.android.ui.main.Navigator.Companion.SELECT_MUSCLES
import life.mibo.android.ui.main.Navigator.Companion.SELECT_PROGRAM
import life.mibo.android.ui.main.Navigator.Companion.SELECT_SUITS
import life.mibo.android.ui.main.Navigator.Companion.SESSION
import life.mibo.android.ui.main.Navigator.Companion.SESSION_POP
import life.mibo.android.ui.rxl.adapter.ReflexModel
import life.mibo.android.ui.rxl.create.ReflexCourseCreateFragment
import life.mibo.android.ui.rxl.impl.CreateCourseAdapter
import life.mibo.android.utils.Constants
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.AlarmManager
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.constants.Config.*
import life.mibo.hardware.core.DataParser
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.*
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceConstants.*
import life.mibo.hardware.models.ScaleData
import life.mibo.hardware.models.UserSession
import life.mibo.hardware.network.CommunicationListener
import life.mibo.views.CircleImageView
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private var navigation: NavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main_cordinator)

        if (savedInstanceState == null) {
            val toolbar: Toolbar? = findViewById(R.id.toolbar)
            //toolbar?.navigationIcon?.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            setSupportActionBar(toolbar)
            drawerLayout = findViewById(R.id.drawer_layout)
            navigation = findViewById(R.id.nav_view)
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

            setNavigationView()
            //setBottomView()
//        drawer.setupWithNavController(navController)

            //getScanned()
            commHandler = CommHandler(this)
            //checkPermissions()
            //startManager()
            commHandler.register()

            setBottomView()
            //setBottomBar()

            log("OnCreate end")
        } else {
            log("OnCreate savedInstanceState end")
        }

        val i = intent?.getIntExtra("from_user_int", 5) ?: 1
        if (i != 7) {
            finish()
        }

        checkLocationPermission()
        registerNetworkMonitor()


    }


//    private fun setBottomBar() {
//        bottomBarHelper.register(item1, item2, item3, item4)
//        bottomBarHelper.listener = object : ItemClickListener<Any> {
//            override fun onItemClicked(item: Any?, position: Int) {
//                bottomBarClicked(position)
//            }
//        }
//
//        bottomBarHelper.bind(bottom_bar)
//    }

    fun getNavigation(): NavigationView {
        if (navigation == null)
            navigation = findViewById(R.id.nav_view)
        return navigation!!
    }

    private var isMember = true
    private fun setNavigationView() {
        getNavigation().setNavigationItemSelectedListener {
            drawerItemClicked(it.itemId)
            if (::drawerLayout.isInitialized)
                drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        val member = Prefs.get(this).member ?: return
        member_ = member
        //drawer_user_email?.text = member.imageThumbnail
        navigation!!.getHeaderView(0).findViewById<TextView?>(R.id.drawer_user_name)?.text =
            "${member.firstName} ${member.lastName}"
//        /drawer.getHeaderView(0).findViewById<TextView?>(R.id.drawer_user_email)?.text = "${member.email}"
        navigation!!.getHeaderView(0).findViewById<TextView?>(R.id.drawer_user_email)?.text =
            Prefs.get(this@MainActivity).get("user_email")
        navigation?.setCheckedItem(R.id.nav_home)

        isMember = member.isMember()
        if (isMember)
            navigation!!.getHeaderView(0)
                .findViewById<View?>(R.id.drawer_user_trainer)?.visibility =
                View.INVISIBLE
        else {
            val tv = navigation!!.getHeaderView(0)
                .findViewById<TextView?>(R.id.drawer_user_trainer)
            tv?.visibility = View.VISIBLE
            tv?.text = member.type?.capitalize()
        }
        loadImage(
            navigation!!.getHeaderView(0).findViewById<CircleImageView?>(R.id.drawer_user_image),
            R.drawable.ic_user_test, member.profileImg, member.isMale()
        )

        //navigation.s
//        if (DEBUG) {
//            navigation.menu.clear()
//            navigation.inflateMenu(R.menu.activity_drawer_drawer_release)
//        }
    }

    private var member_: Member? = null
    private fun getMember(): Member? {
        if (member_ == null)
            member_ = Prefs.get(this).member
        return member_
    }

//    fun setBottomView() {
//        ib_item_1?.setOnClickListener {
//            navigate(HomeItem.Type.PROFILE, null)
//        }
//        ib_item_2?.setOnClickListener {
//            navigateTo(Navigator.BODY_MEASURE_SUMMARY, null)
//            //drawerItemClicked(R.id.navigation_bio_summary)
//        }
//        ib_item_3?.setOnClickListener {
//            //drawerItemClicked(R.id.navigation_account)
//        }
//        ib_item_4?.setOnClickListener {
//            //navigate(navigation_search_trainer)
//            navigate(
//                0, R.id.navigation_search_trainer
//            )
//        }
//        tv_item_fab?.setOnClickListener {
//            if (childPlusClicked()) {
//                //popup(R.id.navigation_home)
//                //navigation?.setCheckedItem(R.id.nav_home)
//                //isHome = true
//            }
//        }
//    }

    fun updateFabIcon(type: Int, bundle: Bundle) {
//        val icon = bundle.getInt("fab_icon", 0)
//        val visible = bundle.getBoolean("fab_visible", true)
//        if (visible) {
//            tv_item_fab?.visibility = View.VISIBLE
//            if (icon != 0)
//                tv_item_fab?.setImageResource(icon)
//
//        } else {
//            tv_item_fab?.visibility = View.GONE
//        }
    }

    fun updateFabIcon(type: Int) {
//        if (type != 0) {
//            when (type) {
//                100 -> tv_item_fab?.setImageResource(R.drawable.ic_home_black_24dp)
//                101 -> tv_item_fab?.setImageResource(R.drawable.ic_add_black_24dp)
//                200 -> {
//                    tv_item_fab?.visibility = View.GONE
//                    bottom_bar?.visibility = View.GONE
//                }
//                0, 300 -> {
//                    tv_item_fab?.visibility = View.VISIBLE
//                    bottom_bar?.visibility = View.VISIBLE
//                }
//            }
//        }
    }

    private fun loadImage(
        iv: ImageView?,
        defaultImage: Int,
        url: String?,
        male: Boolean
    ) {

        Utils.loadImage(iv, url, male)
//        if (url == null) {
//            if (iv != null)
//                Glide.with(this).load(defaultImage).error(defaultImage).fallback(defaultImage)
//                    .into(iv)
//            return
//        }
//        url?.let {
//            if (iv != null)
//                Glide.with(this).load(it).error(defaultImage).fallback(defaultImage).into(iv)
//        }
//        Maybe.fromCallable {
//            log("loadImage fromCallable")
//            var bitmap: Bitmap? = null
//            val img = Prefs.get(this@MainActivity).member?.imageThumbnail
//            log("loadImage size: ${img?.length}")
//            bitmap = if (!img.isNullOrEmpty())
//                Utils.base64ToBitmap(img)
//            else
//                BitmapFactory.decodeResource(resources, defaultImage)
//            //   bitmap = Utils.base64ToBitmap(Utils.testUserImage())
//            bitmap
//        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
//            log("loadImage doOnSuccess $it")
//            if (it != null)
//                iv?.setImageBitmap(it)
//            else
//                iv?.setImageResource(defaultImage)
//        }.doOnError {
//
//        }.subscribe()
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
        drawerToggle.drawerArrowDrawable?.color = Color.DKGRAY


        //drawerToggle.isDrawerIndicatorEnabled = true
        //setupActionBarWithNavController(navController, drawer)
//        NavigationUI.setupActionBarWithNavController(
//            this, navController, AppBarConfiguration(navController.graph, drawerLayout)
//        )

        //too.setupWithNavController(navController, config)
        drawer.post {
            drawerToggle.syncState()
        }



        drawerToggle.setToolbarNavigationClickListener {
            log("onBack setToolbarNavigationClickListener")
            if (!drawer.isDrawerOpen(GravityCompat.START))
                drawer.openDrawer(GravityCompat.START)
        }

//        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
//            override fun onDrawerStateChanged(newState: Int) {
//                log("onBack onDrawerStateChanged")
//            }
//
//            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//                log("onBack onDrawerSlide")
//            }
//
//            override fun onDrawerClosed(drawerView: View) {
//                log("onBack onDrawerClosed")
//            }
//
//            override fun onDrawerOpened(drawerView: View) {
//                log("onBack onDrawerOpened")
//            }
//
//        });

        drawer.addDrawerListener(drawerToggle)
        drawerToggle.isDrawerIndicatorEnabled = true;
        toolbar!!.setupWithNavController(navController, drawer)
        drawerToggle.syncState();

        toolbar.setNavigationOnClickListener {
            log("onBack setNavigationOnClickListener")
            if (childBackPressed()) {
                log("setNavigationOnClickListener")
                NavigationUI.navigateUp(
                    navController,
                    AppBarConfiguration(navController.graph, drawerLayout)
                )
                // navController.navigateUp()
            }
        }
    }

//    fun setupWithNavController(
//        toolbar: Toolbar,
//        navController: NavController,
//        configuration: AppBarConfiguration
//    ) {
//        navController.addOnDestinationChangedListener(
//            ToolbarOnDestinationChangedListener(toolbar, configuration)
//        )
//        toolbar.setNavigationOnClickListener {
//            NavigationUI.navigateUp(
//                navController,
//                configuration
//            )
//        }
//    }


    @SuppressLint("CheckResult")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (::drawerToggle.isInitialized)
            drawerToggle.syncState()
        log("PostCreate")
        saveFirebaseToken(Prefs.get(this).member)
        // Single.timer(1, TimeUnit.SECONDS)
//        Single.just(R.id.nav_home).delay(1, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread()).subscribe { DialogListener ->
//                //EventBus.getDefault().postSticky(NotifyEvent(DialogListener, null))
//                //navigateFragment(DialogListener)
//            }
    }

    private fun drawerLockMode(lock: Boolean) {
        if (lock)
            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        else
            drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
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

//    private fun updateMenu() {
//        // bottom_nav_view.menu.clear()
//        try {
//            Thread.sleep(100)
//        } catch (e: Exception) {
//        }
//        if (bottom_nav_view.maxItemCount < 5)
//            bottom_nav_view?.menu?.add(
//                Menu.NONE,
//                R.id.navigation_devices,
//                Menu.NONE,
//                "Home"
//            )?.setIcon(R.drawable.ic_home_black_24dp);
//        //bottom_nav_view?.inflateMenu(R.menu.bottom_nav_menu_rxl)
//        //val menu = bottom_nav_view.menu as BottomNavigationMenu
//
////        appBarConfiguration = AppBarConfiguration(
////            setOf(
////                R.id.navigation_discover,
////                R.id.navigation_create,
////                R.id.navigation_analytic,
////                R.id.navigation_more
////            )
////        )
////        setupActionBarWithNavController(navController, appBarConfiguration)
////        bottom_nav_view.setupWithNavController(navController)
//    }

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )


    private fun checkLocationPermission() {
        var ask = false
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ask = true
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ask = true
        }


        if (ask) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.location_permission_title))
                .setMessage(getString(R.string.location_permission_msg))

            // builder.setNegativeButton(R.string.no_text) { dialog, which -> }
            builder.setNeutralButton(R.string.no_text) { dialog, which -> }
            builder.setPositiveButton(R.string.yes_text) { dialog, which ->
                PermissionHelper.requestPermission(
                    this@MainActivity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {

                }
            }

            val dialog = builder.create()
            dialog.show()
        }

    }

    private fun startScanningView(isRxl: Boolean, type: Int) {

        PermissionHelper.requestPermission(this@MainActivity, permissions) {
            startManager()
            val bundle = Bundle()
            bundle.putBoolean("is_rxl", isRxl)
            bundle.putInt("is_search_type", type)
            navigate(
                R.id.action_navigation_home_to_navigation_scan,
                R.id.navigation_devices, bundle
            )
        }
    }

    var manager: CommunicationManager? = null
    private fun startManager() {
        log("startManager..... ")

        try {
            SessionManager.initUser()

            val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifi?.isWifiEnabled = true
            val lock = wifi.createMulticastLock("MIBO Cast")
            lock.setReferenceCounted(true)
            lock?.acquire()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }



        manager = CommunicationManager.getInstance(object : CommunicationListener {


            override fun onDeviceDisconnect(uid: String?) {
                //Toasty.warning(this@MainActivity, "Device Disconnected by user $uid").show()
                EventBus.getDefault().post(RemoveConnectionStatus(uid))
            }

            override fun onConnect(name: String?, status: Int) {
               // Toasty.warning(this@MainActivity, "Device Connected! $name : $status").show()
                boosterAlarm(200, name)
            }

            override fun onDisconnect(failed: Boolean, name: String?, status: Int, error: String?) {
                log("onDisconnect $failed :: $status :: $error")
                if (failed) {
                    boosterAlarm(status, "")
                    if (status > 1000) {
                        // broadcast message
                        if (error?.toLowerCase()?.contains("gatt exception") == true)
                            boosterAlarm(300, "")
                        else if (error?.toLowerCase()?.contains("not connect") == true)
                            Toasty.snackbar(nav_view, "$status : $error")
                    } else {
                        Toasty.error(this@MainActivity, error ?: "Failed to connect $status").show()
                    }
                } else {
                    boosterAlarm(100, "")
                    //  Toasty.warning(this@MainActivity, "Device disconnected $status").show()
                }

            }

            override fun onCommandReceived(code: Int, command: ByteArray, uid: String?, type: Int) {
                if (type == 2) {
                    parseRxtCommands(code, command, uid)
                } else {
                    parseCommands(code, command, uid)
                }
            }

            override fun onDeviceDiscoveredEvent(device: Device?) {
                log("onDeviceDiscoveredEvent Device $device")
                EventBus.getDefault().post(NewDeviceDiscoveredEvent(device))
            }

            override fun onBluetoothDeviceFound(result: ScanResult?) {
                //log("onBluetoothDeviceFound " + result)
                // EventBus.getDefault()
                //   .post(NewDeviceDiscoveredEvent(result?.device))
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

            override fun HrEvent(hr: ByteArray, uid: String, property: Int) {
                log("HrEvent $hr : $uid")
                parseHrCommands(hr, uid, property)
            }


            override fun DeviceStatusEvent(uid: String?) {
                log("DeviceStatusEvent $uid")
            }

            override fun ChangeColorEvent(d: Device?, uid: String?) {
                log("DeviceStatusEvent $d : $uid")
            }

            override fun GetMainLevelEvent(mainLevel: Int, uid: String?) {
                log("GetMainLevelEvent $mainLevel : $uid")
                EventBus.getDefault().postSticky(GetMainLevelEvent(mainLevel, uid, "1"))
                EventBus.getDefault().postSticky(SendMainLevelEvent(1, uid));

            }

            override fun onScale(weight: Float, data: ScaleData?, code: Int, other: Any?) {
                EventBus.getDefault().postSticky(ScaleDataEvent(data, weight))
                EventBus.getDefault().postSticky(NewConnectionStatus(""))
            }

            override fun GetLevelsEvent(uid: String?) {
                log("GetLevelsEvent  $uid")
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid))
            }

            override fun onStatus(
                time: Int,
                action: Int,
                pause: Int,
                currentBlock: Int,
                currentProgram: Int,
                uid: String?
            ) {
                log("onStatus  $uid")
                // don't using, parse command whenever needed, don't store in memory
                EventBus.getDefault().postSticky(
                    ProgramStatusEvent(
                        time,
                        action,
                        pause,
                        currentBlock,
                        currentProgram,
                        uid
                    )
                )

            }

            override fun onStatus(command: ByteArray?, uid: String?) {
                EventBus.getDefault().postSticky(ProgramStatusEvent(command, uid))
            }

            override fun DevicePlayPauseEvent(uid: String?) {
                log("GetLevelsEvent  $uid")
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid))
            }

        })

        log("getScanning finished")
    }

    fun boosterAlarm(code: Int, name: String?) {
        log("boosterAlarm $code")
        try {
            val frg =
                supportFragmentManager?.primaryNavigationFragment?.childFragmentManager?.fragments?.get(
                    0
                )
            log("boosterAlarm frg = $frg")
            if (frg is Channel6Fragment) {
                log("boosterAlarm frg = Channel6Fragment")
                return frg.onBoosterAlarm(code)
            }

            if (frg is DeviceScanFragment) {
                log("boosterAlarm frg = Channel6Fragment")
                return frg.onDeviceEvent(DeviceStatusEvent(name))
            }

            if (frg is MeasurementFragment) {
               EventBus.getDefault().postSticky(NewConnectionStatus(""))
            }
        } catch (e: java.lang.Exception) {
            runOnUiThread {
                Toasty.grey(this, getString(R.string.error_occurred)).show()
            }
            e.printStackTrace()
        }

    }

    private fun parseHrCommands(
        command: ByteArray,
        uid: String?,
        property: Int
    ) {
        //        for (Device d : mDiscoveredDevices.values()) {
//            if (d.getUid().equals(uid)) {
//                if (d.getStatusConnected() != DEVICE_WAITING && d.getStatusConnected() != DEVICE_CONNECTED) {
////                    if (d.getStatusConnected() == DEVICE_DISCONNECTED) {
////                        EventBus.getDefault().postSticky(new ChangeColorEvent(d, d.getUid()));
////                    }
//                    d.setStatusConnected(DEVICE_CONNECTED);
//                    SessionManager.getInstance().getUserSession().setDeviceStatus(uid, DEVICE_CONNECTED);
//
//                    if (listener != null)
//                        listener.onConnectionStatus(uid);
//
//                    //EventBus.getDefault().postSticky(new onConnectionStatus(uid));
//
//                }
//            }
//        }

        //SessionManager.getInstance().userSession.setDeviceStatus(uid, DEVICE_CONNECTED);

        if (command.size > 1) {
            log("HeartRate HR >> $command")

            EventBus.getDefault().post(
                HeartRateEvent(
                    uid,
                    life.mibo.hardware.core.Utils.getHeartRate(command, property)
                )
            )
            // HR
        } else if (command.size == 1) {
            SessionManager.getInstance().userSession.setDeviceStatus(uid, DEVICE_CONNECTED);
            log("bleHrConsumer Battery >> " + command[0])
            // Battery
            val device =
                SessionManager.getInstance().userSession
                    .getDevice(uid)
            if (device != null) {
                device.batteryLevel = command[0].toInt()
                device.statusConnected = DEVICE_CONNECTED
            }
            log("bleHrConsumer device $device")
            EventBus.getDefault().postSticky(DeviceStatusEvent(device))
        }
    }

    //[77, 66, 82, 88, 76, 0, -64, 2, 26, -116, -43, -65]
    private fun parseCommands(code: Int, command: ByteArray, uid: String?) {
        logw("parseCommands $code : data " + command.contentToString())
        when (code) {
            INDICATE -> {
                EventBus.getDefault().postSticky(IndicationEvent("", command))
                return
            }

            COMMAND_DEVICE_STATUS_RESPONSE -> {
                // code -126
                logw("parseCommands COMMAND_DEVICE_STATUS_RESPONSE $uid")
                if (!SessionManager.getInstance().userSession.isScanning)
                    return
                //val d = SessionManager.getInstance().userSession.booster
                val list = SessionManager.getInstance().userSession.devices
                for (d in list) {
                    if (d != null && d.uid == uid) {
                        logw(
                            "COMMAND_DEVICE_STATUS_RESPONSE UID MATCHED battery " + DataParser.getStatusBattery(
                                command
                            )
                        )
                        d.batteryLevel = DataParser.getStatusBattery(command)
                        d.signalLevel = DataParser.getStatusSignal(command)
                        //SessionManager.getInstance().userSession.booster.batteryLevel =
                        //    DataParser.getStatusBattery(command)
                        // SessionManager.getInstance().userSession.booster.signalLevel =
                        //      DataParser.getStatusSignal(command)
                        // updated device status/line
                        EventBus.getDefault().postSticky(DeviceStatusEvent(d))
                        if (d.statusConnected != DEVICE_WAITING && d.statusConnected != DEVICE_CONNECTED) {
                            if (d.statusConnected == DEVICE_DISCONNECTED) {
                                // todo why fernando sending again color to booster?
                                // EventBus.getDefault().postSticky(ChangeColorEvent(d, d.uid))
                                logw("COMMAND_DEVICE_STATUS_RESPONSE statusConnected DEVICE_DISCONNECTED")
                            }
                            d.statusConnected = DEVICE_CONNECTED
                            logw("COMMAND_DEVICE_STATUS_RESPONSE statusConnected DEVICE_CONNECTED_")
                            //d.statusConnected = DEVICE_CONNECTED
                            //SessionManager.getInstance().userSession.booster.statusConnected = DEVICE_CONNECTED

                            EventBus.getDefault().postSticky(NewConnectionStatus(uid))
                        } else {
                            d.statusConnected = DEVICE_CONNECTED
                            // SessionManager.getInstance().userSession.booster.statusConnected = DEVICE_CONNECTED
                            logw("COMMAND_DEVICE_STATUS_RESPONSE statusConnected DEVICE_CONNECTED")
                        }
                        if (SessionManager.getInstance().userSession.currentSessionStatus == 1 || SessionManager.getInstance().userSession.currentSessionStatus == 2) {
                            if (SessionManager.getInstance().userSession.setDeviceAlarm(
                                    uid,
                                    command
                                )
                            ) {

                                AlarmManager.getInstance().alarms.AddDeviceChannelAlarm(
                                    SessionManager.getInstance().userSession.getDeviceAlarm(uid),
                                    d.uid
                                )

                                EventBus.getDefault().postSticky(NewAlarmEvent())
                            }
                            logw("COMMAND_DEVICE_STATUS_RESPONSE device is booster, session is 1 or 2")
                            checkDeviceStatus(
                                SessionManager.getInstance().userSession,
                                DataParser.getStatusFlags(command),
                                uid
                            )
                        }
                        // TODO check later functionality remaining
                        //SessionManager.getInstance().userSession.checkDeviceStatus(DataParser.getStatusFlags(command), uid)
//                        if (SessionManager.getInstance().userSession.currentSessionStatus == 2 || SessionManager.getInstance().userSession.currentSessionStatus == 1)
//                            checkDeviceStatus(
//                                SessionManager.getInstance().userSession,
//                                DataParser.getStatusFlags(command),
//                                uid
//                            )
                        logw(
                            "signal:" + DataParser.getStatusSignal(command) + " bat:" + DataParser.getStatusBattery(
                                command
                            )
                        )
                        break
                    } else {
                        logw("COMMAND_DEVICE_STATUS_RESPONSE UID NOT MATCHED ${d.uid} == $uid")
                    }
                }

            }
            COMMAND_SET_MAIN_LEVEL_RESPONSE -> {
                logw("parseCommands COMMAND_SET_MAIN_LEVEL_RESPONSE")
                EventBus.getDefault()
                    .postSticky(GetMainLevelEvent(DataParser.getMainLevel(command), uid, ""))
                SessionManager.getInstance().userSession.user.mainLevel =
                    DataParser.getMainLevel(command)
                //EventBus.getDefault().postSticky(new SendMainLevelEvent(1,uid));
            }
            COMMAND_SET_CHANNELS_LEVELS_RESPONSE -> {
                logw("parseCommands COMMAND_SET_CHANNELS_LEVELS_RESPONSE")
                EventBus.getDefault().postSticky(GetLevelsEvent(uid))
            }
            COMMAND_START_CURRENT_CYCLE_RESPONSE -> {
                logw("parseCommands COMMAND_ASYNC_START_RESPONSE")
                SessionManager.getInstance().userSession.booster.isStarted = true
                //EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid, 3))
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid, 1))
            }
            COMMAND_PAUSE_CURRENT_CYCLE_RESPONSE -> {
                logw("parseCommands COMMAND_ASYNC_PAUSE_RESPONSE")
                SessionManager.getInstance().userSession.booster.isStarted = false
                //SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).setIsStarted(false);
                // EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid, 4))
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid, 2))
            }
            ASYNC_PROGRAM_STATUS -> {
                // RXL_TAP_EVENT
                logw("parseCommands ASYNC_PROGRAM_STATUS ${command.contentToString()}")
                if (SessionManager.getInstance().userSession.isBooster) {
                    SessionManager.getInstance().userSession.booster.deviceSessionTimer =
                        DataParser.getProgramStatusTime(command)
                    EventBus.getDefault().postSticky(ProgramStatusEvent(command, uid))
                    SessionManager.getInstance().userSession.booster?.deviceSessionTimer =
                        DataParser.getProgramStatusTime(command)
                } else if (SessionManager.getInstance().userSession.isRxl) {
                    logw("parseCommands RXL TAP COMMANDS")

                    EventBus.getDefault().postSticky(RxlStatusEvent(command, uid))

                }
            }
            COMMAND_ASYNC_SET_MAIN_LEVEL -> {
                logw("parseCommands COMMAND_ASYNC_SET_MAIN_LEVEL")
                EventBus.getDefault()
                    .postSticky(GetMainLevelEvent(DataParser.getMainLevelAsync(command), uid, ""));
                SessionManager.getInstance().userSession.user.mainLevel =
                    DataParser.getMainLevelAsync(command)
            }
            COMMAND_ASYNC_PAUSE -> {
                logw("parseCommands COMMAND_ASYNC_PAUSE")

                SessionManager.getInstance().userSession.booster.isStarted = false
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid, 2));
            }
            COMMAND_ASYNC_START -> {
                logw("parseCommands COMMAND_ASYNC_START")

                SessionManager.getInstance().userSession.booster.isStarted = true
                EventBus.getDefault().postSticky(DevicePlayPauseEvent(uid, 1))

            }
            else -> {
                if (DEBUG) {
                    logw("parseCommandsExtra")
                    parseCommandsExtra(code, command, uid)
                }
            }
        }
    }

    private fun parseCommandsExtra(code: Int, command: ByteArray, uid: String?) {
        when (code) {
            COMMAND_PING_RESPONSE -> {
                logw("parseCommandsExtra COMMAND_PING_RESPONSE")
            }
            COMMAND_GET_FIRMWARE_REVISION -> {
                logw("parseCommandsExtra COMMAND_FIRMWARE_GET_REVISION")
            }
            COMMAND_FIRMWARE_REVISION_RESPONSE -> {
                logw("parseCommandsExtra COMMAND_FIRMWARE_REVISION_RESPONSE")
            }
            COMMAND_SET_DEVICE_COLOR_RESPONSE -> {
                logw("parseCommandsExtra COMMAND_SET_DEVICE_COLOR_RESPONSE")
                //color response code -125
            }
            COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE -> {
                logw("parseCommandsExtra COMMAND_SET_COMMON_STIMULATION_PARAMETERS_RESPONSE")
            }
            COMMAND_RESET_CURRENT_CYCLE_RESPONSE -> {
                logw("parseCommandsExtra COMMAND_RESET_CURRENT_CYCLE_RESPONSE")
            }
            RXL_TAP_EVENT -> {
                logw("parseCommandsExtra RXL_TAP_EVENT")
            }
            else -> {
                logw("parseCommandsExtra DEFAULTS ${command?.contentToString()}")
            }
        }
    }

    @Synchronized
    private fun parseRxtCommands(code: Int, command: ByteArray, uid: String?) {
        logw("parseRxtCommands $code : data " + command.contentToString())
        when (code) {
            INDICATE -> {
                EventBus.getDefault().postSticky(IndicationEvent("", command))
                return
            }
            COMMAND_PING_RESPONSE -> {
                return
            }

            COMMAND_SET_DEVICE_COLOR_RESPONSE -> {
                EventBus.getDefault().postSticky(RxtStatusEvent(1, command, uid))
                return
            }
            RXL_TAP_EVENT -> {
                logw("parseCommandsRxt... TAP_EVENT")
                EventBus.getDefault().postSticky(RxtStatusEvent(2, command, uid))
                return
            }
            RXT_ID_CONFIG_RESPONSE -> {
                logw("parseCommandsRxt... TAP_EVENT")
                EventBus.getDefault().postSticky(RxtStatusEvent(3, command, uid))
                return
            }
            RXT_TILE_CONFIG -> {
                logw("parseCommandsRxt... TAP_EVENT")
                EventBus.getDefault().postSticky(RxtTileConfigEvent(command, uid))
                return
            }
            COMMAND_DEVICE_STATUS_RESPONSE -> {
                logw("parseCommandsRxt... DEVICE_STATUS_RESPONSE $uid")

                val list = SessionManager.getInstance().userSession.devices
                for (d in list) {
                    if (d.uid == uid) {
                        if (d.tiles == 0) {
                            CommunicationManager.log("parseCommandsRxt... DEVICE_STATUS_RESPONSE Tiles were zero")

                            //var statusConnected = 0

//                            if (d.statusConnected != DEVICE_WAITING && d.statusConnected != DEVICE_CONNECTED) {
//                               // if (d.statusConnected == DEVICE_DISCONNECTED) {
//                                    //EventBus.getDefault().postSticky(new ChangeColorEvent(d, d.getUid()));
//                             //   }
//                                d.statusConnected = DEVICE_CONNECTED
//                                statusConnected = DEVICE_CONNECTED
//                                EventBus.getDefault().postSticky(NewConnectionStatus(uid))
//                            } else {
//                                d.statusConnected = DEVICE_CONNECTED
//                                statusConnected = DEVICE_CONNECTED
//                                //SessionManager.getInstance().session.getRegisteredDevicebyUid(uid).statusConnected = DEVICE_CONNECTED
//                                EventBus.getDefault().postSticky(NewConnectionStatus(uid))
//                            }
                            // var statusConnected = DEVICE_CONNECTED
                            d.statusConnected = DEVICE_CONNECTED
                            d.tiles = DataParser.getStatusBattery(command)
                            d.signalLevel = DataParser.getStatusSignal(command)

//                            SessionManager.getInstance().userSession.updateRegisteredDevice(
//                                uid,
//                                DEVICE_CONNECTED,
//                                DataParser.getStatusBattery(command),
//                                DataParser.getStatusSignal(command)
//                            )


                            EventBus.getDefault().postSticky(NewConnectionStatus(d.uid))

                            //SessionManager.getInstance().session.getRegisteredDevicebyUid(uid).tiles = DataParser.getStatusBattery(command)

                            //SessionManager.getInstance().session.getRegisteredDevicebyUid(uid).signalLevel = DataParser.getStatusSignal(command)
                            //EventBus.getDefault().postSticky(DeviceStatusEvent(d.uid))
                        } else {
                            try {
                                if (SessionManager.getInstance().userSession.isScanning) {
                                    EventBus.getDefault().postSticky(DeviceStatusEvent(d))
                                }
                            } catch (e: Exception) {
                            }
                        }
                        return
                    }
                }

                return
            }
            else -> {
                logw("parseRxtCommands UNKNOWN $code : data " + command.contentToString())
            }
        }
    }

    private fun checkDeviceStatus(user: UserSession, status: BooleanArray, uid: String?) {
        logw("checkDeviceStatus wifi " + status[0] + " ble " + status[1] + " program " + status[2] + " runn " + status[3] + " color " + status[4])

        if (user.currentSessionStatus == 2 || user.currentSessionStatus == 1) {
            if (user.booster.uid == uid) {
                logw("checkDeviceStatus UID MATCH")
                if (!status[2]) {//check if program
                    logw("checkDeviceStatus SendProgramEvent")
                    EventBus.getDefault()
                        .postSticky(SendProgramEvent(user.currentSessionProgram, uid));
                }
                if (!status[4]) {//check if color if (listener != null)
                    logw("checkDeviceStatus ChangeColorEvent")
                    EventBus.getDefault().postSticky(ChangeColorEvent(user.booster, uid));
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
            logw("checkDeviceStatus currentSessionStatus " + user.currentSessionStatus)
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
            Single.just("").delay(MiboApplication.SCAN_TIME, TimeUnit.MILLISECONDS)
                .subscribe { i -> stopScanning() }

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
        if (data is HomeItem) {
            navigate(data.type)
            return
        }
        if (data is HomeItem.Type) {
            navigate(data)
            return
        }
        log("Call $type || $data")
        when (type) {
            CONNECT -> {
                if (data is Device)
                    manager?.connectDevice(data)
                return
            }
            DISCONNECT -> {
                if (data is Device)
                    manager?.disconnectDevice(data)
                return
            }
            SCAN -> {
                //stopScanning()
                //manager.stopScanning()
                if (data is Boolean)
                    startScanning(true, data)
                return
            }
            HOME -> {
                if (data != null && data is HomeItem)
                    homeItemClicked(data)
            }
            CLEAR_HOME -> {
                popup(R.id.navigation_home)
                //navigation?.setCheckedItem(R.id.nav_home)
                isHome = true
            }
            HOME_VIEW -> {
                if (data != null && data is Boolean)
                    updateBar(data)
            }
            HOME_DRAWER -> {
                updateDrawerHomeButton()
                return
            }
            RXL_HOME -> {
                navigate(0, R.id.navigation_rxl_home)
            }
            RXL_EXERCISE -> {
                navigate(0, R.id.navigation_reflex2)
            }
            RXL_TABS -> {
                val bundle = Bundle()
                if (data != null) {
                    if (data is ArrayList<*>) {
                        bundle.putSerializable(Constants.BUNDLE_DATA, data)
                    }
                }

                navigate(0, R.id.navigation_rxl_tabs, bundle)
            }

            RXL_TABS_2 -> {
                navigate(0, R.id.navigation_rxl_tabs2)
            }
            RXL_COURSE_SELECT -> {
                // val t = FragmentNavigator.Extras.Builder().addSharedElement(image!!, "course_icon") .addSharedElement(title!!, "course_title").build()
                navigate(0, R.id.navigation_select_rxl_course)
            }
            RXL_COURSE_CREATE -> {

                if (data is CreateCourseAdapter.Course) {
                    val args = Bundle()
                    args.putSerializable(ReflexCourseCreateFragment.DATA, data)
                    navigate(0, R.id.navigation_create_course, args, getNavOptions(), data.extras)
                    updateBar(true)
                } else if (data is RxlProgram) {
                    val args = Bundle()
                    args.putSerializable(ReflexCourseCreateFragment.DATA_PROGRAM, data)
                    navigate(0, R.id.navigation_create_course, args, getNavOptions())
                    updateBar(true)
                }
            }
            RXL_DETAILS -> {

                if (data is RxlProgram) {
                    val args = Bundle()
                    args.putSerializable(ReflexCourseCreateFragment.DATA, data)
                    navigate(0, R.id.navigation_rxl_details, args, getNavOptions())
                    updateBar(true)
                    return
                }

                if (data is ReflexModel) {
                    val args = Bundle()
                    args.putSerializable(ReflexCourseCreateFragment.DATA, data)
                    navigate(0, R.id.navigation_rxl_details, args, getNavOptions(), data.extras)
                    updateBar(true)
                }
            }
            SELECT_PROGRAM -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_select_program, bundle)
            }

            SELECT_MUSCLES -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_select_muscles, bundle)
            }

            SELECT_SUITS -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_select_suit, bundle)
            }
            SESSION -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_channels, bundle)
                updateBar(true)
            }
            RXL_QUICKPLAY_DETAILS -> {
                val args = Bundle()
                if (data is RxlProgram) {
                    args.putSerializable(ReflexCourseCreateFragment.DATA, data)
                    navigate(
                        0,
                        R.id.navigation_quickplay_details,
                        args,
                        getNavOptions()
                    )
                }
            }
//            RXL_QUICKPLAY_DETAILS_PLAY -> {
//                val args = Bundle()
//                if (data is RxlProgram) {
//                    args.putSerializable(ReflexCourseCreateFragment.DATA, data)
//                }
//                navigate(0, R.id.navigation_quickplay_details_play, args)
//            }
            SESSION_POP -> {
//                var bundle: Bundle? = null
//                if (data is Bundle)
//                    bundle = data
                popup(R.id.navigation_select_program)
                //updateBar(true)
            }
            LOGOUT -> {
                logout()
                return
            }
            Navigator.BODY_MEASURE -> {
                popup(R.id.navigation_home)
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                //showMeasureDialog()

                navigate(0, R.id.navigation_measurement, bundle)
                //updateBar(true)
            }

            Navigator.BODY_MEASURE_SUMMARY -> {
                //todo check popup impact
                popup(R.id.navigation_home)
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_bio_summary, bundle)
                //updateBar(true)
            }
            Navigator.PIC_UPLOADED -> {
                try {
                    loadImage(
                        navigation!!.getHeaderView(0)
                            .findViewById<CircleImageView?>(R.id.drawer_user_image),
                        R.drawable.ic_user_test, data as String, getMember()?.isMale() ?: true
                    )
                    //loadImage()
                } catch (e: java.lang.Exception) {

                }
                return
            }
            Navigator.POST -> {
                postObservable(data)
            }

            Navigator.DRAWER_LOCK -> {
                drawerLockMode(true)
            }
            Navigator.DRAWER_UNLOCK -> {
                drawerLockMode(false)
            }

            Navigator.FAB_UPDATE -> {
                if (data is Int)
                    updateFabIcon(data)
                else if (data is Bundle)
                    updateFabIcon(0, data)

            }

            Navigator.RESCHEDULE -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_reschedule, bundle)
            }
            Navigator.SCHEDULE -> {
                trainerWebView(2)
                return
            }

            Navigator.INVOICES -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_orders, bundle)
            }
            Navigator.ORDERS -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_orders, bundle)
            }

            Navigator.PROFILE_UPDATE -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_profile_edit, bundle)
            }

            Navigator.GOOGLE_FIT -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_fitness, bundle)
            }

            Navigator.HOME_START -> {
                bottom_fab?.show()
                bottom_app_bar?.performShow()
                updateDrawerHomeButton()
            }
            Navigator.HOME_STOP -> {
                bottom_fab?.hide()
                bottom_app_bar?.performHide()
            }

            Navigator.SETTINGS_UNIT -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_settings, bundle)
            }
            Navigator.MY_SERVICES -> {
                navigate(0, R.id.navigation_my_services, null)
            }
            Navigator.MY_CLIENTS -> {
                navigate(0, R.id.navigation_my_clients, null)
            }
            Navigator.MY_SALES -> {
                navigate(0, R.id.navigation_my_sales, null)
            }
            Navigator.VIEW_MEASUREMENT -> {
                navigate(0, R.id.navigation_view_measure, null)
            }
            Navigator.VIEW_SESSIONS -> {
                navigate(0, R.id.navigation_view_session, null)
            }
            Navigator.UPDATE_DATA -> {
                navigate(0, R.id.navigation_profile_update, null)
            }
            Navigator.WEBVIEW -> {
                if (data is Bundle)
                    navigate(0, R.id.navigation_webview, data)
            }
            Navigator.RXT_SELECT_WORKOUT -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_rxt_select, bundle)
            }
            Navigator.RXT_START_WORKOUT -> {
                var bundle: Bundle? = null
                if (data is Bundle)
                    bundle = data
                navigate(0, R.id.navigation_rxt_play, bundle)
            }
            else -> {
                drawerItemClicked(type)
            }
        }
    }

    fun setBottomView() {
        ib_item_1?.setOnClickListener {
            navigate(HomeItem.Type.PROFILE)
        }
        ib_item_2?.setOnClickListener {
            if (isMember)
                navigate(HomeItem.Type.MEASURE_NEW)
            else {
                if (getMember()?.isMember() == false)
                    navigateTo(Navigator.MY_CLIENTS, null)
            }
            // navigateTo(Navigator.BODY_MEASURE_SUMMARY, null)
            //drawerItemClicked(R.id.navigation_bio_summary)
        }
        ib_item_3?.setOnClickListener {
            navigate(HomeItem.Type.MY_ACCOUNT)
            //drawerItemClicked(R.id.navigation_account)
        }
        ib_item_4?.setOnClickListener {
            //if(isMember)
            navigate(HomeItem.Type.SERVICES)
            //navigate(navigation_search_trainer)
        }
        bottom_fab?.setOnClickListener {
            // if (isMember)
            //testAnim()
            navigate(HomeItem.Type.CENTER_BUTTON)
        }

        if (!isMember) {
            ib_item_2?.setImageResource(R.drawable.ic_users_24)
            tv_item_2?.setText(R.string.clients)
        }
    }

    private fun trainerWebView(type: Int) {
        if (isMember)
            return
        navigate(
            0,
            R.id.navigation_webview,
            WebViewFragment.bundle("https://mibolivfit.club")
        )
        title = getString(R.string.mibo_world)
//        navigate(
//            R.id.action_navigation_home_to_schedule,
//            R.id.navigation_schedule
//        )
//        var bundle: Bundle? = null
//        if (data is Bundle)
//            bundle = data
//        navigate(
//            R.id.action_navigation_home_to_schedule,
//            R.id.navigation_schedule, bundle
//        )
    }

    var isFromDialog = false
    fun showMeasureDialog() {
        // val dialog = MeasurementFragmentDialog()
        MeasurementFragmentDialog(object : ItemClickListener<Any?> {
            override fun onItemClicked(item: Any?, position: Int) {
                navigateTo(Navigator.BODY_MEASURE_SUMMARY, null)
            }

        }).show(supportFragmentManager, "MeasurementFragmentDialog")
    }

    private fun postObservable(data: Any?) {
        Single.just("just").delay(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).doOnSuccess {
            EventBus.getDefault().postSticky(EventBusEvent(1, data))
        }.subscribe()
    }

    private fun updateBar(hide: Boolean) {
//        if (hide) {
//            bottomBarHelper.hide()
//        } else {
//            bottomBarHelper.show()
//        }
    }

    private fun getNavOptions(): NavOptions {

        return NavOptions.Builder().setExitAnim(R.anim.exit_to_left)
            .setEnterAnim(R.anim.enter_from_right).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

    }

    // TODO Dashboard click events
    private fun homeItemClicked(item: HomeItem?) {
        item?.let {
            navigate(it.type, it.bundle)
        }
        //testRoom()
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
                popup(R.id.navigation_home)
                isHome = true
                navigation?.setCheckedItem(R.id.nav_home)
            }
            R.id.nav_ch6 -> {
                navigate(HomeItem.Type.BOOSTER_SCAN)
            }
            R.id.navigation_channels -> {
                navigate(HomeItem.Type.BOOSTER_SCAN)
            }
            R.id.navigation_calendar -> {
                navigate(HomeItem.Type.CALENDAR)
            }
            R.id.nav_scan -> {
                //startScanning(false)
                val bundle = Bundle()
                bundle.putBoolean("is_rxl", false);
                navigate(0, R.id.navigation_devices)
            }
            R.id.nav_rxl -> {
                navigate(0, R.id.navigation_rxl_home)

            }

//            R.id.nav_workout -> {
//                navigate(0, R.id.navigation_workout)
//            }
//
            // TODO NAV_TEST
//            R.id.nav_test3 -> {
//                lastId = -1
//                startScanningView(true, DeviceScanFragment.RXL)
//                // SessionManager.getInstance().userSession.createDummy()
//                //startActivity(Intent(this@MainActivity, PaymentActivity::class.java))
//                //Payments.testPayment(this@MainActivity)
//                //startScanning(false)
//                //updateMenu()
//                // test
//                //navigate(0, R.id.navigation_rxl_home)
//
//                //navigate(0, R.id.navigation_select_suit)
//                // navigate(0, R.id.navigation_bmi)
//                // navigate(0, R.id.navigation_measurement)
//
//            }
//
//            R.id.nav_test4 -> {
//
//                val list = ArrayList(SessionManager.getInstance().userSession.devices)
//                if (list.size > 0)
//                    for (d in list) {
//                        if (d.isRxt) {
//                            navigateTo(Navigator.RXT_SELECT_WORKOUT, null)
//                            return
//                        }
//                    }
//                lastId = -1
//                startScanningView(true, DeviceScanFragment.RXT)
//                // SessionManager.getInstance().userSession.createDummy()
//                //startActivity(Intent(this@MainActivity, PaymentActivity::class.java))
//                //Payments.testPayment(this@MainActivity)
//                //startScanning(false)
//                //updateMenu()
//                // test
//                //navigate(0, R.id.navigation_rxl_home)
//
//                //navigate(0, R.id.navigation_select_suit)
//                // navigate(0, R.id.navigation_bmi)
//                // navigate(0, R.id.navigation_measurement)
//            }

            R.id.navigation_add_product -> {
                navigate(0, R.id.navigation_add_product)

            }
            R.id.navigation_barcode -> {
                navigate(0, R.id.navigation_barcode)

            }
            R.id.navigation_rxl_test -> {
                navigate(0, R.id.navigation_rxl_test)

            }
//            R.id.nav_measurement -> {
//                navigate(0, R.id.navigation_bio_summary)
//
//            }
//            R.id.nav_messages -> {
//                comingSoon()
//                return
//            }
//            R.id.nav_groups -> {
//                comingSoon()
//                return
//            }
            R.id.nav_notifications -> {
                navigate(
                    0,
                    R.id.navigation_notifications
                )
                return
            }
            R.id.nav_contact -> {
                navigate(
                    0,
                    R.id.navigation_contact
                )
                return
            }

//            R.id.nav_settings -> {
//                navigate(
//                    0,
//                    R.id.navigation_settings
//                )
//                // comingSoon()
//                // return
//            }

            R.id.nav_share -> {
                lastId = -1
                try {
                    ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setChooserTitle(getString(R.string.share_with_friends))
                        .setText("${getString(R.string.share_text)}${this.packageName}")
                        .startChooser();
                } catch (e: Exception) {

                }
            }
            R.id.nav_policy -> {
                lastId = -1
                navigate(
                    0,
                    R.id.navigation_webview,
                    WebViewFragment.bundle("https://docs.google.com/viewerng/viewer?embedded=true&url=https://mibo.life/wp-content/uploads/2020/06/Mibo-livfit-privacy-policy.pdf")
                )
                title = "Privacy Policy"
            }
            R.id.nav_rate -> {
                lastId = -1
                rateUs()
            }

            R.id.nav_faq -> {
                lastId = -1
                navigate(
                    0,
                    R.id.navigation_webview,
                    WebViewFragment.bundle("http://test.mibo.life/faq-mobile-application/")
                )
//                val builder = androidx.browser.customtabs.CustomTabsIntent.Builder()
//                builder.setStartAnimations(this, android.R.anim.slide_in_left, R.anim.exit_to_left);
//                builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);
//                builder.setShowTitle(false)
//                val client = builder.build()
//                client.launchUrl(this, Uri.parse("http://test.mibo.life/faq-mobile-application/"))
                title = "Frequently asked questions"
            }

//            R.id.nav_test4 -> {
//                navigate(0, R.id.navigation_rxl_test)
//            }
            R.id.nav_logout -> {
                lastId = -1
                AlertDialog.Builder(this).setTitle(getString(R.string.logout_dialog))
                    .setMessage(getString(R.string.logout_message))
                    .setPositiveButton(
                        R.string.ok_button
                    ) { dialog, which ->
                        dialog.dismiss()
                        logout()
                    }.setNegativeButton(
                        R.string.cancel
                    ) { dialog, which ->
                        dialog.dismiss()
                    }.show()
            }
            else -> {
                //Snackbar.make(drawer, "item clicked " + it.itemId, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun rateUs() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + this.packageName)
                )
            )
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
                )
            )
        }
    }

    fun logout() {
        val prefs = Prefs.getEncrypted(this)
        prefs.initCipher()
        prefs.set("login_enable", "false", true)
        prefs.set("profile_skipped", "false", true)
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    fun comingSoon() {
        RememberMeDialog(200).show(supportFragmentManager, "RememberMeDialog")
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

    private fun navigate(type: HomeItem.Type, bundle: Bundle? = null) {
        lastId = -1
        when (type) {
            HomeItem.Type.HEART -> {
//                navigate(
//                    R.id.action_navigation_home_to_navigation_heart_rate,
//                    R.id.navigation_heart_rate, bundle
//                )
                // comingSoon()
                return
                //navigateFragment(R.id.navigation_heart_rate)
            }
            HomeItem.Type.WEIGHT -> {
                //comingSoon()
                //return
                navigate(0, R.id.navigation_weight_compare)
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
                navigate(
                    R.id.action_navigation_home_to_navigation_calories,
                    R.id.navigation_calories
                )
                //navigateFragment(R.id.navigation_calories)
                // comingSoon()
                return
            }

            HomeItem.Type.BOOSTER_SCAN -> {
                //navController.navigate(R.id.action_navigation_home_pop)
                //navController.navigate(R.id.navigation_home)
                startScanningView(false, DeviceScanFragment.BOOSTER)
                // navigate(0, R.id.navigation_devices)
                //navigateFragment(R.id.navigation_calories)
            }
            HomeItem.Type.SCHEDULE -> {
                trainerWebView(3)
                return
                //navigateFragment(R.id.navigation_schedule)
            }

            HomeItem.Type.MEASURE -> {
                //  navigateFragment(R.id.navigation_program)
                //navigate(0, R.id.navigation_measurement )
                navigate(0, R.id.navigation_bio_summary)

            }

            HomeItem.Type.MEASURE_NEW -> {
                //  navigateFragment(R.id.navigation_program)
                navigate(0, R.id.navigation_measurement)

            }

            HomeItem.Type.RXL_TEST -> {
                drawerItemClicked(R.id.navigation_rxl_test)
            }

            HomeItem.Type.RXL_SCAN -> {
                startScanningView(true, DeviceScanFragment.RXL)
                // navigate(0, R.id.navigation_rxl_home)
                // drawerItemClicked(R.id.navigation_rxl_test)
            }
            HomeItem.Type.PROFILE -> {
                //  if (DEBUG)
                navigate(0, R.id.navigation_profile)
                // drawerItemClicked(R.id.navigation_rxl_test)
            }

            HomeItem.Type.PROFILE_UPLOAD -> {
                //  if (DEBUG)
                navigate(0, R.id.navigation_profile_upload)
                // drawerItemClicked(R.id.navigation_rxl_test)
            }
            HomeItem.Type.STEPS -> {
                navigate(0, R.id.navigation_fitness)
                //navigate(0, R.id.navigation_fit_steps)
                //comingSoon()
                return
            }
            HomeItem.Type.WEATHER -> {
                //comingSoon()
                return
            }
            HomeItem.Type.SERVICES -> {
                navigate(0, R.id.navigation_search_trainer)
                return
            }
            HomeItem.Type.CENTER_BUTTON -> {
                navigate(0, R.id.navigation_catolog)
                return
            }
            HomeItem.Type.MY_ACCOUNT -> {
                navigate(0, R.id.navigation_my_account)
                //comingSoon()
                return
            }
            HomeItem.Type.MY_SERVICES -> {
                trainerWebView(4)
                return
            }
            HomeItem.Type.ADD_SERVICE -> {
                trainerWebView(5)
                return
            }
            HomeItem.Type.NOTIFICATIONS -> {
                navigate(0, R.id.navigation_notifications)
                return
            }
            else -> {
                Toasty.warning(this, "ItemClicked - $type").show()
            }
        }
    }

    private fun navigate(
        actionId: Int,
        fragmentId: Int,
        args: Bundle? = null,
        options: NavOptions? = getNavOptions(),
        extras: androidx.navigation.Navigator.Extras? = null
    ) {

        try {
            if (!::navController.isInitialized)
                navController = Navigation.findNavController(this, R.id.nav_host_fragment)
            if (actionId != 0) {
                val action = navController.currentDestination?.getAction(actionId)
                    ?: navController.graph.getAction(actionId)
                if (action != null && navController.currentDestination?.id != action.destinationId) {
                    navController.navigate(actionId, args, options, extras)
                    return
                }
            }
            if (fragmentId != 0 && fragmentId != navController.currentDestination?.id)
                navController.navigate(fragmentId, args, options, extras)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MiboEvent.log(e)
            //IllegalAccessException when action id not match in fragment
            try {
                Toasty.info(this, R.string.error_occurred, Toasty.LENGTH_SHORT, false).show()
            } catch (ex2: java.lang.Exception) {
                ex2.printStackTrace()
            }
        }
    }

    private fun popup(fragmentId: Int) {
        try {
            navController.popBackStack(fragmentId, false)
            lastId = -1
        } catch (e: java.lang.Exception) {
            navigate(0, fragmentId)
        }
        if (fragmentId == R.id.navigation_home)
            navigation?.setCheckedItem(R.id.nav_home)
    }

    private fun saveFirebaseToken(member: Member?) {
        if (member == null)
            return
        FirebaseMessaging.getInstance().isAutoInitEnabled = true;

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                Logger.e("$task")
                if (!task.isSuccessful) {
                    Logger.e("saveFirebaseToken failed")
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Logger.e("saveFirebaseToken token $token")
                if (token != null)
                    API.request.getApi().saveFirebaseToken(
                        FirebaseTokenPost(
                            member.id(),
                            member.accessToken ?: "",
                            token
                        )
                    ).enqueue(object : Callback<ResponseData> {
                        override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                            Logger.e("$t")
                        }

                        override fun onResponse(
                            call: Call<ResponseData>,
                            response: Response<ResponseData>
                        ) {
                            Logger.e("${response.body()}")
                            try {
                                if (MiboApplication.DEBUG)
                                    testMessage("Welcome Back " + getMember()?.firstName)
                            } catch (e: Exception) {

                            }
                        }

                    })
            })

        FirebaseMessaging.getInstance().subscribeToTopic("mibo_user")
            .addOnCompleteListener { task ->
                log("subscribeToTopic $task")
                // var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    //  msg = getString(R.string.msg_subscribe_failed)
                }
                // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }

        val type = getMember()?.type
        FirebaseMessaging.getInstance().subscribeToTopic("$type")
            .addOnCompleteListener { task ->
                log("subscribeToTopic $task")
                // var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    //  msg = getString(R.string.msg_subscribe_failed)
                }
                // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //menuInflater.inflate(R.menu.main, menu)
        log("onCreateOptionsMenu ")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Toasty.info(this, "drawer clicked").show()
        log("onOptionsItemSelected $item")
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.itemId == R.id.home) {

        }

        return super.onOptionsItemSelected(item)
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        Database.getInstance(this).clearAll()
        if (::commHandler.isInitialized)
            commHandler.unregister()
        manager?.onDestroy()
        CommunicationManager.getInstance().onDestroy()
        unregisterNetworkMonitor()
        super.onDestroy()

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

    private fun childBackPressed(): Boolean {
        val base =
            supportFragmentManager?.primaryNavigationFragment?.childFragmentManager?.fragments?.get(
                0
            )
        if (base is BaseFragment) {
            return base.onBackPressed()
        }

        return true
    }

    private fun childPlusClicked(): Boolean {
        val base =
            supportFragmentManager?.primaryNavigationFragment?.childFragmentManager?.fragments?.get(
                0
            )
        if (base is BaseFragment) {
            return base.onPlusClicked()
        }

        return true
    }

    private fun updateDrawerHomeButton() {
        log("updateDrawerHomeButton")
        try {
            if (navigation?.checkedItem?.itemId != R.id.nav_home)
                navigation?.setCheckedItem(R.id.nav_home)
            lastId = -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        log("updateDrawerHomeButton end")
    }

    override fun onBackPressed() {
        log("onBackPressed")
        if (::drawerLayout.isInitialized)
            if (drawerLayout?.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
                return
            }
        if (!childBackPressed())
            return
        // Navigation.findNavController(this, R.id.nav_host_fragment)
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()) {
            lastId = -1
            //updateDrawerHomeButton()
            return
        }
        navigation?.setCheckedItem(R.id.nav_home)
//        if (navController.popBackStack(R.id.navigation_home, true))
//            return;
        isHome = true
        if (isHome) {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed()
                return
            }
            Toasty.info(this, getString(R.string.tap_back_exit), Toast.LENGTH_SHORT, false).show()
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

    /* test room performance and implementation with rx java */
    @SuppressLint("CheckResult")
    fun testRoom() {
        Observable.empty<String>().observeOn(Schedulers.newThread()).doOnComplete {
            log("Database.getInstance memberDao")
            Database.getInstance(this@MainActivity).memberDao()
                .getMember().doOnNext {
                    log("RoomDatabase memberDao doOnNext size ${it.size}")
                    log("RoomDatabase memberDao doOnNext $it")
                }.subscribe {
                    log("RoomDatabase memberDao subscribe $it")
                }
            log("RoomDatabase memberDao end")
        }.subscribe()

        Observable.just("test").observeOn(Schedulers.newThread()).doOnComplete {
            log("RoomDatabase memberDao2 doOnComplete")
            val l = Database.getInstance(this@MainActivity).memberDao().getMembers()
            log("RoomDatabase memberDao2 end ${l.size} : $l")
        }.subscribe {
            log("RoomDatabase memberDao2 subscribe $it")
        }

        Observable.just("test_insert").observeOn(Schedulers.newThread()).doOnComplete {
            log("RoomDatabase memberDao3 doOnComplete")
            val m = Prefs.get(this@MainActivity).member
            log("RoomDatabase memberDao3 $m")
            val m2 = life.mibo.android.database.Member.from(m!!)
            log("RoomDatabase memberDao3 - $m2")
            val id = Database.getInstance(this@MainActivity).memberDao().add(m2)
            log("RoomDatabase memberDao3 - added $id")
        }.subscribe {
            log("RoomDatabase memberDao3 subscribe $it")
        }

        Observable.fromCallable {
            log("RoomDatabase memberDao4 fromCallable")
        }.observeOn(Schedulers.newThread()).doOnComplete {
            log("RoomDatabase memberDao4 doOnComplete")
            val m = Prefs.get(this@MainActivity).member
            log("RoomDatabase memberDao4 $m")
            val m2 = life.mibo.android.database.Member.from(m!!)
            log("RoomDatabase memberDao4 - $m2")
            val id = Database.getInstance(this@MainActivity).memberDao().add(m2)
            log("RoomDatabase memberDao4 - added $id")
        }.subscribe {
            log("RoomDatabase memberDao4 subscribe ")
        }
    }

    override fun onResume() {
        log("RESUME MANI")
        super.onResume()
        try {
            if (isHome)
                updateBar(true)
        } catch (e: java.lang.Exception) {

        }
    }


    fun testMessage(message: String?) {
        val intent: Intent? = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId: String = "test_channel"
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.mibo_144)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pendingIntent)
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel = NotificationChannel(
                channelId,
                "Testing Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }
        notificationManager?.notify(101, notificationBuilder.build())
    }

    var networkMoniter: BroadcastReceiver? = null

    private fun registerNetworkMonitor() {
        if (networkMoniter == null) {
            networkMoniter = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    networkChange(context)
                }

            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                networkMoniter,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                networkMoniter,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    private fun unregisterNetworkMonitor() {
        try {
            unregisterReceiver(networkMoniter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    var internetSnackbar: Snackbar? = null
    fun networkChange(context: Context?) {
        try {
            if (Utils.isConnected(context)) {
                if (internetSnackbar != null)
                    internetSnackbar?.dismiss()
            } else {
                internetSnackbar = Toasty.closeSnackbar(
                    window?.decorView?.rootView,
                    R.string.no_internet, Gravity.CENTER
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

}
