package life.mibo.hardware;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import life.mibo.hardware.bluetooth.BleGattManager;
import life.mibo.hardware.bluetooth.BleScanCallback;
import life.mibo.hardware.bluetooth.CharacteristicChangeListener;
import life.mibo.hardware.bluetooth.OnBleCharChanged;
import life.mibo.hardware.bluetooth.OnBleDeviceDiscovered;
import life.mibo.hardware.bluetooth.operations.GattCharacteristicWriteOperation;
import life.mibo.hardware.bluetooth.operations.GattDisconnectOperation;
import life.mibo.hardware.bluetooth.operations.GattSetNotificationOperation;
import life.mibo.hardware.core.Logger;
import life.mibo.hardware.core.Utils;
import life.mibo.hardware.encryption.Encryption;
import life.mibo.hardware.models.BleDevice;
import life.mibo.hardware.models.Device;
import life.mibo.hardware.models.DeviceTypes;

/**
 * Created by Fer on 08/04/2019.
 * Modified by Sumeet
 */

public class BluetoothManager {

    private ArrayList<BluetoothDevice> devicesBoosterBle;
    private ArrayList<BluetoothDevice> devicesRxl;
    private ArrayList<BluetoothDevice> devicesHRBle;
    private ArrayList<BluetoothDevice> devicesScaleBle;
    private ArrayList<BluetoothDevice> devicesConnectedBle;
    private ArrayList<BluetoothDevice> devices;

    private static final long SCAN_PERIOD = 5000;
    //private Handler mHandlerScan;
    private Context activity;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private OnBleDeviceDiscovered listener = null;
    private OnBleCharChanged onBleCharChanged = null;
    //private BleScanCallback bleScanCallback;
    private BleGattManager.OnConnection bleGatListener = null;
    private BleGattManager mGattManager;
    private int rxlCount = 0;


    public void setBleGatListener(BleGattManager.OnConnection bleGatListener) {
        this.bleGatListener = bleGatListener;
    }

    public void setListener(OnBleDeviceDiscovered listener) {
        this.listener = listener;
    }

    public void setOnBleCharChanged(OnBleCharChanged onBleCharChanged) {
        this.onBleCharChanged = onBleCharChanged;
    }

    public BluetoothManager(Context context) {
        this.activity = context;
    }

    public BluetoothManager(Context activity, OnBleDeviceDiscovered listenerDiscovery, OnBleCharChanged listenerBle) {
        this.activity = activity;
        listener = listenerDiscovery;
        onBleCharChanged = listenerBle;
    }

    public BluetoothManager(Context activity, OnBleDeviceDiscovered listenerDiscovery, OnBleCharChanged listenerBle, BleGattManager.OnConnection bleGatConnection) {
        this.activity = activity;
        listener = listenerDiscovery;
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanDevice(BleScanCallback callback) {
        //this.bleScanCallback = callback;
        scanDevice();
    }

    public void scanDevice() {
        if (!mBluetoothAdapter.enable())
            return;
        //TODO: Check if only devices connected should be cleared o not lose the devices conected


        getDevicesBoosterBle().clear();
        getDevicesHRBle().clear();
        getDevicesScaleBle().clear();
        getDevicesConnectedBle();
        getDevicesRxl().clear();
        //rxlCount = rxlCount();
        //reset();
        // initBlueTooth();
//
//        // Stops scanning after a pre-defined scan period.
//        mHandlerScan.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                bluetoothAdapter.stopLeScan(mLeHrSensorScanCallback);
//
//
//            }
//        }, SCAN_PERIOD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (bluetoothLeScanner == null)
                initBlueTooth();
            bluetoothLeScanner.startScan(mLeHrSensorScanCallback);
        }
    }

