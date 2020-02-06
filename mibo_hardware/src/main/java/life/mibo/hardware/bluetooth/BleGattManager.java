package life.mibo.hardware.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import life.mibo.hardware.CommunicationManager;
import life.mibo.hardware.bluetooth.operations.GattCharacteristicReadOperation;
import life.mibo.hardware.bluetooth.operations.GattDescriptorReadOperation;
import life.mibo.hardware.bluetooth.operations.GattOperation;
import life.mibo.hardware.bluetooth.operations.GattOperationBundle;
import life.mibo.hardware.core.Logger;

//import org.greenrobot.eventbus.EventBus;


/**
 * Created by Fer on 12/04/2019.
 */

public class BleGattManager {
    public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static UUID HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    public static UUID HEART_RATE_MEASUREMENT_CHAR_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public static UUID HEART_RATE_CONTROL_POINT_CHAR_UUID = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");

    public static UUID MIBO_EMS_BOOSTER_SERVICE_UUID = UUID.fromString("26bab000-fd3c-4e2c-83b0-0da3b256eb75");
    public static UUID MIBO_EMS_BOOSTER_TRANSMISSION_CHAR_UUID = UUID.fromString("26bac001-fd3c-4e2c-83b0-0da3b256eb75");
    public static UUID MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID = UUID.fromString("26bac002-fd3c-4e2c-83b0-0da3b256eb75");

    public static UUID MIBO_RXL_SERVICE_CHAR_UUID = UUID.fromString("26bac003-fd3c-4e2c-83b0-0da3b256eb75");
    public static UUID MIBO_RXL_TRANSMISSION_CHAR_UUID = UUID.fromString("26bac004-fd3c-4e2c-83b0-0da3b256eb75");
    public static UUID MIBO_RXL_RECEPTION_CHAR_UUID = UUID.fromString("26bac005-fd3c-4e2c-83b0-0da3b256eb75");

    private ConcurrentLinkedQueue<GattOperation> gattQueue;
    private ConcurrentHashMap<String, BluetoothGatt> gattMap;
    private GattOperation mCurrentOperation;
    private HashMap<UUID, ArrayList<CharacteristicChangeListener>> listenerHashMap;
    private AsyncTask<Void, Void, Void> asyncTaskTimeout;
    private Context context;

    private ArrayList<String> connectingDevices = new ArrayList<>();

    public BleGattManager(Context context) {
        this.context = context;
        gattQueue = new ConcurrentLinkedQueue<>();
        gattMap = new ConcurrentHashMap<>();
        mCurrentOperation = null;
        listenerHashMap = new HashMap<>();
    }

    public BleGattManager(Context context, OnConnection listener) {
        this(context);
        setListener(listener);
    }

    void log(String msg) {
        CommunicationManager.log("BleGattManager: " + msg);
    }

    public synchronized void cancelCurrentOperationBundle() {
        log("Cancelling current operation. Queue size before: " + gattQueue.size());
        if (mCurrentOperation != null && mCurrentOperation.getBundle() != null) {
            for (GattOperation op : mCurrentOperation.getBundle().getOperations()) {
                gattQueue.remove(op);
            }
        }
        log("Queue size after: " + gattQueue.size());
        mCurrentOperation = null;
        drive();
    }

    public synchronized void queue(GattOperation gattOperation) {
        gattQueue.add(gattOperation);
        log("Queueing Gatt operation, size will now become: " + gattQueue.size());
        drive();
    }

