/*
 *  Created by Sumeet Kumar on 2/2/20 12:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/2/20 11:57 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.devices

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_devices2.*
import life.mibo.hardware.SessionManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.DeviceStatusEvent
import life.mibo.hardware.events.NewConnectionStatus
import life.mibo.hardware.events.NewDeviceDiscoveredEvent
import life.mibo.hardware.events.RemoveConnectionStatus
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceTypes
import life.mibo.hardware.models.User
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.ScanComplete
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener
import life.mibo.hexa.ui.base.PermissionHelper
import life.mibo.hexa.ui.devices.adapter.ScanDeviceAdapter
import life.mibo.hexa.ui.main.MessageDialog
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.utils.Toasty
import life.mibo.hexa.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


class DeviceScanFragment2 : BaseFragment(), ScanObserver {

    interface Listener : BaseListener {
        fun onScan(isWifi: Boolean)
        fun onRescan(isWifi: Boolean)
        fun onConnect(device: Device)
        fun onDisconnect(device: Device)
    }

    //private var viewModel: ScanDeviceViewModel? = null
    lateinit var controller: ScanController
    var isRxl = false


    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        //viewModel = ViewModelProviders.of(this).get(ScanDeviceViewModel::class.java)
        val root = i.inflate(R.layout.fragment_devices2, c, false)
        retainInstance = true
        return root
    }

    var isWifi = true
    var isScanning = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
        controller = ScanController(this, this)

        setRecycler()
        tv_scan?.setOnClickListener {
            scanDevices()
            //loadTest()
        }
        tv_scan?.text = getString(R.string.scanning)
        // checkbox_wifi?.isChecked = true
        progress_circular?.visibility = View.VISIBLE
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
            checkPermissions()
        }
        wifi_switch_?.setOnClickListener {
            log("wifi_switch2 clicked")
            // scanDialog()
            checkPermissions()
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
        button_next?.visibility = View.INVISIBLE
        button_next?.setOnClickListener {
            val member = Prefs.get(this.context).member
            if (member != null) {
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
                    if (SessionManager.getInstance().userSession.devices.size > 0) {
                        for (i in SessionManager.getInstance().userSession.devices) {
                            if (i.isBooster) {
                                SessionManager.getInstance().userSession.booster = i
                                navigate(Navigator.SELECT_PROGRAM, null)
                                return@setOnClickListener
                            }
                        }
                    }
                    Toasty.info(context!!, "No booster detected!").show()
                }
                //MessageDialog.info(this@DeviceScanFragment.requireContext(), "", "")
                //   navigate(Navigator.HOME, HomeItem(HomeItem.Type.RXL))
                //else
            }
        }
        button_next?.isEnabled = false

        scanDevices()
        isRxl = arguments?.getBoolean("is_rxl") ?: false
        if (isRxl)
            button_next?.text = "NEXT"

        tv_connect_all?.setOnClickListener {
            connectAllRxl()
        }

        //loadTest()
        //loadTest()
        //testSignals()

    }

    private fun scanDevices() {
        log("scanDevices $isScanning")
        if (isScanning)
            return
        text_type?.text = if (isWifi) getString(R.string.wifi) else getString(R.string.bluetooth)
        isScanning = true
        val size = availabeList.size
        availabeList.clear()
        availabeAdapter?.notifyItemRangeRemoved(0, size)
        // CommunicationManager.getInstance().discoveredDevices.clear()
        progress_circular?.visibility = View.VISIBLE
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
            this.activity!!,
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
            devices?.forEach {
                availabeAdapter?.addDevice(it)
            }
        }
    }


    private val connectionListener = object : ScanDeviceAdapter.Listener {
        override fun onConnectClicked(device: Device?) {
            log("connectionListener onConnectClicked ")
            //SessionManager.getInstance().userSession = UserSession.from(device)
            try {
                Prefs.get(this@DeviceScanFragment2.context).settJson(device?.uid, device)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            if (device?.type == DeviceTypes.WIFI_STIMULATOR || device?.type == DeviceTypes.BLE_STIMULATOR) {
                SessionManager.getInstance().userSession.uid = device.uid
                //SessionManager.getInstance().userSession.addDevice(device)
                Prefs.get(this@DeviceScanFragment2.activity)["user_uid"] = device.uid
            }
            navigate(Navigator.CONNECT, device)
            button_next?.visibility = View.VISIBLE
            //testColors()
        }

        override fun onCancelClicked(device: Device?) {
            //SessionManager.getInstance().userSession.device = null
            log("connectionListener onCancelClicked " + device?.statusConnected)
            navigate(Navigator.DISCONNECT, device)
            isConnected = false
            updateButton()
            //device?.statusConnected = 1
        }

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

        val manager = GridLayoutManager(this@DeviceScanFragment2.activity, 1)
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
        log("DeviceStatusEvent Device $event")

        availabeAdapter?.updateDevice(event.device)
        if (!isConnected || button_next?.visibility != View.VISIBLE) {
            isConnected = true
            updateButton()
            button_next?.visibility = View.VISIBLE
            log("DeviceStatusEvent Device updateButton......................")
        }
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
        log("onDeviceEvent NewConnectionStatus $event")
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


    @Subscribe
    public fun onNewDevice(event: NewDeviceDiscoveredEvent) {
        log("NewDeviceDiscoveredEvent received " + event.data)
        val data = event.data
        if (data is Device) {
            activity?.runOnUiThread {
                availabeAdapter?.addDevice(data)
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
            if (it > 2) {
                if (tv_connect_all?.visibility != View.VISIBLE)
                    tv_connect_all?.visibility = View.VISIBLE
            } else tv_connect_all?.visibility = View.GONE
        }

    }

    private fun connectAllRxl() {
        Observable.fromArray(availabeAdapter!!.list!!).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Device> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Device) {
                    if (t.isPod && t.statusConnected != 1) {
                        connectionListener?.onConnectClicked(t)
                        Thread.sleep(80)
                    }
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    //adapter?.notifyDataSetChanged()

                }
            })

    }

    private val REQUEST_BLUETOOTH = 1022
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )

    private fun checkPermissions() {
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


        if (isLocationEnabled()) {
            PermissionHelper.requestPermission(this@DeviceScanFragment2, permissions) {
                scanDialog()
            }
        } else {
            MessageDialog.info(
                context!!,
                "Location Required",
                "Please enable location to scan BLE devices", "enable"
            ) {
                locationSettings()
            }
        }


        // BluetoothManager.Manager
    }

    fun locationSettings() {
        val enableLocationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(enableLocationIntent, REQUEST_BLUETOOTH);
    }

    fun isLocationEnabled(): Boolean {
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
                context!!.getSystemService(LocationManager::class.java).isLocationEnabled
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

        PermissionHelper.permissionsResult(this@DeviceScanFragment2.activity!!,
            permissions as Array<String>, requestCode, grantResults,
            {
                checkPermissions()
            },
            {
                Toasty.error(
                    this@DeviceScanFragment2.context!!,
                    getString(R.string.permission_denied)
                )
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                checkPermissions()
            }
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
        log("onStart")
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        controller.onStop()
        log("onStop")
    }


}