    public void stopScanDevice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (bluetoothLeScanner != null)
                bluetoothLeScanner.stopScan(mLeHrSensorScanCallback);
        }
    }

    private synchronized void addDevices(BluetoothDevice device) {
        log("addBleDevice........ ");
        if (devices == null)
            devices = new ArrayList<>();

        String uid = BleDevice.getUid(device.getName());
        for (BluetoothDevice d : devices) {
            if (d.getName().contains(uid)) {
                log("addBleDevice found........... ");
                return;
            }
        }
        //String name = "New Device";
        //DeviceTypes type = BleDevice.getDeviceType(device.getName());
        //BleDevice ble = new BleDevice(name, device.getName(), device, type);
        devices.add(device);
        log("mDiscoveredDevices Device added............. --- " + device);
        //if (bleScanCallback != null)
       //     bleScanCallback.onDevice(device);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //
    public ArrayList<BluetoothDevice> getDevices() {
        if (devices == null)
            devices = new ArrayList<>();
        return devices;
    }

    private ArrayList<BluetoothDevice> getDevicesScaleBle() {
        if (devicesScaleBle == null)
            devicesScaleBle = new ArrayList<>();
        return devicesScaleBle;
    }

    private ArrayList<BluetoothDevice> getDevicesBoosterBle() {
        if (devicesBoosterBle == null) {
            devicesBoosterBle = new ArrayList<>();
        }
        return devicesBoosterBle;
    }

    private ArrayList<BluetoothDevice> getDevicesConnectedBle() {
        if (devicesConnectedBle == null) {
            devicesConnectedBle = new ArrayList<>();
        }
        return devicesConnectedBle;
    }

    private ArrayList<BluetoothDevice> getDevicesHRBle() {
        if (devicesHRBle == null) {
            devicesHRBle = new ArrayList<>();
        }
        return devicesHRBle;
    }

    private ArrayList<BluetoothDevice> getDevicesRxl() {
        if (devicesRxl == null)
            devicesRxl = new ArrayList<>();
        return devicesRxl;
    }

    BluetoothDevice getScaleDevice() {
        if (devicesScaleBle != null)
            return devicesScaleBle.get(0);
        return null;
    }

    public void reset() {
        if (devicesBoosterBle != null)
            devicesBoosterBle.clear();
        if (devicesHRBle != null)
            devicesHRBle.clear();
        if (devicesScaleBle != null)
            devicesScaleBle.clear();
        if (devicesConnectedBle != null)
            devicesConnectedBle.clear();
        rxlCount = 0;
    }

    private int rxlCount() {
        int count = 0;
        if (devicesConnectedBle.size() > 0) {
            for (BluetoothDevice d : devicesConnectedBle) {
                if (d.getName().contains("MBRXL"))
                    count++;
            }
        }
        return count;
    }

    //    private BluetoothAdapter.LeScanCallback mLeHrSensorScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                //  String name = device.getName();
//                    if (!devicesHRBle.contains(device) && device.getName() != null && (device.getName().contains("HW") || device.getName().contains("Geonaute"))) {
//                        devicesHRBle.add(device);
//                        listener.bleHrDeviceDiscovered(device.toString(), device.getName());
//                        Log.e("blscan", "" + device.getName() + "   " + device.getClass());
//                    }
//                    if (!devicesBoosterBle.contains(device) && !SessionManager.getInstance().getSession().isBoosterMode() && device.getName() != null && device.getName().contains("MIBO-")) {
//                        devicesBoosterBle.add(device);
//                        listener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-",""), device.getName());
//                      //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
//                        Log.e("blscan", "" + device.getName() + "   " + device.getClass());
//                    }
//                }
//            };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mLeHrSensorScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //if (bleScanCallback != null)
            //    bleScanCallback.onDevice(B);
            log("BluetoothManager onScanResult " + result);
            BluetoothDevice device = result.getDevice();
            //mBluetoothAdapter.getRemoteDevice(result)

            if (!devicesHRBle.contains(device) && device.getName() != null && (device.getName().contains("HW") || device.getName().contains("Geonaute"))) {
                devicesHRBle.add(device);
                listener.bleHrDeviceDiscovered(device.toString(), device.getName());
                log(" onScanResult2 " + device.getName() + "   " + device.getClass());
            }

            // TODO Session is Boosted/Wifi remove later isBoosterMode()
            if (!devicesBoosterBle.contains(device) && device.getName() != null && device.getName().contains("MBRXL")) {
                //rxlCount++;
                devicesBoosterBle.add(device);
                listener.bleRXLDiscovered(Utils.getUid(device.getName()), device.getName(), "");
                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
                log(" onScanResult3 " + device.getName() + "   " + device.getClass());
            }

            if (device.getName() != null && device.getName().contains("MIBO-")) {
                for (BluetoothDevice b : devicesBoosterBle) {
                    if (b.getName().equalsIgnoreCase(device.getName())) {
                        return;
                    }
                }
//                if (device.getName().startsWith("MIBO-RXL")) {
//                    listener.bleRXLDiscovered(Utils.getUid(device.getName()), device.getName(), "RXL- " + devicesBoosterBle.size());
//                    devicesBoosterBle.add(device);
//                    return;
//                }
                devicesBoosterBle.add(device);
                listener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-", ""), device.getName());
                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
                log(" onScanResult4 " + device.getName() + "   " + device.getClass());
            }

//            if (!devicesBoosterBle.contains(device) && !SessionManager.getInstance().getSession().isBoosterMode() && device.getName() != null && device.getName().contains("MIBO-")) {
//                devicesBoosterBle.add(device);
//                listener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-", ""), device.getName());
//                //  listener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
//                log("BluetoothManager onScanResult3 " + device.getName() + "   " + device.getClass());
//            }

            if (!devicesScaleBle.contains(device) && device.getName() != null && (device.getName().contains("WS806"))) {
                devicesScaleBle.add(device);
                listener.bleScaleDeviceDiscovered(device.toString(), device.getName());
                log(" onScanResult4 " + device.getName() + "   " + device.getClass());
            }
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


    public void connectHrGattDevice(String Id) {
        boolean newDevice = true;
        for (BluetoothDevice t : devicesConnectedBle) {
            if (t.toString().equals(Id)) {
                newDevice = false;
            }
        }
        if (newDevice) {
            for (BluetoothDevice d : devicesHRBle) {
                if (d.toString().equals(Id)) {
                    devicesConnectedBle.add(d);


                    getGattManager().queue(new GattSetNotificationOperation(d,
                            BleGattManager.HEART_RATE_SERVICE_UUID,
                            BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID,
                            BleGattManager.CLIENT_UUID));
                    addGattListenerHR(d, mGattManager);
                }
            }
        }
    }

    public BleGattManager getGattManager() {
        if (mGattManager == null)
            mGattManager = new BleGattManager(activity, bleGatListener);
        return mGattManager;
    }

    void connectDevice(BluetoothDevice device) {
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

    void connectMIBOBoosterGattDevice(String Id) {
        log("connectMIBOBoosterGattDevice " + Id);
        boolean newDevice = true;
        for (BluetoothDevice t : devicesConnectedBle) {
            if (t.getName() != null)
                if (t.getName().contains(Id)) {
                    newDevice = false;
                }
        }
        if (newDevice) {
            for (BluetoothDevice d : devicesBoosterBle) {
                if (d.getName() != null)
                    if (d.getName().contains(Id)) {
                        devicesConnectedBle.add(d);

                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                                new byte[0]));
                        addGattListenerBooster(mGattManager, d);
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));


                        log("BluetoothManager booster charr add " + Id);
                    }
            }
        }

    }

    void connectRXLGattDevice(String Id) {
        log("connectRXLGattDevice id " + Id);
//        if (isTest) {
//            testBleConnection(Id);
//            return;
//        }
        boolean newDevice = true;
        for (BluetoothDevice t : devicesConnectedBle) {
            if (t.getName() != null)
                if (t.getName().contains(Id)) {
                    newDevice = false;
                }
        }
        log("connectRXLGattDevice newDevice " + newDevice + " size:" + devicesBoosterBle.size());
        if (newDevice) {
            for (BluetoothDevice d : devicesBoosterBle) {
                if (d.getName() != null) {
                    log("connectRXLGattDevice name: " + d.getName());
                    if (d.getName().toLowerCase().contains(Id.toLowerCase())) {
                        log("connectRXLGattDevice matched ");
                        devicesConnectedBle.add(d);

                        // Not work with MIBO_RXL_SERVICE_CHAR_UUID , MIBO_RXL_TRANSMISSION_CHAR_UUID
//                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
//                                BleGattManager.MIBO_RXL_SERVICE_CHAR_UUID,
//                                BleGattManager.MIBO_RXL_TRANSMISSION_CHAR_UUID,
//                                new byte[0]));

                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                                new byte[0]));
                        addGattListenerRXL2(mGattManager, d);
                        //log("connectRXLGattDevice " + Id);
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));


                        log("connectRXLGattDevice RXL connected " + Id);
                    }
                }
            }
        }

    }

    private boolean isTest = true;

    BluetoothGattCallback testGatt = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            log("BluetoothGattCallback onConnectionStateChange " + newState + " : " + status);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            log("BluetoothGattCallback onServicesDiscovered status " + status);
            super.onServicesDiscovered(gatt, status);
            BluetoothGattCharacteristic characteristic =
                    gatt.getService(BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID)
                            .getCharacteristic(BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID);
            gatt.setCharacteristicNotification(characteristic, true);


            BluetoothGattDescriptor descriptor =
                    characteristic.getDescriptor(BleGattManager.CLIENT_UUID);

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            log("BluetoothGattCallback onCharacteristicRead status " + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            log("BluetoothGattCallback onCharacteristicWrite status " + status);
            log("BluetoothGattCallback onCharacteristicWrite characteristic " + characteristic);

//            BluetoothGattCharacteristic characteristic =
//                    gatt.getService(BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID)
//                            .getCharacteristic(BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID);

            if (characteristic != null) {
                characteristic.setValue(new byte[]{1, 1});
                gatt.writeCharacteristic(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            log("BluetoothGattCallback onCharacteristicChanged status " + characteristic);
        }

    };


    private void testBleConnection(String device) {
        log("testBleConnection started......... " + device);
        for (BluetoothDevice d : devicesBoosterBle) {
            if (d.getName() != null && d.getName().toLowerCase().contains(device.toLowerCase())) {
                log("testBleConnection found and connecting......... " + device);
                BluetoothGatt gatt = d.connectGatt(activity, true, testGatt);
            }

        }
        //BluetoothGatt gatt = device.connectGatt(activity, true, testGatt);
        log("testBleConnection gatt......... ");

    }

    int disconnectMIBOBoosterGattDevice(String Id) {
        log("disconnectMIBOBoosterGattDevice " + Id);

        int aux = -1;
        for (BluetoothDevice d : devicesConnectedBle) {
            if (d.getName().contains(Id)) {

                getGattManager().queue(new GattDisconnectOperation(d));
                aux = devicesConnectedBle.indexOf(d);
                //devicesConnectedBle.remove(d);
            }
        }
        if (aux != -1)
            devicesConnectedBle.remove(aux);
        return aux;
    }

    void disconnectHrGattDevice(String Id) {
        for (BluetoothDevice d : devicesHRBle) {
            if (d.toString().equals(Id)) {
                devicesConnectedBle.remove(d);
                getGattManager().queue(new GattDisconnectOperation(d));
                SessionManager.getInstance().getUserSession().removeDevice(Id);
            }
        }
    }

    ArrayList<BluetoothDevice> getConnectedBleDevices() {
        if (devicesBoosterBle == null)
            devicesBoosterBle = new ArrayList<>();
        return devicesConnectedBle;
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

    // not receive events with MIBO_RXL_RECEPTION_CHAR_UUID , MIBO_RXL_RECEPTION_CHAR_UUID
    private void addGattListenerRXL(BleGattManager gatt, final BluetoothDevice d) {
        log("addGattListenerRXL " + d);
        gatt.addCharacteristicChangeListener(BleGattManager.MIBO_RXL_RECEPTION_CHAR_UUID, new CharacteristicChangeListener() {
            @Override
            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
                log("addGattListenerRXL deviceAddress: " + deviceAddress);
                if (d.toString().equals(deviceAddress)) {
                    if (characteristic == null) {
                        log("BluetoothManager CONNECT TO CHAR");
                        mGattManager.queue(new GattSetNotificationOperation(d,
                                BleGattManager.MIBO_RXL_SERVICE_CHAR_UUID,
                                BleGattManager.MIBO_RXL_RECEPTION_CHAR_UUID,
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

    private void addGattListenerRXL2(BleGattManager gatt, final BluetoothDevice d) {
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

    void sendToBleRxl(String Id, byte[] message) {
        sendToMIBOBoosterGattDevice(Id, message);
    }
    void sendToMIBOBoosterGattDevice(String Id, byte[] message) {
        if (!TextUtils.isEmpty(Id))
            log("sendToMIBOBoosterGattDevice data: " + Utils.getBytes(message));
            Encryption.mbp_encrypt(message, message.length);
            for (BluetoothDevice d : devicesBoosterBle) {
                if (d.getName() != null)
                    if (d.getName().contains(Id)) {
                        getGattManager().queue(new GattCharacteristicWriteOperation(d,
                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                                message));

                    }
            }
    }

    void sendPingToBoosterGattDevice(byte[] message, BluetoothDevice d) {
        log(" sendPingToBoosterGattDevice byte: " + Arrays.toString(message));
        log(" sendPingToBoosterGattDevice char: " + Arrays.toString(new String(message).toCharArray()));
        Encryption.mbp_encrypt(message, message.length);
        getGattManager().queue(new GattCharacteristicWriteOperation(d,
                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                message));

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

    public List<BluetoothDevice> getConnectedDevices() {
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

    public class Manager {
        BluetoothAdapter bluetoothAdapter;
        BluetoothSocket bluetoothSocket;
        BluetoothDevice bluetoothDevice;
        OutputStream outputStream;
        InputStream inputStream;
        Thread thread;
        byte[] readBuffer;
        int readBufferPosition;
        int counter;
        volatile boolean stopWorker;

        public void start() {
            log("BluetoothManager Bluetooth starting");
            try {
                find();
                open();
            } catch (Exception e) {
                Logger.e("BluetoothManager Bluetooth start crashed......", e);
            }
        }

        void find() {
            log("BluetoothManager Bluetooth finding");

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                log(" BluetoothManager No bluetooth adapter available");
            }

            if (!bluetoothAdapter.isEnabled()) {
                //Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBluetooth, 0);
                log(" BluetoothManager bluetooth adapter available isEnabled");
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {

                for (BluetoothDevice device : pairedDevices) {
                    log(" BluetoothManager BluetoothDevice pairedDevices " + device);
                    if (device.getName().equals("MattsBlueTooth")) {
                        bluetoothDevice = device;
                        break;
                    }
                }
            }
            log("BluetoothManager Device pairedDevices.size()  " + pairedDevices.size());
        }

        void open() throws IOException {
            log("BluetoothManager Bluetooth Opening");

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            beginListenForData();
        }

        private void beginListenForData() {
            log("BluetoothManager Bluetooth beginListenForData");
            final byte delimiter = 10; //This is the ASCII code for a newline character

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            thread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = inputStream.available();
                            log("BluetoothManager data available  " + bytesAvailable);
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        log("BluetoothManager data received " + data);

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            Logger.e("BluetoothManager executing exception  ", ex);
                            stopWorker = true;
                        }
                    }
                }
            });

            thread.start();
        }

        public void send(String msg) throws IOException {
            msg += "\n";
            outputStream.write(msg.getBytes());
            log("BluetoothManager sendData Data Sent " + msg);
        }

        public void close() throws IOException {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            log("BluetoothManager Closed.......");
        }
    }


    private void log(String msg) {
        CommunicationManager.log("BluetoothManager: " + msg);
    }

}
