package life.mibo.hardware.bluetooth.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.UUID;

import life.mibo.hardware.CommunicationManager;

public class GattSetNotificationOperation extends GattOperation {

    private final UUID mServiceUuid;
    private final UUID mCharacteristicUuid;
    private final UUID mDescriptorUuid;

    public GattSetNotificationOperation(BluetoothDevice device, UUID serviceUuid, UUID characteristicUuid, UUID descriptorUuid) {
        super(device);
        mServiceUuid = serviceUuid;
        mCharacteristicUuid = characteristicUuid;
        mDescriptorUuid = descriptorUuid;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        log("GattSetNotificationOperation execute "+gatt);
        log("GattSetNotificationOperation mServiceUuid "+mServiceUuid);
        log("GattSetNotificationOperation mCharacteristicUuid "+mCharacteristicUuid);
        BluetoothGattCharacteristic characteristic = gatt.getService(mServiceUuid).getCharacteristic(mCharacteristicUuid);
        boolean enable = true;
        gatt.setCharacteristicNotification(characteristic, enable);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(mDescriptorUuid);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return false;
    }
}