    private synchronized void drive() {
        if (mCurrentOperation != null) {
            log("tried to drive, but currentOperation was not null, " + mCurrentOperation);
            return;
        }
        if (gattQueue.size() == 0) {
            log("Queue empty, drive loop stopped.");
            mCurrentOperation = null;
            return;
        }

        final GattOperation operation = gattQueue.poll();
        log("Driving Gatt queue, size will now become: " + gattQueue.size());
        setCurrentOperation(operation);


        if (asyncTaskTimeout != null) {
            asyncTaskTimeout.cancel(true);
        }
        asyncTaskTimeout = new AsyncTask<Void, Void, Void>() {
            @Override
            protected synchronized Void doInBackground(Void... voids) {
                try {
                    log("Starting to do a background timeout");
                    wait(operation.getTimoutInMillis());
                } catch (InterruptedException e) {
                    log("was interrupted out of the timeout");
                }
                if (isCancelled()) {
                    log("The timeout was cancelled, so we do nothing.");
                    return null;
                }
                log("Timeout ran to completion, time to cancel the entire operation bundle. Abort, abort!");
                cancelCurrentOperationBundle();
                return null;
            }

            @Override
            protected synchronized void onCancelled() {
                super.onCancelled();
                notify();
            }
        }.execute();

        final BluetoothDevice device = operation.getDevice();
        if (gattMap.containsKey(device.getAddress())) {
            execute(gattMap.get(device.getAddress()), operation);
        } else {
            if (!connectingDevices.contains(device.getAddress())) {
                log("connect..." + gattMap.size());
                connectingDevices.add(device.getAddress());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    device.connectGatt(context, true, new BluetoothGattCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            super.onConnectionStateChange(gatt, status, newState);
                            log(" drive onConnectionStateChange");
                            //                    EventBus.postEvent(Trigger.TRIGGER_CONNECTION_STATE_CHANGED,
                            //                            new ConnectionStateChangedBundle(
                            //                                    device.getAddress(),
                            //                                    newState));
                            connectingDevices.remove(device.getAddress());
                            if (status == 133) {
                                log("Got the status 133 bug, closing gatt");
                                setCurrentOperation(null);

                                gatt.close();
                                gattMap.remove(device.getAddress());
                                return;
                            }

                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                log("Gatt connected to device " + device.getAddress());
                                if (listener != null)
                                    listener.onConnected(device.getName());
                                //EventBus.getDefault().postSticky(new BleConnection(device.getName()));
                                if (!gattMap.containsKey(device.getAddress())) {
                                    gattMap.put(device.getAddress(), gatt);

                                    gatt.requestMtu(300);
                                } else {
                                    drive();
                                }

                            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                log("Disconnected from gatt server " + device.getAddress() + ", newState: " + newState);

                                setCurrentOperation(null);
                                // gatt.disconnect();
                                gatt.close();
                                gattMap.remove(device.getAddress());
                                log("drive 1 ");
                                drive();
                            }
                        }

                        @Override
                        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                            super.onDescriptorRead(gatt, descriptor, status);
                            ((GattDescriptorReadOperation) mCurrentOperation).onRead(descriptor);
                            setCurrentOperation(null);
                            log("drive onDescriptorRead  " + status);
                            drive();
                        }

                        @Override
                        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                            super.onMtuChanged(gatt, mtu, status);
                            log("Mtu Changed " + " status: " + status);

                            gatt.discoverServices();
                            //setCurrentOperation(null);
                            //drive();
                        }


