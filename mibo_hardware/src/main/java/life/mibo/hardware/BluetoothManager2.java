/*
 *  Created by Sumeet Kumar on 3/22/20 9:07 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/22/20 9:07 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.onecoder.devicelib.FitBleKit;
import com.onecoder.devicelib.base.control.interfaces.DeviceStateChangeCallback;
import com.onecoder.devicelib.base.entity.BaseDevice;
import com.onecoder.devicelib.base.entity.DeviceType;
import com.onecoder.devicelib.base.protocol.entity.ScaleStableData;
import com.onecoder.devicelib.scale.api.ScaleManager;
import com.onecoder.devicelib.scale.api.interfaces.ScaleInfoCallBack;
import com.onecoder.devicelib.scale.api.interfaces.ScaleNewProtocolDataListener;
import com.onecoder.devicelib.scale.api.interfaces.ScaleUserInfoCallBack;
import com.onecoder.devicelib.scale.protocol.entity.BUserInfo;
import com.onecoder.devicelib.scale.protocol.entity.ScaleInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kotlin.jvm.Synchronized;
import life.mibo.hardware.bluetooth.BleGattManager;
import life.mibo.hardware.bluetooth.BleScanCallback;
import life.mibo.hardware.bluetooth.OnBleCharChanged;
import life.mibo.hardware.bluetooth.OnBleDeviceDiscovered;
import life.mibo.hardware.bluetooth.gatt.AllGattCharacteristics;
import life.mibo.hardware.bluetooth.gatt.AllGattDescriptors;
import life.mibo.hardware.bluetooth.gatt.AllGattServices;
import life.mibo.hardware.constants.Config;
import life.mibo.hardware.core.DataParser;
import life.mibo.hardware.core.Utils;
import life.mibo.hardware.encryption.Encryption;
import life.mibo.hardware.fastble.BleManager;
import life.mibo.hardware.fastble.callback.BleIndicateCallback;
import life.mibo.hardware.fastble.callback.BleMtuChangedCallback;
import life.mibo.hardware.fastble.callback.BleNotifyCallback;
import life.mibo.hardware.fastble.callback.BleReadCallback;
import life.mibo.hardware.fastble.callback.BleScanAndConnectCallback;
import life.mibo.hardware.fastble.callback.BleWriteCallback;
import life.mibo.hardware.fastble.data.BleDevice;
import life.mibo.hardware.fastble.exception.BleException;
import life.mibo.hardware.models.DeviceTypes;
import life.mibo.hardware.models.ScaleData;

/**
 * Created by Fer on 08/04/2019.
 * Modified by Sumeet
 */

public class BluetoothManager2 {

    //private BluetoothLeScanner bluetoothLeScanner;
    // private BluetoothAdapter mBluetoothAdapter;
    private OnBleDeviceDiscovered listener = null;
    private OnBleCharChanged onBleCharChanged = null;
    //private BleScanCallback bleScanCallback;
    private BleGattManager.OnConnection bleGatListener = null;
   // public static final int INDICATE = 100;


    public void setBleGatListener(BleGattManager.OnConnection bleGatListener) {
        // this.bleGatListener = bleGatListener;
    }

    public void setOnBleCharChanged(OnBleCharChanged onBleCharChanged) {
        this.onBleCharChanged = onBleCharChanged;
    }

    public void setListener(OnBleDeviceDiscovered listener) {
        this.listener = listener;
    }


