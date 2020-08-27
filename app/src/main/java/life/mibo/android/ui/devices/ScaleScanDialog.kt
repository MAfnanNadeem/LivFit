/*
 *  Created by Sumeet Kumar on 5/11/20 11:21 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/11/20 10:25 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.devices

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_scan_scale.*
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.devices.adapter.ScanDeviceAdapter
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.events.DeviceStatusEvent
import life.mibo.hardware.events.IndicationEvent
import life.mibo.hardware.events.NewConnectionStatus
import life.mibo.hardware.events.ScaleDataEvent
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.ScaleData
import java.util.concurrent.TimeUnit


class ScaleScanDialog(var type_: Int = 0, val listener: ItemClickListener<Double>?) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_scale, container, false)
    }

    var weightKg: Double = 0.0
    var weight: TextView? = null
    var recyclerView: RecyclerView? = null
    var adapters: ScanDeviceAdapter? = null
    var devices: ArrayList<Device> = ArrayList()
    var buttonView: View? = null
    var empty: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //var connect: CircularProgressButton? = view?.findViewById(R.id.button_connect)
        buttonView = view?.findViewById(R.id.button_view)
        var save: View? = view?.findViewById(R.id.button_done)
        var skip: View? = view?.findViewById(R.id.button_cancel)
        recyclerView = view?.findViewById(R.id.recyclerViewAvailable)
        empty = view?.findViewById(R.id.tv_empty)
        weight = view?.findViewById(R.id.tv_weight)
        isCancelable = true

        button_connect?.setOnClickListener {
            connectDevice()
        }

        save?.setOnClickListener {
            doneClicked()
        }

        skip?.setOnClickListener {

            dismiss()
        }

        button_required?.setOnClickListener {
            enableRequired()
        }

        adapters = ScanDeviceAdapter(devices)

        adapters?.setListener(object : ScanDeviceAdapter.Listener {
            override fun onConnectClicked(device: Device?) {
                log("onConnectClicked $device")
                CommunicationManager.getInstance().connectDevice(device)
            }

            override fun onCancelClicked(device: Device?) {
                log("onCancelClicked $device")
                CommunicationManager.getInstance().disconnectDevice(device)
            }

            override fun onClicked(device: Device?) {
                log("onClicked $device")
            }

        })

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapters
    }

    fun timer() {
       // Single.just("").delay(15000L, TimeUnit.MILLISECONDS)
    }

    private fun enableRequired() {
        if (isBluetooth) {
            startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                REQUEST_LOCATION2
            );
        } else {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_BLUETOOTH2
            )
        }
    }

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )
    private val REQUEST_BLUETOOTH2 = 1032
    private val REQUEST_LOCATION2 = 1033
    private var isBluetooth = false
    //private var isLocation = false
    //private var checkScan = false

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

    fun connectDevice() {

        val bl = BluetoothAdapter.getDefaultAdapter()
        if (bl != null && !bl.isEnabled) {
            tv_required_text?.setText("Enable Bluetooth to scan the device")
            tv_required_text?.visibility = View.VISIBLE
            button_required?.visibility = View.VISIBLE
            return
        }

        isBluetooth = true
        if (isLocationEnabled()) {
            PermissionHelper.requestPermission(this@ScaleScanDialog, permissions) {
                listener?.onItemClicked(0.0, 100)
                button_connect?.startAnimation()
            }
        } else {
            isBluetooth = true
            tv_required_text?.setText("Locations is required to scan the device")
            tv_required_text?.visibility = View.VISIBLE
            button_required?.visibility = View.VISIBLE
        }
    }

    fun doneClicked() {
        listener?.onItemClicked(weightKg, 200)
        dismiss()
    }

    fun updateConnected() {
        adapters?.connectedScale()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener?.onItemClicked(weightKg, 300)
        super.onDismiss(dialog)
    }


    fun update(msg: String) {
        log("Scale Weight Update $msg")
        activity?.runOnUiThread {
            if (buttonView?.visibility != View.VISIBLE)
                buttonView?.visibility = View.VISIBLE
            if (weight?.visibility != View.VISIBLE)
                weight?.visibility = View.VISIBLE
            weight?.text = msg
            isCancelable = false
            updateConnected()
        }
    }

    fun onReceive(event: Device?) {
        if (event != null ) {
            if (rl_connect?.visibility == View.VISIBLE) {
                button_connect?.dispose()
                button_connect?.visibility = View.GONE
                rl_connect?.visibility = View.GONE
            }
            adapters?.addScaleDevice(event)
            buttonView?.visibility = View.VISIBLE
        }
    }

    fun onReceive(event: DeviceStatusEvent) {
        if (rl_connect?.visibility == View.VISIBLE) {
            button_connect?.dispose()
            button_connect?.visibility = View.GONE
            rl_connect?.visibility = View.GONE
        }
        adapters?.addScaleDevice(event.device)

    }

    fun onReceive(event: NewConnectionStatus) {
        updateConnected()
    }


    fun onReceive(event: ScaleDataEvent?) {
        if (event != null) {
            if (event.data != null) {
                activity?.runOnUiThread {
                    var unit = ""
                    when (event.data.weightUnit) {
                        ScaleData.UNIT_KG -> unit = getString(R.string.kg_unit)
                        ScaleData.UNIT_LB -> unit = getString(R.string.lbs_unit)
                        ScaleData.UNIT_ST -> unit = "St"
                        ScaleData.UNIT_G -> unit = "Jin"
                    }
                    weightKg = Calculate.round(event.data?.weight?.toDouble())
                    update("$weightKg $unit")
                }

            } else {
                weightKg = Calculate.round(event.weight.toDouble())
                update("$weightKg")
            }
        }
    }


    fun onReceive(event: IndicationEvent) {
        getWeight(event.data)
    }


    fun getWeight(command: ByteArray) {
        val unit = command[0].toInt() shr 0 and 1
        val kg = unit == 0
        weightKg = Calculate.round(getWeight(command, kg))
        val si = if (kg) getString(R.string.kg_unit) else getString(R.string.kg_unit)

        update("$weightKg $si")

    }

    fun getWeight(command: ByteArray, kg: Boolean): Double {
        if (command.size > 2) {
            val unit = command[0].toInt() shr 0 and 1
            if (kg) {
                val a: Int = (command[1].toInt() and 0xff)
                var b: Int = (command[2].toInt() and 0xff)
                b *= 256
                b += a
                return b.times(0.005)
            } else {
                val a: Int = (command[1].toInt() and 0xff)
                var b: Int = (command[2].toInt() and 0xff)
                b *= 256
                b += a
                return b.times(0.01)
            }

        }
        return 0.0
    }

    fun getHeight(command: ByteArray): Double {
        if (command.size > 2) {
            val unit = command[0].toInt() shr 0 and 1
            if (unit == 0) {
                val a: Int = (command[6].toInt() and 0xff)
                var b: Int = (command[7].toInt() and 0xff)
                b *= 256
                b += a
                return b.times(0.001)
            } else {
                val a: Int = (command[6].toInt() and 0xff)
                var b: Int = (command[7].toInt() and 0xff)
                b *= 256
                b += a
                return b.times(0.1)
            }

        }
        return 0.0
    }


    fun log(msg: String) = Logger.e("ScaleScanDialog", msg)
    override fun onStop() {
        try {
            button_connect?.dispose()
        } catch (Ex: Exception) {

        }
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val height = resources?.displayMetrics?.heightPixels?.times(0.8);
            dialog?.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    height?.toInt() ?: ViewGroup.LayoutParams.WRAP_CONTENT
                )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}