package life.mibo.hardware;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import life.mibo.hardware.bluetooth.BleGattManager;
import life.mibo.hardware.bluetooth.CharacteristicChangeListener;
import life.mibo.hardware.bluetooth.operations.GattCharacteristicWriteOperation;
import life.mibo.hardware.bluetooth.operations.GattDisconnectOperation;
import life.mibo.hardware.bluetooth.operations.GattSetNotificationOperation;
import life.mibo.hardware.core.Logger;
import life.mibo.hardware.encryption.Encryption;

/**
 * Created by Fer on 08/04/2019.
 */

public class BluetoothManager {

    private ArrayList<BluetoothDevice> devicesBoosterBle;
    private ArrayList<BluetoothDevice> devicesHRBle;
    public ArrayList<BluetoothDevice> devicesScaleBle;

    private ArrayList<BluetoothDevice> devicesConnectedBle;
    private static final long SCAN_PERIOD = 5000;
    //private Handler mHandlerScan;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private OnBleDeviceDiscovered mDiscoverListener = null;
    private OnBleCharChanged mBleListener = null;
    private Activity activity;
    private BleGattManager mGattManager;


    public interface OnBleDeviceDiscovered {
        void bleHrDeviceDiscovered(String uid, String serial);

        void bleBoosterDeviceDiscovered(String uid, String serial);

        void bleScaleDeviceDiscovered(String uid, String serial);
    }

    public interface OnBleCharChanged {
        void bleHrChanged(int hr, String uid);

        void bleBoosterChanged(byte[] data, String uid);
    }

    public BluetoothManager(Activity activity, OnBleDeviceDiscovered listenerDiscovery, OnBleCharChanged listenerBle) {
        this.activity = activity;
        mDiscoverListener = listenerDiscovery;
        mBleListener = listenerBle;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initBlueTooth() {

//        if (mHandlerScan == null)
//            mHandlerScan = new Handler();


        // Initializes a Bluetooth adapter.
        if (mBluetoothAdapter == null)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothLeScanner == null)
            bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // if(!mBluetoothAdapter.isEnabled())
        mBluetoothAdapter.enable();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //scanDevice();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanDevice(BleScanCallback callback) {
        this.bleScanCallback = callback;
        scanDevice();
    }
    public void scanDevice() {

        //TODO: Check if only devices connected should be cleared o not lose the devices conected
        if (devicesHRBle == null) {
            devicesHRBle = new ArrayList<>();
        }
        if (devicesBoosterBle == null) {
            devicesBoosterBle = new ArrayList<>();
        }
        if (devicesConnectedBle == null) {
            devicesConnectedBle = new ArrayList<>();
        }
        if (devicesScaleBle == null) {
            devicesScaleBle = new ArrayList<>();
        }
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
            bluetoothLeScanner.stopScan(mLeHrSensorScanCallback);
        }
    }

    public void clearDevicesboosterBle() {
        devicesBoosterBle.clear();
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
    }

    //    private BluetoothAdapter.LeScanCallback mLeHrSensorScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                //  String name = device.getName();