                        @Override
                        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                            super.onDescriptorWrite(gatt, descriptor, status);
                            setCurrentOperation(null);
                            log("drive 3 ");
                            drive();
                            log("drive 3 ");
                        }

                        @Override
                        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                            super.onCharacteristicRead(gatt, characteristic, status);
                            ((GattCharacteristicReadOperation) mCurrentOperation).onRead(characteristic);
                            setCurrentOperation(null);
                            log("drive 4 ");
                            drive();
                            log("drive 4 ");
                        }

                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                            super.onServicesDiscovered(gatt, status);
                            log("services discovered, status: " + status + " " + device.getName());

                            if (device.getName().contains("MIBO"))
                                for (CharacteristicChangeListener listener : listenerHashMap.get(BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID)) {
                                    listener.onCharacteristicChanged(device.getAddress(), null);
                                }
                            if (device.getName().contains("MBRXL")) {
                                //for (CharacteristicChangeListener listener : listenerHashMap.get(BleGattManager.MIBO_RXL_RECEPTION_CHAR_UUID)) {
                                  //  listener.onCharacteristicChanged(device.getAddress(), null);
                               // }
                                for (CharacteristicChangeListener listener : listenerHashMap.get(BleGattManager.MIBO_EMS_BOOSTER_RECEPTION_CHAR_UUID)) {
                                    listener.onCharacteristicChanged(device.getAddress(), null);
                                }
                            }
                            if (device.getName().contains("HW") || device.getName().contains("Geonaute"))
                                for (CharacteristicChangeListener listener : listenerHashMap.get(BleGattManager.HEART_RATE_MEASUREMENT_CHAR_UUID)) {
                                    listener.onCharacteristicChanged(device.getAddress(), null);
                                }
                            execute(gatt, operation);
                        }


                        @Override
                        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                            super.onCharacteristicWrite(gatt, characteristic, status);
                            // log("Characteristic " + characteristic.getUuid() + "written to on device " + device.getAddress());
                            setCurrentOperation(null);
                            // log("drive 5 " );

                            drive();
                        }


                        @Override
                        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                            super.onCharacteristicChanged(gatt, characteristic);
                            //Log.e("GattManager","Characteristic " + characteristic.getUuid() + " changed, device: " + device.getAddress());
                            if (listenerHashMap.containsKey(characteristic.getUuid())) {
                                for (CharacteristicChangeListener listener : listenerHashMap.get(characteristic.getUuid())) {
                                    listener.onCharacteristicChanged(device.getAddress(), characteristic);
                                    //log("Characteristic " + characteristic.getUuid() + "read from device " + device.getAddress());

                                }
                            }
                            //setCurrentOperation(null);
                        }
                    }, 2);
                }
            }
            waitIdle();
        }
    }

    public static boolean waitIdle() {
        int i = 300;
        i /= 10;
        while (--i > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Logger.e("BleGattManager waitIdle waiting ");

        }
        return i > 0;
    }

    private void execute(BluetoothGatt gatt, GattOperation operation) {
        if (operation != mCurrentOperation) {
            return;
        }
        operation.execute(gatt);
        if (!operation.hasAvailableCompletionCallback()) {
            setCurrentOperation(null);
            drive();
        }
    }

    public synchronized void setCurrentOperation(GattOperation currentOperation) {
        mCurrentOperation = currentOperation;
    }

    public BluetoothGatt getGatt(BluetoothDevice device) {
        return gattMap.get(device);
    }

    public void addCharacteristicChangeListener(UUID characteristicUuid, CharacteristicChangeListener characteristicChangeListener) {
        //Log.e("GattManager","addCharacteristicChangeListener..."+listenerHashMap.size());
        if (!listenerHashMap.containsKey(characteristicUuid)) {
            listenerHashMap.put(characteristicUuid, new ArrayList<CharacteristicChangeListener>());
            //      listenerHashMap.get(characteristicUuid).add(characteristicChangeListener);
        }
        listenerHashMap.get(characteristicUuid).add(characteristicChangeListener);
//        if(!listenerHashMap.get(characteristicUuid).contains(characteristicChangeListener)) {
//            listenerHashMap.get(characteristicUuid).add(characteristicChangeListener);
//        }
        //log("addCharacteristicChangeListener2..."+listenerHashMap.get(characteristicUuid).size());
    }

    public void queue(GattOperationBundle bundle) {
        for (GattOperation operation : bundle.getOperations()) {
            queue(operation);
        }
    }

    public class ConnectionStateChangedBundle {
        public final int mNewState;
        public final String mAddress;

        public ConnectionStateChangedBundle(String address, int newState) {
            mAddress = address;
            mNewState = newState;
        }
    }

    private OnConnection listener;

    public OnConnection getListener() {
        return listener;
    }

    public void setListener(OnConnection listener) {
        this.listener = listener;
    }

    public interface OnConnection {
        void onConnected(String deviceName);
    }
}
