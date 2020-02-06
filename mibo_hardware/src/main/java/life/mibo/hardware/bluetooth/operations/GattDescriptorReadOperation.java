package life.mibo.hardware.bluetooth.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import java.util.UUID;

import life.mibo.hardware.CommunicationManager;
import life.mibo.hardware.bluetooth.GattDescriptorReadCallback;


public class GattDescriptorReadOperation extends GattOperation {

    private final UUID mService;
    private final UUID mCharacteristic;
    private final UUID mDescriptor;
    private final GattDescriptorReadCallback mCallback;

    public GattDescriptorReadOperation(BluetoothDevice device, UUID service, UUID characteristic, UUID descriptor, GattDescriptorReadCallback callback) {
        super(device);
        mService = service;
        mCharacteristic = characteristic;
        mDescriptor = descriptor;
        mCallback = callback;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        log("Reading from " + mDescriptor);
        BluetoothGattDescriptor descriptor = gatt.getService(mService).getCharacteristic(mCharacteristic).getDescriptor(mDescriptor);
        gatt.readDescriptor(descriptor);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return true;
    }

    public void onRead(BluetoothGattDescriptor descriptor) {
        mCallback.call(descriptor.getValue());
    }
}
