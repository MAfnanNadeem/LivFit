package life.mibo.hardware.bluetooth;

public interface GattDescriptorReadCallback {
    void call(byte[] value);
}
