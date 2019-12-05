package life.mibo.hardware.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;


public interface CharacteristicChangeListener {
    void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic);
}