    public BluetoothManager2(Context context) {
        //this.activity = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initBlueTooth() {
        BleManager.getInstance().enableBluetooth();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanDevice(BleScanCallback callback) {
        //this.bleScanCallback = callback;
        scanDevice();
    }

    @Synchronized
    public void scanDevice() {
        BleManager.getInstance().scan(new life.mibo.hardware.fastble.callback.BleScanCallback() {
            @Override
            public void onScanFinished(List<life.mibo.hardware.fastble.data.BleDevice> scanResultList) {
                log("FastBle: onScanFinished " + scanResultList);
            }

            @Override
            public void onScanStarted(boolean success) {
                log("FastBle: onScanStarted success=" + success);

            }

            @Override
            public void onScanning(BleDevice device) {
                log("FastBle: onScanning bleDevice" + device);
                //bleRxlDiscovered(bleDevice.getMac(), bleDevice.getMac(), bleDevice.getName()+);
                String name = device.getName();
                if (name == null)
                    return;
                log("FastBle: onScanning name " + name);
                if (name.contains("MBRXL")) {
                    device.setType(DeviceTypes.RXL_BLE);
                    listener.bleRXLDiscovered(Utils.getUid(device.getName()), device.getName(), "RXL ");
                    deviceHashMap.put(device.getName(), device);
                    return;
                }

                if (name.contains("MIBO-")) {
                    device.setType(DeviceTypes.BLE_STIMULATOR);
                    listener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-", ""), device.getName());
                    deviceHashMap.put(device.getName(), device);
                    return;
                }

                if (device.getName().contains("HW") || device.getName().contains("Geonaute")) {
                    device.setType(DeviceTypes.HR_MONITOR);
                    // devicesHRBle.add(device);
                    listener.bleHrDeviceDiscovered(device.getMac(), device.getName());
                    deviceHashMap.put(device.getMac(), device);
                    return;
                }

                if (device.getName().contains("MI SCALE") || device.getName().contains("WS806")) {
                    device.setType(DeviceTypes.SCALE);
                    // devicesHRBle.add(device);
                    listener.bleScaleDeviceDiscovered(device.getMac(), device.getName());
                    deviceHashMap.put(device.getMac(), device);
                    return;
                }

                if (device.getName().contains("WGNBPA960BT")) {
                    device.setType(DeviceTypes.WGNBPA960BT);
                    // devicesHRBle.add(device);
                    listener.bleScaleDeviceDiscovered(device.getMac(), device.getName());
                    deviceHashMap.put(device.getMac(), device);
                    return;
                }
                if (device.getName().contains("Contour7801H6669461")) {
                    device.setType(DeviceTypes.Contour7801H6669461);
                    // devicesHRBle.add(device);
                    listener.bleScaleDeviceDiscovered(device.getMac(), device.getName());
                    deviceHashMap.put(device.getMac(), device);
                    return;
                }

            }
        });
    }

    public void stopScanDevice() {
        BleManager.getInstance().cancelScan();
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private ScanCallback mLeHrSensorScanCallback = new ScanCallback() {
//
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//            //if (bleScanCallback != null)
//            //    bleScanCallback.onDevice(B);
//            log("BluetoothManager onScanResult " + result);
//            BluetoothDevice device = result.getDevice();
//            if (device == null || device.getName() == null)
//                return;
//            log("BluetoothManager getName " + result.getDevice().getName());
//            //mBluetoothAdapter.getRemoteDevice(result)
//
//            // TODO Session is Boosted/Wifi remove later isBoosterMode()
//            if (device.getName() != null && device.getName().contains("MBRXL")) {
//                //rxlCount++;
//                // devicesBoosterBle.put(device.getName(), device);
//                //addBleDevices(device);
//                listener.bleRXLDiscovered(Utils.getUid(device.getName()), device.getName(), "RXL ");
//                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
//                log(" onScanResult3 " + device.getName() + "   " + device.getClass());
//            }
//
//            if (device.getName() != null && device.getName().contains("MIBO-")) {
////                for (BluetoothDevice b : devicesBoosterBle.values()) {
////                    if (b.getName().equalsIgnoreCase(device.getName())) {
////                        return;
////                    }
////                }
////                if (device.getName().startsWith("MIBO-RXL")) {
////                    listener.bleRXLDiscovered(Utils.getUid(device.getName()), device.getName(), "RXL- " + devicesBoosterBle.size());
////                    devicesBoosterBle.add(device);
////                    return;
////                }
//                //devicesBoosterBle.put(device.getName(), device);
//                listener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-", ""), device.getName());
//                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
//                log(" onScanResult4 " + device.getName() + "   " + device.getClass());
//            }
//
////            if (device.getName() != null && !devicesHRBle.contains(device) && (device.getName().contains("HW") || device.getName().contains("Geonaute"))) {
////                //devicesHRBle.add(device);
////                listener.bleHrDeviceDiscovered(device.toString(), device.getName());
////                log(" onScanResult2 " + device.getName() + "   " + device.getClass());
////                return;
////            }
//
//
////            if (!devicesBoosterBle.contains(device) && !SessionManager.getInstance().getSession().isBoosterMode() && device.getName() != null && device.getName().contains("MIBO-")) {
////                devicesBoosterBle.add(device);
////                listener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-", ""), device.getName());
////                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
////                log("BluetoothManager onScanResult3 " + device.getName() + "   " + device.getClass());
////            }
//
////            if (!devicesScaleBle.contains(device) && device.getName() != null && (device.getName().contains("WS806"))) {
////                devicesScaleBle.add(device);
////                listener.bleScaleDeviceDiscovered(device.toString(), device.getName());
////                log(" onScanResult4 " + device.getName() + "   " + device.getClass());
////                return;
////            }
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//            log("BluetoothManager onBatchScanResults " + results);
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            super.onScanFailed(errorCode);
//            log("BluetoothManager onScanFailed " + errorCode);
//        }
//    };


    public void connectScale(String Id) {
        connectBleFast(Id);
    }


    private ScaleManager scaleManager;

    public void destroy() {
        try {
            if (scaleManager != null) {
                scaleManager.closeDevice();
            }
        } catch (Exception ee) {

        }
    }

    private ScaleManager getScaleManager() {
        log("ScaleManager getScaleManager");
        if (scaleManager == null) {
            FitBleKit.getInstance().initSDK(MIBO.getContext());
            scaleManager = ScaleManager.getInstance();
            scaleManager.setUnit(ScaleStableData.UNIT_KG);

            scaleManager.registerStateChangeCallback(new DeviceStateChangeCallback() {
                @Override
                public void onStateChange(String s, int i) {
                   // log("ScaleManager DeviceStateChangeCallback onStateChange " + i);
                    if(listener != null)
                        listener.onConnect(s, i);
                }

                @Override
                public void onEnableWriteToDevice(String s, boolean b) {
                    //log("ScaleManager DeviceStateChangeCallback onEnableWriteToDevice " + s + " b="+b);
                }
            });
            scaleManager.setNewScaleDataListener(new ScaleNewProtocolDataListener() {
                @Override
                public void onRealTimeData(float v) {
                    log("ScaleManager setNewScaleDataListener onRealTimeData " + v);
                    if (onBleCharChanged != null)
                        onBleCharChanged.bleScale(v, null, Config.SCALE_LEGACY, null);
                }

                @Override
                public void onStableWeightData(ScaleStableData scaleStableData) {
                    log("ScaleManager setNewScaleDataListener onStableWeightData " + scaleStableData);
                    if (onBleCharChanged != null)
                        onBleCharChanged.bleScale(0, ScaleData.from(scaleStableData), Config.SCALE_LEGACY, null);
                }

                @Override
                public void onHistoryWeightData(List<ScaleStableData> list) {
                    log("ScaleManager setNewScaleDataListener onHistoryWeightData " + list);
                }
            });

            scaleManager.setScaleInfoCallBack(new ScaleInfoCallBack() {
                @Override
                public void onSucess(ScaleInfo scaleInfo) {
                    log("ScaleManager ScaleInfoCallBack onSucess " + scaleInfo);
                }
            });

            scaleManager.setScaleUserInfoCallBack(new ScaleUserInfoCallBack() {
                @Override
                public void onSucess(List<BUserInfo> list) {
                    log("ScaleManager getScaleManager ScaleUserInfoCallBack " + list);
                }

                @Override
                public void onSuccessGetID(String s, int i) {
                    log("ScaleManager getScaleManager ScaleUserInfoCallBack onSuccessGetID " + i);
                }
            });
        }

        return scaleManager;
    }

    public void disconnectScaleLegacy(String Id) {
        getScaleManager().disconnect(true);
    }

    public void connectScaleLegacy(String Id) {
        log("ScaleManager connectScaleLegacy");
        try {
            BleDevice device = getBle(Id);
            if (device != null) {
                BaseDevice scale = new BaseDevice();
                scale.setDeviceType(DeviceType.Scale);
                scale.setMacAddress(device.getMac());
                scale.setName(device.getName());
                getScaleManager().connectDevice(scale);
            }
        } catch (Exception ee) {

        }
    }

    public void connectHrGattDevice(String Id) {
        connectBleFast(Id);
//        log("connectHrGattDevice Id " + Id);
//        boolean newDevice = true;
//        for (BluetoothDevice t : devicesConnectedBle) {
//            if (t.toString().equals(Id)) {
//                newDevice = false;
//            }
//        }
//        if (newDevice) {
//            for (BluetoothDevice d : devicesHRBle) {
//                if (d.toString().equals(Id)) {
//                    devicesConnectedBle.add(d);
//
//
//                    getGattManager().queue(new GattSetNotificationOperation(d,
//                            BleGattManager.HEART_RATE_SERVICE_UUID,
//                            BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID,
//                            BleGattManager.CLIENT_UUID));
//                    addGattListenerHR(d, mGattManager);
//                }
//            }
//        }
    }

    void connectDevice(BluetoothDevice device) {
//        if (device != null && device.getName() != null) {
//
//            getGattManager().queue(new GattCharacteristicWriteOperation(device,
//                    BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                    BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
//                    new byte[0]));
//            addGattListenerBooster(mGattManager, device);
////                        mGattManager.queue(new GattSetNotificationOperation(d,
////                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
////                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
////                                BleGattManager.CLIENT_UUID));
//
//
//            log("BluetoothManager booster charr add " + device.getName());
//        }

    }

    void connectMIBOBoosterGattDevice(String Id) {
        connectBleFast(Id);
//        log("connectMIBOBoosterGattDevice " + Id);
//        boolean newDevice = true;
//        for (BluetoothDevice t : devicesConnectedBle) {
//            if (t.getName() != null)
//                if (t.getName().contains(Id)) {
//                    newDevice = false;
//                }
//        }
//        if (newDevice) {
//            for (BluetoothDevice d : devicesBoosterBle.values()) {
//                if (d.getName() != null)
//                    if (d.getName().contains(Id)) {
//                        devicesConnectedBle.add(d);
//
//                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
//                                new byte[0]));
//                        addGattListenerBooster(mGattManager, d);
////                        mGattManager.queue(new GattSetNotificationOperation(d,
////                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
////                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
////                                BleGattManager.CLIENT_UUID));
//
//
//                        log("BluetoothManager booster charr add " + Id);
//                    }
//            }
//        }

    }

    void connectRXLGattDevice(String Id) {
        connectBleFast(Id);
//        log("connectRXLGattDevice id " + Id);
////        if (isTest) {
////            testBleConnection(Id);
////            return;
////        }
//        boolean newDevice = true;
//        for (BluetoothDevice t : devicesConnectedBle) {
//            if (t.getName() != null)
//                if (t.getName().contains(Id)) {
//                    newDevice = false;
//                }
//        }
//        log("connectRXLGattDevice newDevice " + newDevice + " size:" + devicesBoosterBle.size());
//        if (newDevice) {
//            try {
//                for (BluetoothDevice d : devicesBoosterBle.values()) {
//                    if (d.getName() != null) {
//                        log("connectRXLGattDevice name: " + d.getName());
//                        if (d.getName().toLowerCase().contains(Id.toLowerCase())) {
//                            log("connectRXLGattDevice matched " + d.getName());
//                            devicesConnectedBle.add(d);
//
//                            // Not work with MIBO_RXL_SERVICE_CHAR_UUID , MIBO_RXL_TRANSMISSION_CHAR_UUID
////                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
////                                BleGattManager.MIBO_RXL_SERVICE_CHAR_UUID,
////                                BleGattManager.MIBO_RXL_TRANSMISSION_CHAR_UUID,
////                                new byte[0]));
//
//                            getGattManager().queue(new GattCharacteristicWriteOperation(d,
//                                    BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                    BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
//                                    new byte[0]));
//                            addGattListenerRXL2(mGattManager, d);
//                            //log("connectRXLGattDevice " + Id);
////                        mGattManager.queue(new GattSetNotificationOperation(d,
////                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
////                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
////                                BleGattManager.CLIENT_UUID));
//
//
//                            log("connectRXLGattDevice RXL connected " + Id);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                log("ERROR : " + e.getMessage());
//                Iterator<BluetoothDevice> iter = devicesBoosterBle.values().iterator();
//
//                while (iter.hasNext()) {
//                    BluetoothDevice d = iter.next();
//
//                    if (d.getName() != null) {
//                        log("connectRXLGattDevice name: " + d.getName());
//                        if (d.getName().toLowerCase().contains(Id.toLowerCase())) {
//                            log("connectRXLGattDevice matched ");
//                            devicesConnectedBle.add(d);
//
//                            // Not work with MIBO_RXL_SERVICE_CHAR_UUID , MIBO_RXL_TRANSMISSION_CHAR_UUID
////                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
////                                BleGattManager.MIBO_RXL_SERVICE_CHAR_UUID,
////                                BleGattManager.MIBO_RXL_TRANSMISSION_CHAR_UUID,
////                                new byte[0]));
//
//                            getGattManager().queue(new GattCharacteristicWriteOperation(d,
//                                    BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                    BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
//                                    new byte[0]));
//                            addGattListenerRXL2(mGattManager, d);
//                            //log("connectRXLGattDevice " + Id);
////                        mGattManager.queue(new GattSetNotificationOperation(d,
////                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
////                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
////                                BleGattManager.CLIENT_UUID));
//
//
//                            log("connectRXLGattDevice RXL connected " + Id);
//                        }
//                    }
//
//                    //if (someCondition)
//                    //  iter.remove();
//                }
//            }
//
//        }

    }

    int disconnectMIBOBoosterGattDevice(String Id) {
        disconnectBleFast(Id);
//        log("disconnectMIBOBoosterGattDevice " + Id);
//
//        int aux = -1;
//        for (BluetoothDevice d : devicesConnectedBle) {
//            if (d.getName().contains(Id)) {
//
//                getGattManager().queue(new GattDisconnectOperation(d));
//                aux = devicesConnectedBle.indexOf(d);
//                //devicesConnectedBle.remove(d);
//            }
//        }
//        if (aux != -1)
//            devicesConnectedBle.remove(aux);
//        return aux;
        return 0;
    }

    void disconnectHrGattDevice(String Id) {
        disconnectBleFast(Id);
//        for (BluetoothDevice d : devicesHRBle) {
//            if (d.toString().equals(Id)) {
//                devicesConnectedBle.remove(d);
//                getGattManager().queue(new GattDisconnectOperation(d));
//                SessionManager.getInstance().getUserSession().removeDevice(Id);
//            }
//        }
    }


    private void addGattListenerBooster(BleGattManager gatt, final BluetoothDevice d) {
        log("addGattListenerBooster " + d);
//        gatt.addCharacteristicChangeListener(BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID, new CharacteristicChangeListener() {
//            @Override
//            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
//                if (d.toString().equals(deviceAddress)) {
//                    if (characteristic == null && d.getName().contains("MIBO-")) {
//                        Logger.e("BluetoothManager CONNECT TO CHAR");
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));
//                    } else {
//                        final byte[] data = characteristic.getValue();
//                        if (data != null && data.length > 0) {
//                            onBleCharChanged.bleBoosterChanged(data
//                                    , d.getName().replace("MIBO-", ""));//deviceAddress);
//                            //  Log.e("gattboostlistener", "booster charr: " + data.length);
//                        }
//                    }
//                }
//            }
//        });
    }

    // not receive events with MIBO_RXL_RECEPTION_CHAR_UUID , MIBO_RXL_RECEPTION_CHAR_UUID
    private void addGattListenerRXL(BleGattManager gatt, final BluetoothDevice d) {
        log("addGattListenerRXL " + d);
//        gatt.addCharacteristicChangeListener(BleGattManager.MIBO_RXL_RECEPTION_CHAR_UUID, new CharacteristicChangeListener() {
//            @Override
//            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
//                log("addGattListenerRXL deviceAddress: " + deviceAddress);
//                if (d.toString().equals(deviceAddress)) {
//                    if (characteristic == null) {
//                        log("BluetoothManager CONNECT TO CHAR");
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_RXL_SERVICE_CHAR_UUID,
//                                BleGattManager.MIBO_RXL_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));
//                    } else {
//                        final byte[] data = characteristic.getValue();
//                        if (data != null && data.length > 0) {
//                            onBleCharChanged.bleBoosterChanged(data, Utils.getUid(d.getName()));//deviceAddress);
//                            //  Log.e("gattboostlistener", "booster charr: " + data.length);
//                        }
//                    }
//                }
//            }
//        });
    }

    private void addGattListenerRXL2(BleGattManager gatt, final BluetoothDevice d) {
        log("addGattListenerRXL " + d);
//        gatt.addCharacteristicChangeListener(BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID, new CharacteristicChangeListener() {
//            @Override
//            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
//                if (d.toString().equals(deviceAddress)) {
//                    if (characteristic == null && d.getName().contains("MBRXL-")) {
//                        log("BluetoothManager CONNECT TO CHAR");
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));
//                    } else {
//                        final byte[] data = characteristic.getValue();
//                        if (data != null && data.length > 0) {
//                            onBleCharChanged.bleBoosterChanged(data, Utils.getUid(d.getName()));//deviceAddress);
//                            //  Log.e("gattboostlistener", "booster charr: " + data.length);
//                        }
//                    }
//                }
//            }
//        });
    }

    public void sendMessage(String Id, byte[] message, String tag, int type) {
        log(tag + ": sendMessage uid:" + Id + " ,data: " + Arrays.toString(message));
        if (type == DataParser.RXL)
            sendMessageToRxt(Id, message);
        else
            sendMessageToBooster(Id, message);
    }

    void sendMessage(String Id, byte[] message, String tag) {
        log(tag + ": sendMessage uid:" + Id + " ,data: " + Arrays.toString(message));
        sendMessageToBooster(Id, message);
    }


    private void sendMessageToRxt(String Id, byte[] message) {
        Encryption.mbp_encrypt(message, message.length);
        writeBleFastNonSync(Id, message, BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(),
                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID.toString());
    }

    private void sendMessageToBooster(String Id, byte[] message) {
        //log("sendToMIBOBoosterGattDevice uid:" + Id + " ,data: " + Arrays.toString(message));
        Encryption.mbp_encrypt(message, message.length);
        writeBleFast(Id, message, BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(),
                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID.toString());
        log("sendToMIBOBoosterGattDevice end");
//        if (!TextUtils.isEmpty(Id)) {
//            log("sendToMIBOBoosterGattDevice data: " + Utils.getBytes(message));
//            Encryption.mbp_encrypt(message, message.length);
//            for (BluetoothDevice d : devicesBoosterBle.values()) {
//                if (d.getName() != null)
//                    if (d.getName().contains(Id)) {
//                        log("sendToMIBOBoosterGattDevice ID MATCHED: " + d.getName());
//                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
//                                message));
//
//                    } else {
//                        log("sendToMIBOBoosterGattDevice ID NOT MATCHED: " + d.getName());
//                    }
//            }
//        } else {
//            log("sendToMIBOBoosterGattDevice Id is empty...........");
//        }
    }

    void sendPingToBoosterGattDevice(byte[] message, BluetoothDevice d) {
        log(" sendPingToBoosterGattDevice byte: " + Arrays.toString(message));
        log(" sendPingToBoosterGattDevice char: " + Arrays.toString(new String(message).toCharArray()));
        Encryption.mbp_encrypt(message, message.length);
        writeBleFast(d.getName(), message, BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(),
                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID.toString());
//        getGattManager().queue(new GattCharacteristicWriteOperation(d,
//                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
//                message));

    }


    private void addGattListenerHR(final BluetoothDevice d, BleGattManager gatt) {
//        gatt.addCharacteristicChangeListener(BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID, new CharacteristicChangeListener() {
//            @Override
//            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
//                if (characteristic == null && (d.getName().contains("HW") || d.getName().contains("Geonaute"))) {
//                    log("BluetoothManager CONNECT TO CHAR");
//                    mGattManager.queue(new GattSetNotificationOperation(d,
//                            BleGattManager.HEART_RATE_SERVICE_UUID,
//                            BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID,
//                            BleGattManager.CLIENT_UUID));
//                } else {
//                    final byte[] data = characteristic.getValue();
//                    if (data != null && data.length > 0) {
//
//                        int HR = (int) extractHeartRate(characteristic);
//                        onBleCharChanged.bleHrChanged(HR
//                                , deviceAddress);
//                        //Log.e("HRlistener", "hr: " + HR);
//                    }
//                }
//
//            }
//        });
    }

