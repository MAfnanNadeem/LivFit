package life.mibo.hardware.bluetooth.operations;

/**
 * Created by Fer on 20/04/2019.
 */

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

import life.mibo.hardware.CommunicationManager;
import life.mibo.hardware.MIBO;

public class GattCharacteristicWriteOperation extends GattOperation {

    private final UUID mService;
    private final UUID mCharacteristic;
    private final byte[] mValue;

    public GattCharacteristicWriteOperation(BluetoothDevice device, UUID service, UUID characteristic, byte[] value) {
        super(device);
        mService = service;
        mCharacteristic = characteristic;
        mValue = value;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        try {
            log("GattCharacteristicWriteOperation BluetoothGatt execute " + gatt);
            BluetoothGattService service = gatt.getService(mService);
            if (service == null) {
                CommunicationManager.log("GattCharacteristicWriteOperation service is NULL......");
                return;
            }
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(mCharacteristic);
            characteristic.setValue(mValue);
            gatt.writeCharacteristic(characteristic);
        } catch (Exception e) {
            MIBO.log("GattCharacteristicWriteOperation execute error");
            MIBO.log(e);
        }
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return true;
    }
}