//                    if (!devicesHRBle.contains(device) && device.getName() != null && (device.getName().contains("HW") || device.getName().contains("Geonaute"))) {
//                        devicesHRBle.add(device);
//                        mDiscoverListener.bleHrDeviceDiscovered(device.toString(), device.getName());
//                        Log.e("blscan", "" + device.getName() + "   " + device.getClass());
//                    }
//                    if (!devicesBoosterBle.contains(device) && !SessionManager.getInstance().getSession().isBoosterMode() && device.getName() != null && device.getName().contains("MIBO-")) {
//                        devicesBoosterBle.add(device);
//                        mDiscoverListener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-",""), device.getName());
//                      //  mDiscoverListener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
//                        Log.e("blscan", "" + device.getName() + "   " + device.getClass());
//                    }
//                }
//            };

    interface BleScanCallback {
        void onDevice(ScanResult result);
    }

    private BleScanCallback bleScanCallback;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mLeHrSensorScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (bleScanCallback != null)
                bleScanCallback.onDevice(result);
            Logger.e("BluetoothManager onScanResult " + result);
            BluetoothDevice device = result.getDevice();

            if (!devicesHRBle.contains(device) && device.getName() != null && (device.getName().contains("HW") || device.getName().contains("Geonaute"))) {
                devicesHRBle.add(device);
                mDiscoverListener.bleHrDeviceDiscovered(device.toString(), device.getName());
                Logger.e("BluetoothManager onScanResult " + device.getName() + "   " + device.getClass());
            }

            if (!devicesBoosterBle.contains(device) && !SessionManager.getInstance().getSession().isBoosterMode() && device.getName() != null && device.getName().contains("MIBO-")) {
                devicesBoosterBle.add(device);
                mDiscoverListener.bleBoosterDeviceDiscovered(device.getName().replace("MIBO-", ""), device.getName());
                //  mDiscoverListener.bleBoosterDeviceDiscovered(device.toString(), device.getName());
                Logger.e("BluetoothManager onScanResult2 " + device.getName() + "   " + device.getClass());
            }

            if (!devicesScaleBle.contains(device) && device.getName() != null && (device.getName().contains("WS806"))) {
                devicesScaleBle.add(device);
                mDiscoverListener.bleScaleDeviceDiscovered(device.toString(), device.getName());
                Logger.e("BluetoothManager onScanResult3 " + device.getName() + "   " + device.getClass());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Logger.e("BluetoothManager onBatchScanResults " + results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Logger.e("BluetoothManager onScanFailed " + errorCode);
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

                    if (mGattManager == null)
                        mGattManager = new BleGattManager(activity);
                    mGattManager.queue(new GattSetNotificationOperation(d,
                            BleGattManager.HEART_RATE_SERVICE_UUID,
                            BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID,
                            BleGattManager.CLIENT_UUID));
                    addGattListenerHR(d, mGattManager);
                }
            }
        }
    }

    public void connectMIBOBoosterGattDevice(String Id) {
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

                        if (mGattManager == null) {
                            mGattManager = new BleGattManager(activity);
                        }

                        mGattManager.queue(new GattCharacteristicWriteOperation(d,
                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                                new byte[0]));
                        addGattListenerBooster(mGattManager, d);
//                        mGattManager.queue(new GattSetNotificationOperation(d,
//                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
//                                BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID,
//                                BleGattManager.CLIENT_UUID));


                        Logger.e("BluetoothManager booster charr add " + Id);
                    }
            }
        }

    }

    public void disconnectMIBOBoosterGattDevice(String Id) {
        int aux = -1;
        for (BluetoothDevice d : devicesConnectedBle) {
            if (d.getName().contains(Id)) {

                if (mGattManager == null) {
                    mGattManager = new BleGattManager(activity);
                }
                mGattManager.queue(new GattDisconnectOperation(d));
                aux = devicesConnectedBle.indexOf(d);
                //devicesConnectedBle.remove(d);
            }
        }
        if (aux != -1)
            devicesConnectedBle.remove(aux);
    }

    public void disconnectHrGattDevice(String Id) {
        for (BluetoothDevice d : devicesHRBle) {
            if (d.toString().equals(Id)) {
                devicesConnectedBle.remove(d);
                if (mGattManager == null) mGattManager = new BleGattManager(activity);
                mGattManager.queue(new GattDisconnectOperation(d));
            }
        }
    }

    public ArrayList<BluetoothDevice> getConnectedBleDevices() {
        return devicesConnectedBle;
    }


    public void addGattListenerBooster(BleGattManager gatt, final BluetoothDevice d) {
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
                            mBleListener.bleBoosterChanged(data
                                    , d.getName().replace("MIBO-", ""));//deviceAddress);
                            //  Log.e("gattboostlistener", "booster charr: " + data.length);
                        }
                    }
                }
            }
        });
    }

    public void sendToMIBOBoosterGattDevice(String Id, byte[] message) {
        Encryption.mbp_encrypt(message, message.length);
        if (!Id.equals(""))
            for (BluetoothDevice d : devicesBoosterBle) {
                if (d.getName() != null)
                    if (d.getName().contains(Id)) {
                        if (mGattManager == null)
                            mGattManager = new BleGattManager(activity);

                        mGattManager.queue(new GattCharacteristicWriteOperation(d,
                                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                                message));

                    }
            }
    }

    public void sendPingToBoosterGattDevice(byte[] message, BluetoothDevice d) {
        Encryption.mbp_encrypt(message, message.length);
        if (mGattManager == null)
            mGattManager = new BleGattManager(activity);

        //  if(mGattManager.getGatt(d).connect())
        mGattManager.queue(new GattCharacteristicWriteOperation(d,
                BleGattManager.MIBO_EMS_BOOSTER_SERVICE_UUID,
                BleGattManager.MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID,
                message));

    }


    public void addGattListenerHR(final BluetoothDevice d, BleGattManager gatt) {
        gatt.addCharacteristicChangeListener(BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID, new CharacteristicChangeListener() {
            @Override
            public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic) {
                if (characteristic == null && (d.getName().contains("HW") || d.getName().contains("Geonaute"))) {
                    Logger.e("BluetoothManager CONNECT TO CHAR");
                    mGattManager.queue(new GattSetNotificationOperation(d,
                            BleGattManager.HEART_RATE_SERVICE_UUID,
                            BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID,
                            BleGattManager.CLIENT_UUID));
                } else {
                    final byte[] data = characteristic.getValue();
                    if (data != null && data.length > 0) {

                        int HR = (int) extractHeartRate(characteristic);
                        mBleListener.bleHrChanged(HR
                                , deviceAddress);
                        //Log.e("HRlistener", "hr: " + HR);
                    }
                }

            }
        });
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
            Logger.e("BluetoothManager Bluetooth starting");
            try {
                find();
                open();
            } catch (Exception e) {
                Logger.e("BluetoothManager Bluetooth start crashed......", e);
            }
        }

        void find() {
            Logger.e("BluetoothManager Bluetooth finding");

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Logger.e(" BluetoothManager No bluetooth adapter available");
            }

            if (!bluetoothAdapter.isEnabled()) {
                //Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBluetooth, 0);
                Logger.e(" BluetoothManager bluetooth adapter available isEnabled");
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {

                for (BluetoothDevice device : pairedDevices) {
                    Logger.e(" BluetoothManager BluetoothDevice pairedDevices " + device);
                    if (device.getName().equals("MattsBlueTooth")) {
                        bluetoothDevice = device;
                        break;
                    }
                }
            }
            Logger.e("BluetoothManager Device pairedDevices.size()  " + pairedDevices.size());
        }

        void open() throws IOException {
            Logger.e("BluetoothManager Bluetooth Opening");

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            beginListenForData();
        }

        private void beginListenForData() {
            Logger.e("BluetoothManager Bluetooth beginListenForData");
            final byte delimiter = 10; //This is the ASCII code for a newline character

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            thread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = inputStream.available();
                            Logger.e("BluetoothManager data available  " + bytesAvailable);
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
                                        Logger.e("BluetoothManager data received " + data);

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
            Logger.e("BluetoothManager sendData Data Sent " + msg);
        }

        public void close() throws IOException {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            Logger.e("BluetoothManager Closed.......");
        }

    }
}
