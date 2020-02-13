package life.mibo.hardware.bluetooth.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import life.mibo.hardware.CommunicationManager;

public abstract class GattOperation {

    private static final int DEFAULT_TIMEOUT_IN_MILLIS = 400;
    private BluetoothDevice mDevice;
    private GattOperationBundle mBundle;

    public GattOperation(BluetoothDevice device) {
        mDevice = device;
    }

    public abstract void execute(BluetoothGatt bluetoothGatt);

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public int getTimeoutInMillis() {
        return DEFAULT_TIMEOUT_IN_MILLIS;
    }

    public abstract boolean hasAvailableCompletionCallback();

    public GattOperationBundle getBundle() {
        return mBundle;
    }

    public void setBundle(GattOperationBundle bundle) {
        mBundle = bundle;
    }

    void log(String msg) {
        CommunicationManager.log("GattOperation: " + msg);
    }

    @Override
    public String toString() {
        return "GattOperation{" +
                "mDevice=" + mDevice +
                ", mBundle=" + mBundle +
                '}';
    }
}
