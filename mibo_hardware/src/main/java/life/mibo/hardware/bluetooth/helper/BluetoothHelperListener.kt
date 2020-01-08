package c.tlgbltcn.library

import android.bluetooth.BluetoothDevice

/**
 * Created by SumeetGehi on 09.12.2019
 */
interface BluetoothHelperListener {

    fun onStartDiscovery()

    fun onFinishDiscovery()

    fun onEnabledBluetooth()

    fun onDisabledBluetooh()

    fun onDeviceFound(device: BluetoothDevice?)
}