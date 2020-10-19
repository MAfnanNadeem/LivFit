/*
 *  Created by Sumeet Kumar on 2/2/20 12:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/2/20 11:57 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.devices

//import com.polidea.rxandroidble2.RxBleClient
//import com.polidea.rxandroidble2.RxBleDevice
//import com.polidea.rxandroidble2.scan.ScanFilter
//import com.polidea.rxandroidble2.scan.ScanSettings
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_devices2.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.models.ScanComplete
import life.mibo.android.ui.TestActivity
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.devices.adapter.ScanDeviceAdapter
import life.mibo.android.ui.heart_rate.HeartRateFragment
import life.mibo.android.ui.home.HomeItem
import life.mibo.android.ui.main.MessageDialog
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.ui.trainer.TrainerCalendarActivity
import life.mibo.android.ui.trainer.TrainerCalendarDialog
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.*
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceConstants.DEVICE_CONNECTED
import life.mibo.hardware.models.DeviceTypes
import life.mibo.hardware.models.User
import life.mibo.views.loadingbutton.presentation.State
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


class DeviceScanFragment : BaseFragment(), ScanObserver {

    companion object {

        const val ANY = 1
        const val BOOSTER = 2
        const val RXL = 3
        const val RXT = 4

        fun bundle(isRxl: Boolean, type: Int): Bundle {
            val arg = Bundle()
            arg.putBoolean("is_rxl", isRxl)
            arg.putInt("is_search_type", type)
            return arg
        }

        fun rxtBundle(): Bundle {
            val arg = Bundle()
            arg.putBoolean("is_rxl", true)
            arg.putInt("is_search_type", RXT)
            return arg
        }

    }

    interface Listener : BaseListener {
        fun onScan(isWifi: Boolean)
        fun onRescan(isWifi: Boolean)
        fun onConnect(device: Device)
        fun onDisconnect(device: Device)
    }

    //private var viewModel: ScanDeviceViewModel? = null
    lateinit var controller: ScanController
    var isRxl = false
    private var searchType = 0


    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        //viewModel = ViewModelProviders.of(this).get(ScanDeviceViewModel::class.java)
        val root = i.inflate(R.layout.fragment_devices2, c, false)
        retainInstance = true
        return root
    }

    var isWifi = false
    var isScanning = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
        controller = ScanController(this, this)

        setRecycler()
        tv_scan?.setOnClickListener {
            //scanRxTest()
            checkAndScan()
            //loadTest()
        }
        //tv_scan?.text = getString(R.string.scanning)
        // checkbox_wifi?.isChecked = true
        //progress_circular?.visibility = View.VISIBLE
        //AnimateView(tv_scan)
        recyclerViewAvailable?.isNestedScrollingEnabled = false
        //recyclerViewConnect?.isNestedScrollingEnabled = false
        //recyclerViewPaired?.isNestedScrollingEnabled = false

        controller.getConnectedDevices()
        controller.getSavedDevices()
//        btn_send.setOnClickListener {
//            controller.testConnect(et_ip?.text.toString())
//        }
        wifi_switch?.setOnClickListener {
            log("wifi_switch clicked")
            //scanDialog()
            checkPermissions(false)
        }
        wifi_switch_?.setOnClickListener {
            log("wifi_switch2 clicked")
            // scanDialog()
            checkPermissions(false)
        }
        //wifi_switch_.isClickable = false
        //wifi_switch_.isEnabled = false
//        wifi_switch?.setCheckedChangeListener {
//            // val wifi = it.toggle() == IconSwitch.Checked.RIGHT
//            if (isScanDialog) {
//                isScanDialog = false
//                return@setCheckedChangeListener
//            }
//            checkPermissions()
//            //log("wifi_switch $wifi")
////            if (wifi != isWifi) {
////                isWifi = wifi
////                //navigate(Navigator.SCAN, isWifi)
////            }
//        }
        //bindProgressButton(tv_scan)
//        /button_next?.visibility = View.INVISIBLE
        button_next?.isEnabled = false
        button_next?.setOnClickListener {
            onNextClicked()
        }


        isRxl = arguments?.getBoolean("is_rxl") ?: false
        searchType = arguments?.getInt("is_search_type", 0) ?: 0
        if (searchType == RXT) {
            isWifi = true
            wifi_switch_?.setChecked(isWifi)
        }
        checkAndScan()
        //if (isRxl)
        button_next?.text = getString(R.string.next)

        button_connect_all?.setOnClickListener {
            //testCal()
            //startTrainerCalenderResult()
            if (button_connect_all.getState() == State.IDLE || button_connect_all.getState() == State.STOPPED) {
                connectAllDevices(isRxl)
            }
        }

//        button_connect_all?.setListener {
//
//        }

        //loadTest()
        //loadTest()
        //testSignals()

        //isBluetoothEnable()
    }

    fun setDefaultMode(isWifi_: Boolean) {
        if (isWifi_) {
            isWifi = true
            wifi_switch_?.setChecked(isWifi)
        } else {

        }
    }

    private fun onNextClicked() {
        log("onNextClicked $isRxl")
        val member = Prefs.get(this.context).member
        if (member != null) {
            if (searchType == RXT) {
                navigate(Navigator.RXT_SELECT_WORKOUT, null)
                return
            }
            SessionManager.getInstance().userSession.user =
                User("${member.firstName}", "${member.lastName}", "${member.id}")

            if (isRxl) {
                var navigate = false
                if (SessionManager.getInstance().userSession.devices.size > 0) {
                    for (i in SessionManager.getInstance().userSession.devices) {
                        if (i.isPod) {
                            navigate = true
                            break
                        }
                    }
                    if (navigate)
                        navigate(Navigator.RXL_HOME, null)
                }
            } else {
                val size = SessionManager.getInstance().userSession.devices.size
                // log("onNextClicked size :: $size")
                if (size > 0) {
                    for (i in SessionManager.getInstance().userSession.devices) {
                        log("onNextClicked dev :: $i")
                        if (size == 1 && i.isBand) {
                            navigate(
                                Navigator.HOME,
                                HomeItem(HomeItem.Type.HEART, HeartRateFragment.bundle(1))
                            )
                            return
                        }
                        if (i.isScale) {
                            startActivity(Intent(context, TestActivity::class.java))
                            return
                        }
                        if (i.isBooster) {
                            if (member?.isMember()) {
                                SessionManager.getInstance().userSession.booster = i
                                //navigate(Navigator.SELECT_SUITS, null)
                                startTrainerCalenderResult()
                            } else {
                                startTrainerCalenderResult()
                            }
                            return
                        }

                        if (searchType == RXT) {
                            navigate(Navigator.RXT_SELECT_WORKOUT, null)
                            return
                        }
                    }
                }
                Toasty.info(requireContext(), getString(R.string.no_booster_found)).show()
            }
            //MessageDialog.info(this@DeviceScanFragment.requireContext(), "", "")
            //   navigate(Navigator.HOME, HomeItem(HomeItem.Type.RXL))
            //else
        }
    }

    fun testCal() {
        TrainerCalendarDialog(CalendarListener).show(childFragmentManager, "TrainerCalendarDialog")
//        FeedbackDialog(requireContext(), object : ItemClickListener<FeedbackDialog.Feedback> {
//            override fun onItemClicked(item: FeedbackDialog.Feedback?, position: Int) {
//                Toasty.info(requireContext(), "clicked $item").show()
//            }
//
//        }).show()
    }

    private var CalendarListener =
        object : ItemClickListener<TrainerCalendarDialog.TrainerSession> {

            override fun onItemClicked(
                session: TrainerCalendarDialog.TrainerSession?,
                position: Int
            ) {
                if (session != null) {
                    val bundle = Bundle()
                    bundle.putBoolean("is_trainer", true)
                    bundle.putInt("session_id", session.sessionId)
                    bundle.putInt("userId_id", session.memberId)
                    bundle.putString("user_weight", session.weight)
                    bundle.putString("member_image", session.profile)
                    bundle.putString("member_name", session.member)
                    navigate(Navigator.SELECT_PROGRAM, bundle)
                } else Toasty.snackbar(view, R.string.error_occurred)
            }

        }


    private fun startTrainerCalenderResult() {
        val i = Intent(requireContext(), TrainerCalendarActivity::class.java)
        i.putExtra("activity_type", 15)
        startActivityForResult(i, REQUEST_CALENDER)
    }

    private fun proceedToNext(bundle: Bundle) {
        navigate(Navigator.SELECT_PROGRAM, bundle)
    }


    var isConnectTrigger = false

    @SuppressLint("CheckResult")
    fun revertNextButton(time: Long = 1000) {
        Single.timer(time, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                if (isConnectTrigger) {
                    val b = BitmapFactory.decodeResource(resources, R.drawable.ic_done_white)
                    log("button_next doOnSuccess $b")
//                    button_connect_all.doneLoadingAnimation(
//                        ContextCompat.getColor(
//                            context!!,
//                            R.color.color_button_yellow
//                        ), MyWebViewClient
//                    )
                    if (b != null)
                        button_connect_all.doneLoadingAnimation(0xFFFFA000.toInt(), b)
                }
            }.doOnSuccess {
                isConnectTrigger = false
                log("testNextButton doOnSuccess $it")
                button_connect_all?.revertAnimation {
                    availabeAdapter?.list?.size?.let { size ->
                        if (size > 1)
                            button_connect_all?.text = getString(R.string.connect_all)
                        else
                            button_connect_all?.text = getString(R.string.connect)
                        button_connect_all?.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.button_shape_primary
                            )
                        //else tv_connect_all?.visibility = View.GONE
                    }

                    //button_next.background
                    //if (isRxl)
                    //    button_next?.text = getString(R.string.next)
                    //  else
                    //   button_next?.text = getString(R.string.select_program)
                }
            }.subscribe { i ->
                log("testNextButton subscribe $i")
            }
    }

    private fun scanDevices() {
        log("scanDevices $isScanning")
        if (isScanning)
            return
        text_type?.text = if (isWifi) getString(R.string.wifi) else getString(R.string.bluetooth)
        isScanning = true
        val size = availabeList.size
        availabeAdapter?.resetRxlCount()
        availabeList.clear()
        availabeAdapter?.notifyItemRangeRemoved(0, size)
        // CommunicationManager.getInstance().discoveredDevices.clear()
        progress_circular?.visibility = View.VISIBLE
        //progress_circular?.visibility = View.VISIBLE
        navigate(Navigator.SCAN, isWifi)
        //wifi_switch?.checked = IconSwitch.Checked.RIGHT
        tv_scan?.text = getString(R.string.scanning) + " ..."

//            tv_scan.showProgress {
//                buttonText = "Scanning "
//                progressColorRes = R.color.colorAccent
//            }

        controller.getConnectedDevices()
    }

    var isScanDialog = false
    private fun scanDialog() {
        //isScanDialog = true
        val wifi = !isWifi
        val txt = if (wifi) getString(R.string.wifi) else getString(R.string.bluetooth)
        MessageDialog.show(
            this.requireActivity(),
            getString(R.string.scan) + " $txt",
            getString(R.string.scan_again) + " $txt",
            getString(R.string.scan),
            getString(R.string.cancel),
            object : MessageDialog.Listener {
                override fun onClick(button: Int) {
                    if (button == MessageDialog.POSITIVE) {
                        isWifi = wifi
                        wifi_switch_!!.post {
                            wifi_switch_?.setChecked(isWifi)
                            isScanning = false
                            scanDevices()
                        }
                    } else {
                        wifi_switch_!!.post {
                            wifi_switch_?.setChecked(isWifi)
                        }
                    }
                }
            })
//        AlertDialog.Builder(this.activity!!)
//            .setTitle(getString(R.string.scan) + " $txt")
//            .setMessage(getString(R.string.scan_again) + " $txt")
//            .setNegativeButton(getString(R.string.cancel)) { i, j ->
//                i.dismiss()
//                wifi_switch!!.post {
//                    wifi_switch?.setChecked(isWifi)
//                }
//            }.setPositiveButton(getString(R.string.scan)) { i, _ ->
//                i.dismiss()
//                isWifi = wifi
//                wifi_switch!!.post {
//                    wifi_switch?.setChecked(isWifi)
//                    isScanning = false
//                    scanDevices()
//                }
//
//            }.create().show()
    }

    fun testSignals() {
        for (i in 1..20) {
            Logger.e(
                "DeviceStatusEvent testSignals signalLevel ${i.times(-10)} - " + Utils.getWifiSignalLevel(
                    i.times(-10),
                    4
                )
            )
        }
    }

    var isConnected = false

    fun updateButton() {
        activity?.runOnUiThread {
            button_next?.isEnabled = isConnected
        }
    }

    override fun onNewDevice(device: Device) {

    }


    override fun onNewDevices(devices: ArrayList<Device>) {

    }

    override fun onPairedDevices(devices: ArrayList<Device>) {

    }


    override fun onConnectedDevices(devices: ArrayList<Device>) {
        log("onConnectedDevices ${devices.size}")
        if (devices.isNotEmpty()) {
//            log("onConnectedDevices before ${devices}")
            devices.sortBy {
                it.id
            }
//            log("onConnectedDevices after ${devices}")
            availabeAdapter?.addDevice(devices, isRxl)
//            devices?.forEach {
//                availabeAdapter?.addDevice(it)
////                if (isRxl)
////                    availabeAdapter?.addDevice(it, isRxl)
////                else availabeAdapter?.addDevice(it)
//            }
        }
    }


    private val connectionListener = object : ScanDeviceAdapter.Listener {
        override fun onConnectClicked(device: Device?) {
            log("connectionListener onConnectClicked " + device?.statusConnected)
            if (device?.statusConnected == DEVICE_CONNECTED)
                return
            //SessionManager.getInstance().userSession = UserSession.from(device)
            CommunicationManager.getInstance().connectDevice(device)
            try {
                Prefs.get(this@DeviceScanFragment.context).setJson(device?.uid, device)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            if (device?.type == DeviceTypes.WIFI_STIMULATOR || device?.type == DeviceTypes.BLE_STIMULATOR) {
                SessionManager.getInstance().userSession.uid = device.uid
                //SessionManager.getInstance().userSession.addDevice(device)
                Prefs.get(this@DeviceScanFragment.activity)["user_uid"] = device.uid
            }

            //navigate(Navigator.CONNECT, device)
            button_next?.visibility = View.VISIBLE
            //testColors()
        }

        override fun onCancelClicked(device: Device?) {
            //SessionManager.getInstance().userSession.device = null
            //DEVICE_CONNECTED
            log("connectionListener onCancelClicked " + device?.statusConnected)
            CommunicationManager.getInstance().disconnectDevice(device)
            //navigate(Navigator.DISCONNECT, device)
            isConnected = false
            updateButton()
            //device?.statusConnected = 1
        }

        override fun onClicked(device: Device?) {
            if (device?.isRxt == true)
                blinkRxtDevice(device?.uid)
            else
                blinkDevice(device?.id, device?.uid)
        }

    }

    fun blinkDevice(id: String?, uid: String?) {
        uid?.let {
            Single.fromCallable {
                val color = Utils.getColorAt(id)
                EventBus.getDefault().postSticky(RxlBlinkEvent(it, 200, 200, 3, color))
            }.subscribeOn(Schedulers.io()).doOnError {
                MiboEvent.log(it)
            }.subscribe()
        }
    }

    fun blinkRxtDevice(uid: String?) {
        Single.fromCallable {
            CommunicationManager.getInstance()
                .onRxtBlinkAll(ChangeColorEvent(uid, "2", Color.RED, 500, 500))
            ""
        }.subscribeOn(Schedulers.io()).doOnError { }.subscribe()
    }

    @SuppressLint("CheckResult")
    fun testColors() {
        Single.just(Utils.getRandomColor()).delay(4, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe { it ->
                run {
                    log("testColors $it")
//                    val d = SessionManager.getInstance().userSession.devices
//                    if (d != null && d.type == DeviceTypes.RXL_WIFI) {
//                        d.colorPalet = it
//                        EventBus.getDefault().postSticky(ChangeColorEvent(d, d.uid))
//                        navigate(Navigator.HOME, HomeItem(HomeItem.Type.RXL_TEST))
//                    }
                }
            }
    }

    fun loadTest() {
        availabeList.addAll(getTestDevices())
        availabeAdapter?.notifyDataSetChanged()

        //connectedAdapter?.notifyDataSetChanged()
        onConnectedDevices(getTestDevices())

    }

    fun getTestDevices(): ArrayList<Device> {
        val list = ArrayList<Device>()
        list.add(Device("Test Device", "192.168.101.125", "", DeviceTypes.WIFI_STIMULATOR))
        list.add(Device("Test Device", "192.168.101.126", "", DeviceTypes.BLE_STIMULATOR))
        list.add(Device("Test Device", "192.168.101.127", "", DeviceTypes.GENERIC))
        list.add(Device("Test Device", "192.168.101.128", "", DeviceTypes.GENERIC))
        list.add(Device("Test Device", "192.168.101.129", "", DeviceTypes.RXL_WIFI))
        list.add(Device("Test Device", "192.168.101.121", "", DeviceTypes.RXL_BLE))
        return list
    }

    //var list = ArrayList<ScanDeviceAdapter.ScanItem>()
    //private var connectedList = ArrayList<Device>()
    private var availabeList = ArrayList<Device>()

    //private var list = ArrayList<Device>()
    private var availabeAdapter: ScanDeviceAdapter? = null
    //private var connectedAdapter: ScanDeviceAdapter? = null

    private fun setRecycler() {

        availabeList.clear()

//        list.add(Device("Test Device", "192.168.101.125", "", DeviceTypes.WIFI_STIMULATOR))
//        list.add(Device("Test Device", "192.168.101.125", "", DeviceTypes.BLE_STIMULATOR))
//        list.add(Device("Test Device", "192.168.101.125", "", DeviceTypes.GENERIC))
//        list.add(Device("Test Device", "192.168.101.125", "", DeviceTypes.GENERIC))
        availabeAdapter = ScanDeviceAdapter(availabeList, 0)

        availabeAdapter?.setListener(connectionListener)

        val manager = GridLayoutManager(this@DeviceScanFragment.activity, 1)
        recyclerViewAvailable.layoutManager = manager

        recyclerViewAvailable.adapter = availabeAdapter

//        if (SessionManager.getInstance().userSession != null && SessionManager.getInstance().userSession.registeredDevices.size > 0) {
//
//            //recyclerViewPaired.layoutManager = GridLayoutManager(this@DeviceScanFragment2.activity, 1)
//            //recyclerViewPaired.adapter = ScanDeviceAdapter(SessionManager.getInstance().userSession.registeredDevices, 0)
//        }


        //connectedAdapter = ScanDeviceAdapter(connectedList, 0)
        //connectedAdapter?.setListener(connectionListener)
//        val cManager = LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
//        recyclerViewConnect.layoutManager = cManager
//        recyclerViewConnect.adapter = connectedAdapter

    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public fun onDeviceEvent(event: ScanComplete) {
        log("onDeviceEvent ScanComplete $event")
        //tv_scan?.hideProgress("Scan")
        activity?.runOnUiThread {
            tv_scan?.text = "Rescan"
            progress_circular?.visibility = View.GONE
            isScanning = false

            if (availabeList.size == 0)
                tv_no_available?.visibility = View.VISIBLE
            else tv_no_available?.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onDeviceEvent(event: DeviceStatusEvent) {
        EventBus.getDefault().removeStickyEvent(event)

        log("DeviceStatusEvent Device ${event.device?.uid}")
        // if (event.device == null)
        //     return
        availabeAdapter?.updateDevice(event.device)
        if (!isConnected || button_next?.visibility != View.VISIBLE) {
            isConnected = true
            //updateButton()
            button_next?.visibility = View.VISIBLE
            log("DeviceStatusEvent Device updateButton......................")
        }
        updateButton()
    }

    @Subscribe
    public fun onDeviceEvent(event: Device) {
        log("onDeviceEvent Device $event")
        availabeAdapter?.addDevice(event)
        activity?.runOnUiThread {
            tv_no_available?.visibility = View.GONE
            showConnectAll()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public fun onDeviceEvent(event: RemoveConnectionStatus) {
        log("onDeviceEvent RemoveConnectionStatus $event")
        var pos = -1
        availabeAdapter?.list?.forEachIndexed { i, d ->
            if (d.uid == event.uid) {
                d.statusConnected = 0
                pos = i
            }
        }

        if (pos != -1)
            availabeAdapter?.notifyItemChanged(pos)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public fun onDeviceEvent(event: NewConnectionStatus) {
        EventBus.getDefault().removeStickyEvent(event)
        log("onDeviceEvent NewConnectionStatus ${event.uid}")
        isConnected = false
        var pos = -1
        availabeAdapter?.list?.forEachIndexed { i, d ->
            if (d.uid == event.uid) {
                d.statusConnected = 1
                isConnected = true
                updateButton()
                pos = i
            }
        }

        if (pos != -1)
            availabeAdapter?.notifyItemChanged(pos)
    }


    // MainActivity
    @Subscribe
    public fun onNewDevice(event: NewDeviceDiscoveredEvent) {
        //  log("NewDeviceDiscoveredEvent received isRxl $isRxl : " + event.data)
        val data = event.data
        if (data is Device) {
            activity?.runOnUiThread {
                //log("NewDeviceDiscoveredEvent data.isPod ${data.isPod}")
                if (data.isPod && isRxl) {
                    availabeAdapter?.addDevice(data, true)
                } else {
                    availabeAdapter?.addDevice(data)
                }
                tv_no_available?.visibility = View.GONE
                showConnectAll()
            }
            try {
                //Prefs.get(this@DeviceScanFragment.context).settJson(data.uid, data)
                log("Saved.........${data.uid}")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showConnectAll() {
        availabeAdapter?.list?.size?.let {
            if (it > 1) {
                activity?.runOnUiThread {
                    button_connect_all?.text = getString(R.string.connect_all)
                    log("showConnectAll size $it")
                }
                //if (button_connect_all?.visibility != View.VISIBLE)
                //    tv_connect_all?.visibility = View.VISIBLE
            }
            //else tv_connect_all?.visibility = View.GONE
        }

    }

    private fun connectAllDevices(rxl: Boolean = false) {
        if (searchType == RXT) {
            connectAllRxts()
            return
        }
        if (availabeAdapter!!.list!!.size > 0) {
            Observable.fromIterable(availabeAdapter!!.list!!).doOnError { }
                .subscribeOn(Schedulers.io())
                .subscribe(object : io.reactivex.Observer<Device> {
                    override fun onSubscribe(d: Disposable) {
                        button_connect_all?.startAnimation {

                        }
                    }

                    override fun onNext(t: Device) {
                        log("connectAllDevices onNext")
                        if (rxl) {
                            if (t.isPod) {
                                connectionListener?.onConnectClicked(t)
                                // Thread.currentThread().sleep(100)
                                isConnectTrigger = true
                                Thread.sleep(120)
                                log("connectAllDevices statusConnected ${t.statusConnected}")
                            }
                        } else {
                            if (t.isBooster) {
                                connectionListener?.onConnectClicked(t)
                                isConnectTrigger = true
                                Thread.sleep(100)
                            }

                        }
                    }

                    override fun onError(e: Throwable) {
                        log("iv_plus onError", e)
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        //adapter?.notifyDataSetChanged()
                        Single.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess {
                                revertNextButton()

                            }.subscribe()

                    }
                })

        }

    }

    private fun connectAllRxts() {
        if (availabeAdapter!!.list!!.size > 0) {
            Observable.fromIterable(availabeAdapter!!.list!!).doOnError { }
                .subscribeOn(Schedulers.io())
                .subscribe(object : io.reactivex.Observer<Device> {
                    override fun onSubscribe(d: Disposable) {
                        button_connect_all?.startAnimation {

                        }
                    }

                    override fun onNext(t: Device) {
                        log("connectAllDevices onNext")
                        if (t.isRxt) {
                            connectionListener?.onConnectClicked(t)
                            isConnectTrigger = true
                            Thread.sleep(100)
                        }
                    }

                    override fun onError(e: Throwable) {
                        log("iv_plus onError", e)
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        //adapter?.notifyDataSetChanged()
                        Single.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                            .doOnSuccess {
                                revertNextButton()

                            }.subscribe()

                    }
                })

        }

    }

    private val REQUEST_BLUETOOTH = 1022
    private val REQUEST_LOCATION = 1023
    private val REQUEST_CALENDER = 1025

    private val REQUEST_BLUETOOTH2 = 1032
    private val REQUEST_LOCATION2 = 1033

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )

    private var isBluetooth = false
    private var isLocation = false
    private var checkScan = false

    private fun checkAndScan() {
        if (isWifi) {
            scanDevices()
            return
        }

        val bl = BluetoothAdapter.getDefaultAdapter()
        if (bl != null && !bl.isEnabled) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_BLUETOOTH2
            )
            return
        }

        isBluetooth = true
        if (isLocationEnabled()) {
            isLocation = true
            checkScan = true
            PermissionHelper.requestPermission(this@DeviceScanFragment, permissions) {
                scanDevices()
            }
        } else {
            MessageDialog.info(
                requireContext(),
                "Location Required",
                "Please enable location to scan BLE devices", "enable"
            ) {
                locationSettings(REQUEST_LOCATION2)
            }
        }
    }

    private fun checkPermissions(scan: Boolean) {
        if (!isWifi) {
            scanDialog()
            return
        }

        val bl = BluetoothAdapter.getDefaultAdapter()
        if (bl != null && !bl.isEnabled) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_BLUETOOTH
            )
            return
        }

        isBluetooth = true
        if (isLocationEnabled()) {
            isLocation = true
            checkScan = false
            PermissionHelper.requestPermission(this@DeviceScanFragment, permissions) {
                scanDialog()
            }
        } else {
            MessageDialog.info(
                requireContext(),
                getString(R.string.location_required),
                getString(R.string.location_required_text), getString(R.string.enable)
            ) {
                locationSettings()
            }
        }


        // BluetoothManager.Manager
    }

    private fun locationSettings(code: Int = REQUEST_LOCATION) {
        val enableLocationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(enableLocationIntent, code);
    }

    private fun isBluetoothEnable() {
        val REQUEST_ENABLE_BT = 1022
        val bl = BluetoothAdapter.getDefaultAdapter()
        if (bl != null && !bl.isEnabled) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BT
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        var mode = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return context?.getSystemService(LocationManager::class.java)?.isLocationEnabled!!
        } else {
            try {
                mode =
                    Settings.Secure.getInt(context?.contentResolver, Settings.Secure.LOCATION_MODE)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return false
            }
            return mode != Settings.Secure.LOCATION_MODE_OFF;
        }


    }

    fun blockedByLocationOff(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                requireContext().getSystemService(LocationManager::class.java).isLocationEnabled
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionHelper.permissionsResult(
            this.requireActivity(),
            permissions as Array<String>, requestCode, grantResults,
            {
                if (checkScan)
                    checkAndScan()
                else
                    checkPermissions(false)
            },
            {
                Toasty.error(
                    this.requireContext(),
                    getString(R.string.permission_denied)
                )
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log("onActivityResult $requestCode  $resultCode  : $data")
        if (requestCode == REQUEST_CALENDER) {
            if (resultCode == Activity.RESULT_OK) {
                val bundle = data?.getBundleExtra("result_data")
                if (bundle != null && bundle.containsKey("session_id")) {
                    proceedToNext(bundle)
                }
            }
        }

        if (requestCode == REQUEST_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                //isBluetooth = true
                checkPermissions(false)
            }
        }
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                // isLocation = true
                checkPermissions(false)
            }
        }

        if (requestCode == REQUEST_BLUETOOTH2) {
            if (resultCode == Activity.RESULT_OK) {
                //isBluetooth = true
                checkAndScan()
            }
        }
        if (requestCode == REQUEST_LOCATION2) {
            checkAndScan()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {


        super.startActivityForResult(intent, requestCode)

    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        SessionManager.getInstance().userSession.isScanning = true
        log("onStart")
    }

    override fun onStop() {
        super.onStop()
        CommunicationManager.getInstance().stopScanning()
        EventBus.getDefault().unregister(this)
        SessionManager.getInstance().userSession.isScanning = false
        controller.onStop()
        button_connect_all?.dispose()
        //button_next?.dispose()
        log("onStop")
        // button_connect_all?.dispose()
    }

    override fun onDestroy() {
        button_connect_all?.release()
        super.onDestroy()
    }


    // TODO RX BLE TEST
//    var bleClient = lazy {
//        RxBleClient.create(requireContext())
//    }
//
//    fun statusRxTest() {
//
//        val d = bleClient.value?.observeStateChanges()?.subscribe {
//            when (it) {
//                RxBleClient.State.READY -> {
//                    scanRxTest()
//                }
//                RxBleClient.State.BLUETOOTH_NOT_ENABLED -> {
//
//                }
//                RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED -> {
//
//                }
//            }
//        }
//    }
//
//    fun scanRxTest() {
//        log("scanRxTest ...... ")
//        val filters = ScanFilter.Builder().build()
//        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED)
//            .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
//            .setShouldCheckLocationServicesState(true).build()
//
//        val d = bleClient.value?.scanBleDevices(settings, filters)?.doOnError {
//            log("scanRxTest: error $it")
//        }?.subscribe {
//            log("scanRxTest: subscribe $it")
//
//            if (it.bleDevice != null) {
//                log("scanRxTest: subscribe name >> ${it.bleDevice.name}")
//
//            }
//
//        }
//    }
//
//
//    private val rxDeviceList = HashMap<String, RxBleDevice?>()
//    fun connectRxTest(mac: String, connect: Boolean) {
//        log("connectRxTest $mac :: $connect")
//        val d = bleClient.value?.getBleDevice(mac)
//        rxDeviceList[mac] = d
//        if (connect) {
//            val d = d?.establishConnection(true)?.subscribe {
//                log("establishConnection $it")
//            }
//        } else {
//            bleClient.value?.getBleDevice(mac)?.establishConnection(false)
//        }
//    }


}