/*
 *  Created by Sumeet Kumar on 1/13/20 10:11 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/13/20 10:11 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.devices

import life.mibo.hardware.models.Device

interface ScanObserver {
    fun onNewDevice(device: Device)
    fun onNewDevices(devices: ArrayList<Device>)
    fun onPairedDevices(devices: ArrayList<Device>)
    fun onConnectedDevices(devices: ArrayList<Device>)
}