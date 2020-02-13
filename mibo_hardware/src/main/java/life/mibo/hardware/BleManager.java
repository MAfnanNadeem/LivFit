/*
 *  Created by Sumeet Kumar on 2/9/20 9:17 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/9/20 9:17 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import life.mibo.hardware.bluetooth.BleGattManager;
import life.mibo.hardware.bluetooth.BleScanCallback;
import life.mibo.hardware.bluetooth.CharacteristicChangeListener;
import life.mibo.hardware.bluetooth.OnBleCharChanged;
import life.mibo.hardware.bluetooth.operations.GattCharacteristicWriteOperation;
import life.mibo.hardware.bluetooth.operations.GattDisconnectOperation;
import life.mibo.hardware.bluetooth.operations.GattSetNotificationOperation;
import life.mibo.hardware.core.Logger;
import life.mibo.hardware.core.Utils;
import life.mibo.hardware.encryption.Encryption;
import life.mibo.hardware.models.BleDevice;
import life.mibo.hardware.models.DeviceTypes;

/**
 * Created by Sumeet on 09/02/2020.
 */

public class BleManager {
    private ArrayList<BluetoothDevice> connectedDevices;
    private ArrayList<BleDevice> bluetoothDevices;

    private static final long SCAN_PERIOD = 5000;
    //private Handler mHandlerScan;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private OnBleCharChanged onBleCharChanged = null;
    private BleGattManager.OnConnection bleGatListener = null;
    private Context activity;
    private BleGattManager mGattManager;
    private int rxlCount = 0;
    private BleScanCallback bleScanCallback;


    public void setBleGatListener(BleGattManager.OnConnection bleGatListener) {
        this.bleGatListener = bleGatListener;
    }

    public void setOnBleCharChanged(OnBleCharChanged onBleCharChanged) {
        this.onBleCharChanged = onBleCharChanged;
    }

    public BleManager(Context context) {
        this.activity = context;
    }

    public BleManager(Context activity, OnBleCharChanged listenerBle) {
        this.activity = activity;
        onBleCharChanged = listenerBle;
    }

    public BleManager(Context activity, OnBleCharChanged listenerBle, BleGattManager.OnConnection bleGatConnection) {
        this.activity = activity;
        onBleCharChanged = listenerBle;
        this.bleGatListener = bleGatConnection;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initBlueTooth() {

//        if (mHandlerScan == null)
//            mHandlerScan = new Handler();


        // Initializes a Bluetooth adapter.
        if (mBluetoothAdapter == null)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothAdapter.enable();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mBluetoothAdapter.enable()) {
            if (bluetoothLeScanner == null)
                bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

        // if(!mBluetoothAdapter.isEnabled())

        //scanDevice();
    }

    private BleGattManager getGattManager() {
        if (mGattManager == null)
            mGattManager = new BleGattManager(activity, bleGatListener);
        return mGattManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanDevice(BleScanCallback callback) {
        this.bleScanCallback = callback;
        scanDevice();
    }

    public void scanDevice() {
        if (!mBluetoothAdapter.enable())
            return;
        //TODO: Check if only devices connected should be cleared o not lose the devices connected

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getBleDevices().clear();
            getConnectedDevices().clear();
            rxlCount = 0;
            //reset();
            // initBlueTooth();

            if (bluetoothLeScanner == null)
                initBlueTooth();
            bluetoothLeScanner.startScan(scanCallback);
        }
    }

    public void stopScanDevice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (bluetoothLeScanner != null)
                bluetoothLeScanner.stopScan(scanCallback);
        }
    }


    private ArrayList<BluetoothDevice> getConnectedDevices() {
        if (connectedDevices == null) {
            connectedDevices = new ArrayList<>();
        }
        return connectedDevices;
    }


    private ArrayList<BleDevice> getBleDevices() {
        if (bluetoothDevices == null)
            bluetoothDevices = new ArrayList<>();
        return bluetoothDevices;
    }

