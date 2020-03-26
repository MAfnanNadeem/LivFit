/*
 *  Created by Sumeet Kumar on 3/22/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/22/20 5:16 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.fastble.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.os.Build;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import life.mibo.hardware.fastble.BleManager;
import life.mibo.hardware.fastble.data.BleDevice;
import life.mibo.hardware.fastble.utils.BleLruHashMap;

public class MultipleBluetoothController {

    private final BleLruHashMap<String, life.mibo.hardware.fastble.bluetooth.BleBluetooth> bleLruHashMap;
    private final HashMap<String, life.mibo.hardware.fastble.bluetooth.BleBluetooth> bleTempHashMap;

    public MultipleBluetoothController() {
        bleLruHashMap = new BleLruHashMap<>(BleManager.getInstance().getMaxConnectCount());
        bleTempHashMap = new HashMap<>();
    }

    public synchronized life.mibo.hardware.fastble.bluetooth.BleBluetooth buildConnectingBle(BleDevice bleDevice) {
        life.mibo.hardware.fastble.bluetooth.BleBluetooth bleBluetooth = new life.mibo.hardware.fastble.bluetooth.BleBluetooth(bleDevice);
        if (!bleTempHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleTempHashMap.put(bleBluetooth.getDeviceKey(), bleBluetooth);
        }
        return bleBluetooth;
    }

    public synchronized void removeConnectingBle(life.mibo.hardware.fastble.bluetooth.BleBluetooth bleBluetooth) {
        if (bleBluetooth == null) {
            return;
        }
        if (bleTempHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleTempHashMap.remove(bleBluetooth.getDeviceKey());
        }
    }

    public synchronized void addBleBluetooth(life.mibo.hardware.fastble.bluetooth.BleBluetooth bleBluetooth) {
        if (bleBluetooth == null) {
            return;
        }
        if (!bleLruHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleLruHashMap.put(bleBluetooth.getDeviceKey(), bleBluetooth);
        }
    }

    public synchronized void removeBleBluetooth(life.mibo.hardware.fastble.bluetooth.BleBluetooth bleBluetooth) {
        if (bleBluetooth == null) {
            return;
        }
        if (bleLruHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleLruHashMap.remove(bleBluetooth.getDeviceKey());
        }
    }

    public synchronized boolean isContainDevice(BleDevice bleDevice) {
        return bleDevice != null && bleLruHashMap.containsKey(bleDevice.getKey());
    }

    public synchronized boolean isContainDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && bleLruHashMap.containsKey(bluetoothDevice.getName() + bluetoothDevice.getAddress());
    }

    public synchronized BleDevice getBleDevice(String mac) {
        try {
            if (bleLruHashMap.containsKey(mac)) {
                return bleLruHashMap.get(mac).getDevice();
            }
            if (bleTempHashMap.containsKey(mac)) {
                return bleTempHashMap.get(mac).getDevice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized life.mibo.hardware.fastble.bluetooth.BleBluetooth getBleBluetooth(BleDevice bleDevice) {
        if (bleDevice != null) {
            if (bleLruHashMap.containsKey(bleDevice.getKey())) {
                return bleLruHashMap.get(bleDevice.getKey());
            }
        }
        return null;
    }

    public synchronized void disconnect(BleDevice bleDevice) {
        if (isContainDevice(bleDevice)) {
            getBleBluetooth(bleDevice).disconnect();
        }
    }

    public synchronized void disconnectAllDevice() {
        for (Map.Entry<String, life.mibo.hardware.fastble.bluetooth.BleBluetooth> stringBleBluetoothEntry : bleLruHashMap.entrySet()) {
            stringBleBluetoothEntry.getValue().disconnect();
        }
        bleLruHashMap.clear();
    }

    public synchronized void destroy() {
        for (Map.Entry<String, life.mibo.hardware.fastble.bluetooth.BleBluetooth> stringBleBluetoothEntry : bleLruHashMap.entrySet()) {
            stringBleBluetoothEntry.getValue().destroy();
        }
        bleLruHashMap.clear();
        for (Map.Entry<String, life.mibo.hardware.fastble.bluetooth.BleBluetooth> stringBleBluetoothEntry : bleTempHashMap.entrySet()) {
            stringBleBluetoothEntry.getValue().destroy();
        }
        bleTempHashMap.clear();
    }

    public synchronized List<life.mibo.hardware.fastble.bluetooth.BleBluetooth> getBleBluetoothList() {
        List<life.mibo.hardware.fastble.bluetooth.BleBluetooth> bleBluetoothList = new ArrayList<>(bleLruHashMap.values());
        Collections.sort(bleBluetoothList, new Comparator<life.mibo.hardware.fastble.bluetooth.BleBluetooth>() {
            @Override
            public int compare(life.mibo.hardware.fastble.bluetooth.BleBluetooth lhs, life.mibo.hardware.fastble.bluetooth.BleBluetooth rhs) {
                return lhs.getDeviceKey().compareToIgnoreCase(rhs.getDeviceKey());
            }
        });
        return bleBluetoothList;
    }

    public synchronized List<BleDevice> getDeviceList() {
        refreshConnectedDevice();
        List<BleDevice> deviceList = new ArrayList<>();
        for (life.mibo.hardware.fastble.bluetooth.BleBluetooth BleBluetooth : getBleBluetoothList()) {
            if (BleBluetooth != null) {
                deviceList.add(BleBluetooth.getDevice());
            }
        }
        return deviceList;
    }

    public synchronized ArrayList<BluetoothDevice> getBluetoothDeviceList() {
        refreshConnectedDevice();
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
        for (life.mibo.hardware.fastble.bluetooth.BleBluetooth BleBluetooth : getBleBluetoothList()) {
            if (BleBluetooth != null) {
                deviceList.add(BleBluetooth.getDevice().getDevice());
            }
        }
        return deviceList;
    }

    public void refreshConnectedDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<life.mibo.hardware.fastble.bluetooth.BleBluetooth> bluetoothList = getBleBluetoothList();
            for (int i = 0; bluetoothList != null && i < bluetoothList.size(); i++) {
                BleBluetooth bleBluetooth = bluetoothList.get(i);
                if (!BleManager.getInstance().isConnected(bleBluetooth.getDevice())) {
                    removeBleBluetooth(bleBluetooth);
                }
            }
        }
    }


}
