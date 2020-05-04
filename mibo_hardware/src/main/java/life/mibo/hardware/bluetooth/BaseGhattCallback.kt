/*
 *  Created by Sumeet Kumar on 5/2/20 3:26 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/2/20 3:26 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import life.mibo.hardware.core.Logger
import java.util.*

open class BaseGhattCallback : BluetoothGattCallback() {
    var mIsDeviceConnected = false
    private var mCommandCharacteristic: BluetoothGattCharacteristic? = null
    open val TAG = "BaseGhattCallback"

    private val SUBSCRIBE_CHARACTERISTIC: Int = 0
    private val READ_CHARACTERISTIC: Int = 1
    private val WRITE_CHARACTERISTIC: Int = 2

    fun queueReadDataFromCharacteristic(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        Logger.e(TAG, "queueReadDataFromCharacteristic")
        val ghattQueueItem = GhattQueueItem()
        ghattQueueItem.characteristic = characteristic
        ghattQueueItem.ghatt = gatt
        ghattQueueItem.ghattQueueItemType = READ_CHARACTERISTIC
        addToGhattQueue(ghattQueueItem)
    }


    fun queueSetNotificationForCharacteristic(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        Logger.e(TAG, "queueSetNotificationForCharacteristic")
        val ghattQueueItem = GhattQueueItem()
        ghattQueueItem.characteristic = characteristic
        ghattQueueItem.enabled = true
        ghattQueueItem.ghatt = gatt
        ghattQueueItem.ghattQueueItemType = SUBSCRIBE_CHARACTERISTIC
        addToGhattQueue(ghattQueueItem)
    }

    fun queueWriteDataToCharacteristic(gatt: BluetoothGatt, dataToWrite: ByteArray) {
        Logger.e(TAG, "queueWriteDataToCharacteristic")
        val txQueueItem = GhattQueueItem()
        txQueueItem.ghatt = gatt
        txQueueItem.dataToWrite = dataToWrite
        txQueueItem.ghattQueueItemType = WRITE_CHARACTERISTIC
        addToGhattQueue(txQueueItem)
    }

    fun sendBatteryCommand(gatt: BluetoothGatt) {
        Logger.e(TAG, "Send battery command!")
        val olddata = byteArrayOf(0x2A19.toByte(), 0x00, 0x00, 0x00, 0x00)
        queueWriteDataToCharacteristic(gatt, olddata)
    }

    private var ghattQueueProcessing: Boolean = false

    private fun addToGhattQueue(ghattQueueItem: GhattQueueItem) {
        ghattQueue.add(ghattQueueItem)
        if (!ghattQueueProcessing) {
            processTxQueue()
        }
    }

    protected fun processTxQueue() {
        Logger.e(TAG, "processTxQueue isProcessing $ghattQueueProcessing and size is ${ghattQueue.size}")
        if (ghattQueue.size <= 0) {
            ghattQueueProcessing = false
            return
        }
        ghattQueueProcessing = true
        val ghattQueueItem = ghattQueue.remove()
        when (ghattQueueItem.ghattQueueItemType) {
            WRITE_CHARACTERISTIC -> writeDataToCharacteristic(ghattQueueItem)
            SUBSCRIBE_CHARACTERISTIC -> setNotificationForCharacteristic(ghattQueueItem)
            READ_CHARACTERISTIC -> readDataFromCharacteristic(ghattQueueItem)
        }
    }

    private fun readDataFromCharacteristic(ghattQueueItem: GhattQueueItem?) {
        Logger.e(TAG, " readDataFromCharacteristic")
        ghattQueueItem?.ghatt?.readCharacteristic(ghattQueueItem.characteristic)
    }

    private fun setNotificationForCharacteristic(ghattQueueItem: GhattQueueItem?) {
        Logger.e(TAG, " setNotificationForCharacteristic")
        if (ghattQueueItem != null) {
            val gatt = ghattQueueItem.ghatt
            val characteristic = ghattQueueItem.characteristic
            if (gatt != null && characteristic != null) {
                gatt.setCharacteristicNotification(characteristic, true)
                // This is specific to Heart Rate Measurement.
                val descriptor = characteristic.getDescriptor(
                    UUID.fromString("DISCRIPTOR_ID"))
                if (descriptor != null) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                    gatt.writeDescriptor(descriptor)
                } else {
                    Logger.e(TAG, "descriptor is NULL")
                }
            } else {
                Logger.e(TAG, "Gatt or Charactertisic is NULl")
            }
        } else {
            Logger.e(TAG, "GhattQueueItem is NULL")
        }
    }

    private fun writeDataToCharacteristic(ghattQueueItem: GhattQueueItem) {
        Logger.e(TAG, " writeDataToCharacteristic")
        if (mCommandCharacteristic != null) {
            mCommandCharacteristic!!.value = ghattQueueItem.dataToWrite
            val gatt = ghattQueueItem.ghatt
            if (gatt != null) {
                gatt.writeCharacteristic(mCommandCharacteristic)
            } else {
                Logger.e(TAG, "Gatt is NULL")
            }
        } else {
            Logger.e(TAG, "mCommandCharacteristic is null")
            ghattQueueProcessing = false
        }
    }

    private val ghattQueue = LinkedList<GhattQueueItem>()

    private inner class GhattQueueItem {
        internal var ghatt: BluetoothGatt? = null
        internal var characteristic: BluetoothGattCharacteristic? = null
        internal var dataToWrite: ByteArray? = null // Only used for characteristic write
        internal var enabled: Boolean = false // Only used for characteristic notification subscription
        var ghattQueueItemType: Int? = null
    }
}