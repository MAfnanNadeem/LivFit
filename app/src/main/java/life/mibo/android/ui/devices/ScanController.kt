/*
 *  Created by Sumeet Kumar on 1/13/20 10:11 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/13/20 10:11 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.devices

import android.bluetooth.BluetoothProfile
import android.content.Context
import android.view.View
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.devices.adapter.ScanDeviceAdapter
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.core.DataParser
import life.mibo.hardware.core.Logger
import life.mibo.hardware.core.Utils
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceTypes
import java.net.InetAddress

class ScanController(val context: BaseFragment, val observer: ScanObserver) :
    DeviceScanFragment.Listener {

    private var connectedList = ArrayList<Device>()
    private var availableList = ArrayList<Device>()
    private var pairedList = ArrayList<Device>()

    private lateinit var availableAdapter: ScanDeviceAdapter
    private lateinit var pairedAdapter: ScanDeviceAdapter
    private lateinit var connectedAdapter: ScanDeviceAdapter

    override fun onCreate(view: View?, data: Any?) {

    }

    override fun onResume() {

    }

    override fun onStop() {

    }

    override fun onScan(wifi: Boolean) {

    }

    override fun onRescan(wifi: Boolean) {

    }

    override fun onConnect(device: Device) {

    }

    override fun onDisconnect(device: Device) {

    }

    fun getSavedDevices() {

    }
    fun getConnectedDevices() {
        connectedList.clear()
        val list = CommunicationManager.getInstance().tcpClients
        Logger.e("ScanController getConnectedDevices ${list?.size}")
        if (list != null && list.isNotEmpty()) {
            // connectedList.clear()
            for (i in list) {
                if (i.isStopped)
                    continue
                var d: Device? = null
                try {
                    d = Prefs.get(context.context).getJson(i.uid, Device::class.java)
                    context.log("getConnectedDevices device.........${d}")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                context.log("Saved device found.........${d}")
                if (d != null) {
                    //d.statusConnected = 1
                    var add = true
                    for (k in connectedList) {
                        if (k.uid == d.uid) {
                            add = false
                            break
                        }
                    }
                    if (add)
                        connectedList.add(d)
                }
                else connectedList.add(
                    Device(
                        "Device",
                        i.serverIp,
                        i.uid,
                        DeviceTypes.WIFI_STIMULATOR
                    )
                )
            }
        }

        try {
            val bluetoothManager =
                context.activity?.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager;
            context.log("getConnectedDevices bluetoothManager")
            val devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            for (b in devices) {
                var d: Device? = null
                try {
                    context.log("getConnectedDevices connectedList1 Device $b")
                    d = SessionManager.getInstance().userSession.getDevice(b.name)
                    context.log("getConnectedDevices connectedList2 Device $b")
                    if (d == null)
                        d = Prefs.get(context.context).getJson(Utils.getUid(b.name), Device::class.java)
                    context.log("getConnectedDevices connectedList3 Device $b")
                    context.log("getConnectedDevices bluetoothDevice.........${d}")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    context.log("getConnectedDevices bluetoothDevice error.........${e.message}")
                }
                if (d != null)
                    connectedList.add(d)
//                if(d == null)
//                    d = Device(MyWebViewClient.name, MyWebViewClient.address, MyWebViewClient.address, DeviceTypes.BLE_STIMULATOR)
               // if (d != null)
                 //   connectedList.add(d)
                context.log("getConnectedDevices connectedList add ble $b")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            context.log("getConnectedDevices ble error " + e.message)
        }

//        if (connectedList.isNotEmpty()) {
//            connectedAdapter = ScanDeviceAdapter(connectedList, 0)
//            connectedAdapter?.setListener(listener)
//            val manager = LinearLayoutManager(this.context.activity, LinearLayoutManager.VERTICAL, false)
//            recyclerViewConnect.layoutManager = manager
//            recyclerViewConnect.adapter = connectedAdapter
//        } else {
//            //cardview_connected?.visibility = View.GONE
//            //recyclerViewConnect?.visibility = View.GONE
//        }

        observer.onConnectedDevices(connectedList)
    }

    fun testConnect(ip: String){
        val b = ByteArray(8)
        CommunicationManager.getInstance().connectDevice(
            Device(
                "Test",
                DataParser.getUID(b),
                InetAddress.getByName(ip),
                DeviceTypes.RXL_WIFI
            )
        )
    }

}