//    private void addDevices(BluetoothDevice device) {
//        addDevices(device, "New Device");
//    }

    private synchronized void addDevices(BluetoothDevice device) {
        log("addBleDevice........ ");
        if (bluetoothDevices == null)
            bluetoothDevices = new ArrayList<>();

        String uid = BleDevice.getUid(device.getName());
        for (BleDevice d : bluetoothDevices) {
            if (d.getUid().equalsIgnoreCase(uid)) {
                log("addBleDevice found........... ");
                return;
            }
        }
        String name = "New Device";
        DeviceTypes type = BleDevice.getDeviceType(device.getName());
        if (type == DeviceTypes.RXL_BLE) {
            rxlCount++;
            name = "RXL " + rxlCount;
        }
        BleDevice ble = new BleDevice(name, device.getName(), device, type);
        bluetoothDevices.add(ble);
        log("mDiscoveredDevices Device added............. --- " + device);
        if (bleScanCallback != null)
            bleScanCallback.onDevice(ble);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void reset() {
        if (bluetoothDevices != null)
            bluetoothDevices.clear();
        if (connectedDevices != null)
            connectedDevices.clear();
        rxlCount = 0;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
//            if (bleScanCallback != null)
//                bleScanCallback.onDevice(result);
            log("BluetoothManager onScanResult " + result);
            BluetoothDevice device = result.getDevice();
            //mBluetoothAdapter.getRemoteDevice(result)
            if (device != null && device.getName() != null) {
                addDevices(device);
                return;
            }

//            if (!devicesHRBle.contains(device) && device.getName() != null && (device.getName().contains("HW") || device.getName().contains("Geonaute"))) {
//                devicesHRBle.add(device);
//                listener.bleHrDeviceDiscovered(device.toString(), device.getName());
//                log(" onScanResult2 " + device.getName() + "   " + device.getClass());
//            }
//
//            // TODO Session is Boosted/Wifi remove later isBoosterMode()
//            if (!devicesBoosterBle.contains(device) && device.getName() != null && device.getName().contains("MBRXL")) {
//                rxlCount++;
//                devicesBoosterBle.add(device);
//                listener.bleRXLDiscovered(Utils.getUid(device.getName()), device.getName(), "RXL " + rxlCount);
//                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
//                log(" onScanResult3 " + device.getName() + "   " + device.getClass());
//            }
//
//            if (device.getName() != null && device.getName().contains("MIBO-")) {
//                for (BluetoothDevice b : devicesBoosterBle) {
//                    if (b.getName().equalsIgnoreCase(device.getName())) {
//                        return;
//                    }
//                }
////                if (device.getName().startsWith("MIBO-RXL")) {
////                    listener.bleRXLDiscovered(Utils.getUid(device.getName()), device.getName(), "RXL- " + devicesBoosterBle.size());
////                    devicesBoosterBle.add(device);
////                    return;
////                }
//                devicesBoosterBle.add(device);
//                listener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-", ""), device.getName());
//                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
            //log(" onScanResult4 " + device.getName() + "   " + device.getClass());
//            }

//            if (!devicesBoosterBle.contains(device) && !SessionManager.getInstance().getSession().isBoosterMode() && device.getName() != null && device.getName().contains("MIBO-")) {
//                devicesBoosterBle.add(device);
//                listener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-", ""), device.getName());
//                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
//                log("BluetoothManager onScanResult3 " + device.getName() + "   " + device.getClass());
//            }

//            if (!devicesScaleBle.contains(device) && device.getName() != null && (device.getName().contains("WS806"))) {
//                devicesScaleBle.add(device);
//                listener.bleScaleDeviceDiscovered(device.toString(), device.getName());
//                log(" onScanResult4 " + device.getName() + "   " + device.getClass());
//            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            log("BluetoothManager onBatchScanResults " + results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            log("BluetoothManager onScanFailed " + errorCode);
        }
    };

    void connectDevice(String id) {
        for (BluetoothDevice t : connectedDevices) {
            if (t.getName() != null)
                if (t.getName().contains(id)) {
                    return;
                }
        }

        for (BleDevice d : bluetoothDevices) {
            if (d.getName() != null)
                if (d.getSerial().contains(id)) {
                    switch (d.getType()) {
                        case BLE_STIMULATOR: {
                            connectMIBOBooster(d.getDevice());
                        }
                        break;
                        case RXL_BLE: {
                            connectRXL(d.getDevice());
                        }
                        break;
                        case HR_MONITOR: {
                            connectHrGattDevice(d.getDevice());
                        }
                        break;
                        case SCALE: {
                            SessionManager.getInstance().getUserSession().addScale(d.getDevice());
                        }
                        break;
                        case TERMINAL: {

                        }
                        break;
                    }

                }
        }
    }

    private void connectDevice(BluetoothDevice device) {
        log("BluetoothManager connectDevice " + device.getName());
        if (device != null && device.getName() != null) {

            getGattManager().queue(new GattCharacteristicWriteOperation(device,
                    BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                    BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                    new byte[0]));
            addGattListenerBooster(mGattManager, device);
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));


            log("BluetoothManager booster charr add " + device.getName());
        }

    }

    private void connectHrGattDevice(BluetoothDevice device) {

        connectedDevices.add(device);
        getGattManager().queue(new GattSetNotificationOperation(device,
                BleGattManager.HEART_RATE_SERVICE_UUID,
                BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID,
                BleGattManager.CLIENT_UUID));
        addGattListenerHR(device, mGattManager);
    }

    private void connectMIBOBooster(BluetoothDevice device) {
        log("connectMIBOBoosterGattDevice " + device);

        connectedDevices.add(device);

        getGattManager().queue(new GattCharacteristicWriteOperation(device,
                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                new byte[0]));
        addGattListenerBooster(mGattManager, device);
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));

        log("BluetoothManager booster charr add " + device);

    }

    private void connectRXL(BluetoothDevice device) {
        log("connectRXLGattDevice id " + device);

        log("connectRXLGattDevice matched ");
        connectedDevices.add(device);

        // Not work with MIBO_RXL_SERVICE_CHAR_UUID , MIBO_RXL_TRANSMISSION_CHAR_UUID
//                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
//                                BleGattManager.MIBO_RXL_SERVICE_CHAR_UUID,
//                                BleGattManager.MIBO_RXL_TRANSMISSION_CHAR_UUID,
//                                new byte[0]));

        getGattManager().queue(new GattCharacteristicWriteOperation(device,
                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                new byte[0]));
        addGattListenerRXL(mGattManager, device);
        //log("connectRXLGattDevice " + Id);
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));


        log("connectRXLGattDevice RXL connected " + device);

    }

    private boolean isTest = true;

