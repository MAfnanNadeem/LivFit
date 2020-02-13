/*
 *  Created by Sumeet Kumar on 2/9/20 9:09 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/9/20 9:09 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.models;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class BleDevice implements Serializable, BaseModel {

    private String uid;
    private DeviceTypes type;
    private ConnectionTypes connectionType = ConnectionTypes.BLE;
    private String name = "New Device";
    private String serial;
    private BluetoothDevice device;
    private int connectionStatus = 0;

    public BleDevice(String serial, BluetoothDevice device) {
        this.serial = serial;
        this.uid = getUid(serial);
        this.device = device;
        this.type = getDeviceType(serial);
    }

    public BleDevice(String name, String serial, BluetoothDevice device, DeviceTypes type) {
        this.name = name;
        this.serial = serial;
        this.uid = getUid(serial);
        this.device = device;
        this.type = type;
    }


    public static String getUid(String name) {
        if (name != null)
            try {
                if (name.startsWith("MBRXL"))
                    return name.replace("MBRXL-", "");
                if (name.startsWith("MIBO-"))
                    return name.replace("MIBO-", "");
                if (name.startsWith("RXL"))
                    return name.replace("RXL-", "");
            } catch (Exception e) {
                e.printStackTrace();

            }
        return name;
    }

    public static DeviceTypes getDeviceType(String name) {
        if (name != null)
            try {
                if (name.toLowerCase().startsWith("mbrxl"))
                    return DeviceTypes.RXL_BLE;
                if (name.toLowerCase().startsWith("mibo-"))
                    return DeviceTypes.BLE_STIMULATOR;
                if (name.toLowerCase().startsWith("hw"))
                    return DeviceTypes.HR_MONITOR;
                if (name.toLowerCase().startsWith("geonaute"))
                    return DeviceTypes.HR_MONITOR;
                if (name.toLowerCase().startsWith("ws806"))
                    return DeviceTypes.SCALE;
            } catch (Exception e) {
                e.printStackTrace();

            }
        return DeviceTypes.GENERIC;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public DeviceTypes getType() {
        return type;
    }

    public void setType(DeviceTypes type) {
        this.type = type;
    }

    public ConnectionTypes getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionTypes connectionType) {
        this.connectionType = connectionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(int connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
}