//    public boolean connected(BluetoothDevice b) {
//        try {
//            android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//            if ((bluetoothManager.getConnectionState(b, BluetoothProfile.GATT)) {
//
//            }
//        } catch (Exception e) {
//
//        }
//        return false;
//    }

    ArrayList<BluetoothDevice> getConnectedBleDevices() {
        return BleManager.getInstance().getAllConnectedBluetooths();
    }

    public List<BluetoothDevice> getConnectedDevices(Activity activity) {
        List<BluetoothDevice> devices = new ArrayList<>();
        try {
            android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            devices.addAll(bluetoothManager.getConnectedDevices(BluetoothProfile.GATT));
            for (BluetoothDevice device : devices) {
                if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {

                }
            }
        } catch (Exception e) {

        }
        return devices;

    }

    synchronized void refreshList() {
//        List<BluetoothDevice> connected = new ArrayList<>();
//        List<BluetoothDevice> removed = new ArrayList<>();
//        try {
//            android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
//
//            connected.addAll(bluetoothManager.getConnectedDevices(BluetoothProfile.GATT));
//            for (BluetoothDevice device : connected) {
//                if (devicesBoosterBle.size() > 0) {
//                    boolean contains = false;
//                    for (BluetoothDevice d : devicesBoosterBle.values()) {
//                        if (device.getName().equalsIgnoreCase(d.getName())) {
//                            contains = true;
//                            break;
//                        }
//                    }
//                    if (!contains) {
//                        removed.add(device);
//                    }
//                }
//                if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
//
//                }
//            }
//
//            if (removed.size() > 0) {
//                for (BluetoothDevice dd : removed) {
//                    Iterator<BluetoothDevice> i = devicesBoosterBle.values().iterator();
//                    while (i.hasNext()) {
//                        if (i.next().getName().equalsIgnoreCase(dd.getName()))
//                            i.remove();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log("refreshList error............................................................. " + e.getMessage());
//        }

    }

    private String lastHr = "";

    public void startReading(String uid) {
        BleDevice device = null;
        if (uid == null || uid.isEmpty()) {
            for (BleDevice key : deviceHashMap.values()) {
                if (key.getType() == DeviceTypes.HR_MONITOR) {
                    device = key;
                    uid = key.getName();
                    break;
                }
            }
        } else device = getBle(uid);
        notifyBleFast(device, uid, BleGattManager.HEART_RATE_SERVICE_UUID.toString(),
                BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID.toString(), false);

    }

    public void stopReading(String uid) {
        BleDevice device = getBle(uid);
        stopNotifyBleFast(device, BleGattManager.HEART_RATE_SERVICE_UUID.toString(),
                BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID.toString(), false);
    }


    private double extractHeartRate(BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;
        // Heart rate bit number format
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        return characteristic.getIntValue(format, 1);
    }

    private void log(String msg) {
        CommunicationManager.log("BluetoothManager: FastBle: " + msg);
    }


    // TODO Ble Test
    private boolean fastBle = true;
    private HashMap<String, BleDevice> deviceHashMap = new HashMap<>();

    BleDevice getBle(String uid) {
        for (Map.Entry<String, BleDevice> key : deviceHashMap.entrySet()) {
            if (key.getKey().contains(uid)) {
                return key.getValue();
            }
        }
        return BleManager.getInstance().getBleDevice(uid);
    }

    private void disconnectBleFast(String uid) {
        BleDevice device = getBle(uid);
        log("disconnectBleFast BleDevice: " + device);
        BleManager.getInstance().disconnect(device);
    }

    private void connectBleFast(String uid) {
        log("connectBleFast mac: " + uid);
        BleDevice device = getBle(uid);

        if (device == null) {
            log("connectBleFast not found:" + uid);
            return;
        }

        BleManager.getInstance().connect(device, new BleScanAndConnectCallback() {
            @Override
            public void onStartConnect() {
                log("connectBleFast onStartConnect ");
            }

            @Override
            public void onConnectFail(BleDevice device, BleException exception) {
                log("connectBleFast onConnectFail " + exception);
                if (listener != null)
                    listener.onConnectFailed(device.getName(), exception.getCodeMessage());
                //SessionManager.getInstance().getUserSession().setDeviceStatusByName(device.getName(), DEVICE_FAILED);
                //EventBus.getDefault().postSticky(DeviceStatusEvent(d))

            }

            @Override
            public void onConnectSuccess(BleDevice device, BluetoothGatt gatt, int status) {
                log("connectBleFast onConnectSuccess " + status);
                if (listener != null)
                    listener.onConnect(device.getName(), status);
                if (status == BluetoothGatt.STATE_CONNECTED) {
                    setMtu(device, 300);
                }
                if (device.getDevice().getName().contains("HW")) {
                    log("connectBleFast device is HW");
                    readBleFast(device, BleGattManager.HEART_RATE_BATTERY_SERVICE.toString(), BleGattManager.HEART_RATE_BATTERY.toString());
//                    notifyBleFast(device, Utils.getUid(device.getName()), BleGattManager.HEART_RATE_SERVICE_UUID.toString(),
//                            BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID.toString(), false);
                    // print(gatt, BleGattManager.HEART_RATE_SERVICE_UUID, BleGattManager.HEART_RATE_2902);

//                    notifyBleFast(device, Utils.getUid(device.getName()), "0x2A37",
//                            "0x2A37");
//                    indicateBleFast(device, BleGattManager.HEART_RATE_SERVICE_UUID.toString(),
//                            BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID.toString());

                } else if (device.getDevice().getName().contains("MIBO-")) {
                    log("connectBleFast device is Booster.....");
                    //readBleFast(device, BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(), BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID.toString());
                    notifyBleFast(device, Utils.getUid(device.getName()), BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(),
                            BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID.toString(), false);
                    //life.mibo.android.utils.Utils.toastDebug("Device Connected "+device.getDevice().getName());
                } else if (device.getDevice().getName().contains("MBRXL")) {
                    readBleFast(device, BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(), BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID.toString());
                    notifyBleFast(device, Utils.getUid(device.getName()), BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(),
                            BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID.toString(), false);
                } else if (device.getDevice().getName().toLowerCase().contains("mi scale") || device.getDevice().getName().toLowerCase().contains("mi scale")) {
                    // readBleFast(device, BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(), BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID.toString());
                    indicateBleFast(device, device.getMac(), "0000181d-0000-1000-8000-00805f9b34fb",
                            "00002a9d-0000-1000-8000-00805f9b34fb", false);
                } else if (device.getDevice().getName().contains("WGNBPA960BT")) {
                    // readBleFast(device, BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(), BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID.toString());
                    indicateBleFast(device, device.getMac(), "0000181d-0000-1000-8000-00805f9b34fb",
                            "00002a9d-0000-1000-8000-00805f9b34fb", false);
                    log("onConnectSuccess WGNBPA960BT  Blood Pressure ");
                    print(gatt, UUID.randomUUID(), UUID.randomUUID());
                } else if (device.getDevice().getName().contains("Contour7801H6669461")) {
                    log("onConnectSuccess Contour7801H6669461  Blood Pressure ");
                    print(gatt, UUID.randomUUID(), UUID.randomUUID());
                } else {
                    print(gatt, UUID.randomUUID(), UUID.randomUUID());
                }
                //gatt.requestMtu()
                //indicateBleFast(bleDevice, BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID.toString(),
                //       BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID.toString());
            }


            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                if (listener != null)
                    listener.onDisconnect(isActiveDisConnected, device.getName(), status);

                log("connectBleFast onDisConnected " + isActiveDisConnected + " :: " + status + " :: " + gatt);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                log("connectBleFast onScanning ");
            }

            @Override
            public void onScanStarted(boolean success) {
                log("connectBleFast onScanStarted ");
            }

            @Override
            public void onScanFinished(BleDevice scanResult) {
                log("connectBleFast onScanFinished ");
            }

        });
    }

    private void readBleFast(final BleDevice uid, String serviceUid, String writeUid) {
        BleManager.getInstance().read(uid, serviceUid, writeUid, new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        log("readBleFast: onReadSuccess " + Arrays.toString(data));
                        onBleCharChanged.bleBoosterChanged(data, Utils.getUid(uid.getName()), 0);
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        log("readBleFast: onReadFailure " + exception);
                    }
                });
    }

    public void setMtu(String uid, int mtu) {
        BleDevice device = getBle(uid);
        if (device != null) {
            setMtu(device, mtu);
        }
    }

    public void setMtu(BleDevice device, int mtu) {
        BleManager.getInstance().setMtu(device, 300, new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                log("setMtu: onSetMTUFailure " + exception.getMessage());
            }

            @Override
            public void onMtuChanged(int mtu) {
                log("setMtu: onMtuChanged " + mtu);
            }
        });
    }

    private void writeBleFast(String uid, byte[] data, String serviceUid, String writeUid) {
        log("writeBleFast: uid " + uid);
        BleDevice device = getBle(uid);
        log("writeBleFast: device " + device);
        BleManager.getInstance().write(device, serviceUid, writeUid, data, false, true, 0, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                log("FastBle: onWriteSuccess " + Arrays.toString(justWrite));

            }

            @Override
            public void onWriteFailure(BleException exception) {
                log("FastBle: onWriteFailure " + exception);
                CommunicationManager.getInstance().broadcastMessage(1002, "", exception.getDescription());
            }
        });
    }

    private void writeBleFastNonSync(String uid, byte[] data, String serviceUid, String writeUid) {
        log("writeBleFast: uid " + uid);
        BleDevice device = getBle(uid);
        log("writeBleFast: device " + device);
        BleManager.getInstance().write2(device, serviceUid, writeUid, data, false, true, 0, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                log("FastBle: onWriteSuccess ");

            }

            @Override
            public void onWriteFailure(BleException exception) {
                log("FastBle: onWriteFailure " + exception);
                CommunicationManager.getInstance().broadcastMessage(1001, "", exception.getCodeMessage());

            }
        });
    }

    private BleNotifyCallback notifyCallback = new BleNotifyCallback() {
        @Override
        public void onNotifySuccess() {
            log("onNotifySuccess: ");
        }

        @Override
        public void onNotifyFailure(BleException exception) {
            log("onNotifyFailure:  " + exception);
        }

        @Override
        public void onCharacteristicChanged(byte[] data, String uid, int property) {
            log("onCharacteristicChanged:  " + Arrays.toString(data) + " : uid " + uid);
            if (onBleCharChanged != null)
                onBleCharChanged.bleBoosterChanged(data, uid, property);
        }

    };

    private void notifyBleFast(final BleDevice device, String uid, String serviceUid, String charUid, boolean descriptor) {
        log("notifyBleFast: uid " + device);
        if (device == null)
            return;
        //BleDevice device = getBle(uid);
        // log("writeBleFast: device " + device);
        BleManager.getInstance().notify(device, uid, serviceUid, charUid, descriptor, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                log("onNotifySuccess: ");
            }

            @Override
            public void onNotifyFailure(BleException exception) {
                log("onNotifyFailure:  " + exception);
            }

            @Override
            public void onCharacteristicChanged(byte[] data, String uid, int property) {
                log("onCharacteristicChanged:  " + Arrays.toString(data) + " : uid " + uid);
                if (onBleCharChanged != null)
                    onBleCharChanged.bleBoosterChanged(data, uid, property);
            }

        });
    }


    private void stopNotifyBleFast(final BleDevice device, String serviceUid, String charUid, boolean descriptor) {
        log("stopNotifyBleFast: uid " + device);
        if (device == null)
            return;
        BleManager.getInstance().stopNotify(device, serviceUid, charUid, descriptor);
    }


    private void indicateBleFast(BleDevice device, String uid, String serviceUid, String charUid, boolean useChar) {
        //log("writeBleFast: uid " + uid);
        // BleDevice device = getBle(uid);
        log("writeBleFast: device " + device);
        BleManager.getInstance().indicate(device, uid, serviceUid, charUid, useChar, new BleIndicateCallback() {

            @Override
            public void onIndicateSuccess() {
                log("onIndicateSuccess: ");
            }

            @Override
            public void onIndicateFailure(BleException exception) {
                log("onIndicateFailure: " + exception);
            }

            @Override
            public void onCharacteristicChanged(byte[] data, String uid) {
                // log("onCharacteristicChanged: " + Arrays.toString(data));
                //[2, -82, 6, -78, 7, 2, 28, 6, 58, 10]
                if (onBleCharChanged != null)
                    onBleCharChanged.bleBoosterChanged(data, uid, Config.INDICATE);
            }
        });
    }

    public BluetoothDevice getScaleDevice() {
        return null;
    }

    public void clear() {
        BleManager.getInstance().clearAll();
    }


    public synchronized void print(BluetoothGatt mBluetoothGatt) {
        print(mBluetoothGatt, UUID.randomUUID(), UUID.randomUUID());
    }

    public synchronized void print(BluetoothGatt mBluetoothGatt, UUID serviceUUID, UUID characteristicUUID) {
        log("print serviceUUID " + serviceUUID);
        log("print characteristicUUID " + characteristicUUID);
        log("print start--------------------------------- ");
        int count = 1;
        if (mBluetoothGatt != null) {
            // mBluetoothGatt.discoverServices();
            for (BluetoothGattService service : mBluetoothGatt.getServices()) {
                log(count + " SERVICE ----- " + service);
                if (service != null) {
                    log(count + " service NAME " + AllGattServices.lookup(service.getUuid()));
                    log(count + " service uid " + service.getUuid());
                    log(count + " service type " + service.getType());
                    for (BluetoothGattCharacteristic chr : service.getCharacteristics()) {
                        log(count + " Characteristic ----- " + chr);
                        if (chr != null) {
                            log(count + " Characteristic  NAME ----- " + AllGattCharacteristics.lookup(chr.getUuid()));
                            log("Characteristic uid " + chr.getUuid());
                            log("Characteristic property " + chr.getProperties());
                            for (BluetoothGattDescriptor desc : chr.getDescriptors()) {
                                log(count + " Descriptors ------ " + desc);
                                if (desc != null) {
                                    log(count + " Descriptors NAME " + AllGattDescriptors.lookup(desc.getUuid()));
                                    log(count + " Descriptors uid " + desc.getUuid());
                                    log(count + " Descriptors value " + Arrays.toString(desc.getValue()));
                                }

                            }
                        }
                    }
                } else {
                    log("print service is NULL ");
                }
                count++;
            }
        } else {
            log("print mBluetoothGatt is NULL ");
        }

        log("print End--------------------------------- ");
    }

}