//    private void testBleConnection(String device) {
//        log("testBleConnection started......... " + device);
//        for (BluetoothDevice d : devicesBoosterBle) {
//            if (d.getName() != null && d.getName().toLowerCase().contains(device.toLowerCase())) {
//                log("testBleConnection found and connecting......... " + device);
//                BluetoothGatt gatt = d.connectGatt(activity, true, testGatt);
//            }
//
//        }
//        //BluetoothGatt gatt = device.connectGatt(activity, true, testGatt);
//        log("testBleConnection gatt......... ");
//
//    }

    int disconnectDevice(String id) {
        int pos = -1;

        for (BleDevice d : bluetoothDevices) {
            if (d.getName() != null)
                if (d.getSerial().contains(id)) {
                    switch (d.getType()) {
                        case BLE_STIMULATOR:
                        case RXL_BLE: {
                            disconnectMIBOBooster(d.getUid());
                        }
                        break;
                        case HR_MONITOR: {
                            //disconnectMIBOBooster(d.getUid());
                            disconnectHr(d.getDevice());

                        }
                        break;
                        case SCALE: {
                           // SessionManager.getInstance().getUserSession().addScale(d.getDevice());
                        }
                        break;
                        case TERMINAL: {

                        }
                        break;
                    }

                }
        }
        return pos;
    }

    private int disconnectMIBOBooster(String uid) {
        log("disconnectMIBOBoosterGattDevice " + uid);

        int aux = -1;
        for (BluetoothDevice d : connectedDevices) {
            if (d.getName().contains(uid)) {

                getGattManager().queue(new GattDisconnectOperation(d));
                aux = connectedDevices.indexOf(d);
                //connectedDevices.remove(d);
            }
        }
        if (aux != -1)
            connectedDevices.remove(aux);
        return aux;
    }

    private void disconnectHr(BluetoothDevice device) {
        connectedDevices.remove(device);
        getGattManager().queue(new GattDisconnectOperation(device));
        SessionManager.getInstance().getUserSession().removeDevice(device.getAddress());
    }


    private void sendToBleRxl(String Id, byte[] message) {
        sendToMIBOBoosterGattDevice(Id, message);
    }

    void sendMessage(String id, byte[] message) {
        if (!TextUtils.isEmpty(id)) {
            log("sendToMIBOBoosterGattDevice data: " + Utils.getBytes(message));
            Encryption.mbp_encrypt(message, message.length);
            for (BluetoothDevice d : connectedDevices) {
                if (d.getName() != null)
                    if (d.getName().contains(id)) {
                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                                message));

                    }
            }
        }
    }

    private void sendToMIBOBoosterGattDevice(String Id, byte[] message) {
        if (!TextUtils.isEmpty(Id))
            log("sendToMIBOBoosterGattDevice data: " + Utils.getBytes(message));
        Encryption.mbp_encrypt(message, message.length);
        for (BluetoothDevice d : connectedDevices) {
            if (d.getName() != null)
                if (d.getName().contains(Id)) {
                    getGattManager().queue(new GattCharacteristicWriteOperation(d,
                            BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                            BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                            message));

                }
        }
    }

    void sendPingToBooster(byte[] message, BluetoothDevice d) {
        log(" sendPingToBoosterGattDevice byte: " + Utils.getBytes(message));
        log(" sendPingToBoosterGattDevice char: " + Utils.getChars(message));
        Encryption.mbp_encrypt(message, message.length);
        getGattManager().queue(new GattCharacteristicWriteOperation(d,
                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                message));

    }

    private void addGattListenerBooster(BleGattManager gatt, final BluetoothDevice d) {
        log("addGattListenerBooster " + d);
        gatt.addCharacteristicChangeListener(BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID, new CharacteristicChangeListener() {
            @Override
            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
                if (d.toString().equals(deviceAddress)) {
                    if (characteristic == null && d.getName().contains("MIBO-")) {
                        Logger.e("BluetoothManager CONNECT TO CHAR");
                        mGattManager.queue(new GattSetNotificationOperation(d,
                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
                                BleGattManager.CLIENT_UUID));
                    } else {
                        final byte[] data = characteristic.getValue();
                        if (data != null && data.length > 0) {
                            onBleCharChanged.bleBoosterChanged(data
                                    , d.getName().replace("MIBO-", ""));//deviceAddress);
                            //  Log.e("gattboostlistener", "booster charr: " + data.length);
                        }
                    }
                }
            }
        });
    }

    private void addGattListenerRXL(BleGattManager gatt, final BluetoothDevice d) {
        log("addGattListenerRXL " + d);
        gatt.addCharacteristicChangeListener(BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID, new CharacteristicChangeListener() {
            @Override
            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
                if (d.toString().equals(deviceAddress)) {
                    if (characteristic == null && d.getName().contains("MBRXL-")) {
                        log("BluetoothManager CONNECT TO CHAR");
                        mGattManager.queue(new GattSetNotificationOperation(d,
                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
                                BleGattManager.CLIENT_UUID));
                    } else {
                        final byte[] data = characteristic.getValue();
                        if (data != null && data.length > 0) {
                            onBleCharChanged.bleBoosterChanged(data, Utils.getUid(d.getName()));//deviceAddress);
                            //  Log.e("gattboostlistener", "booster charr: " + data.length);
                        }
                    }
                }
            }
        });
    }

    private void addGattListenerHR(final BluetoothDevice d, BleGattManager gatt) {
        gatt.addCharacteristicChangeListener(BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID, new CharacteristicChangeListener() {
            @Override
            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
                if (characteristic == null && (d.getName().contains("HW") || d.getName().contains("Geonaute"))) {
                    log("BluetoothManager CONNECT TO CHAR");
                    mGattManager.queue(new GattSetNotificationOperation(d,
                            BleGattManager.HEART_RATE_SERVICE_UUID,
                            BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID,
                            BleGattManager.CLIENT_UUID));
                } else {
                    final byte[] data = characteristic.getValue();
                    if (data != null && data.length > 0) {

                        int HR = (int) extractHeartRate(characteristic);
                        onBleCharChanged.bleHrChanged(HR
                                , deviceAddress);
                        //Log.e("HRlistener", "hr: " + HR);
                    }
                }

            }
        });
    }

    public List<BluetoothDevice> getConnectedBleDevices() {
        List<BluetoothDevice> devices = new ArrayList<>();
        try {
            android.bluetooth.BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            devices.addAll(bluetoothManager.getConnectedDevices(BluetoothProfile.GATT));
//            for (BluetoothDevice device : devices) {
//                if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
//
//                }
//            }
        } catch (Exception e) {

        }
        return devices;

    }

    private static double extractHeartRate(BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;
        // Heart rate bit number format
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        final int heartRate = characteristic.getIntValue(format, 1);
        return heartRate;
    }

    private void log(String msg) {
        CommunicationManager.log("BleManager: " + msg);
    }

}
