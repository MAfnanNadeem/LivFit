package life.mibo.hardware.bluetooth;

public interface GattCharacteristicReadCallback {
    void call(byte[] characteristic);
}
