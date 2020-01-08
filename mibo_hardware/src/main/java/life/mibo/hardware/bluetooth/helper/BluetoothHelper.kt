package c.tlgbltcn.library

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.os.Build

/**
 * Created by SumeetGehi on 09.12.2019
 */
class BluetoothHelper(private val context: Context, private val listener: BluetoothHelperListener) {

    object BluetoothHelperConstant {

        const val ACCESS_FINE_LOCATION = "Manifest.permission.ACCESS_FINE_LOCATION"
        const val ACCESS_COARSE_LOCATION = "Manifest.permission.ACCESS_COARSE_LOCATION"
        const val REQ_CODE = 1001
    }

    private val bluetoothAdapter by lazy {
        return@lazy BluetoothAdapter.getDefaultAdapter()
    }

    private var isRequiredPermission = false

    private var isEnabled = bluetoothAdapter.isEnabled

    private var isDiscovering = bluetoothAdapter.isDiscovering

    private val bluetoothStateChangeReceiver by lazy {
        object : BluetoothStateChangeReceiver() {
            override fun onStartDiscovering() {
                isDiscovering = true
                listener.onStartDiscovery()
            }

            override fun onFinishDiscovering() {
                isDiscovering = false
                context.unregisterReceiver(bluetoothDeviceFounderReceiver)
                listener.onFinishDiscovery()
            }

            override fun onEnabledBluetooth() {
                isEnabled = true
                listener.onEnabledBluetooth()
            }

            override fun onDisabledBluetooth() {
                isEnabled = false
                listener.onDisabledBluetooh()
            }
        }
    }

    private val bluetoothDeviceFounderReceiver by lazy {
        object : BluetoothDeviceFounderReceiver() {
            override fun getFoundDevices(device: BluetoothDevice) {
                listener.onDeviceFound(device)
            }
        }
    }

    fun isBluetoothEnabled() = isEnabled

    fun isBluetoothScanning() = isDiscovering

    fun enableBluetooth() {
        if (!isEnabled) bluetoothAdapter.enable()
    }

    fun disableBluetooth() {
        if (isEnabled) bluetoothAdapter.disable()
    }

    fun registerBluetoothStateChanged() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(bluetoothStateChangeReceiver, intentFilter)
    }

    fun unregisterBluetoothStateChanged() {
        context.unregisterReceiver(bluetoothStateChangeReceiver)
    }

    fun startDiscovery() {
        if (isEnabled && !isDiscovering) {
            bluetoothAdapter.startDiscovery()
            val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
            context.registerReceiver(bluetoothDeviceFounderReceiver, discoverDevicesIntent)
        }
    }

    fun stopDiscovery() {
        if (isEnabled && isDiscovering) {
            bluetoothAdapter.cancelDiscovery()

            if (!bluetoothAdapter.isDiscovering && bluetoothDeviceFounderReceiver.isOrderedBroadcast) {
                context.unregisterReceiver(bluetoothDeviceFounderReceiver)
            }
        }
    }

    fun onStop() {
        startDiscovery()
        unregisterBluetoothStateChanged()
    }

    private fun checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            var permissionCheck =
                context.checkSelfPermission(BluetoothHelperConstant.ACCESS_FINE_LOCATION)
            permissionCheck += context.checkSelfPermission(BluetoothHelperConstant.ACCESS_COARSE_LOCATION)

            if (permissionCheck != 0)
                (context as Activity).requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), BluetoothHelperConstant.REQ_CODE
                )
        }
    }

    fun setPermissionRequired(isRequired: Boolean): BluetoothHelper {
        this.isRequiredPermission = isRequired
        return this
    }

    fun create(): BluetoothHelper {
        if (this.isRequiredPermission) checkBTPermissions()
        return BluetoothHelper(context, listener)
    }
}