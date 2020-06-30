/*
 *  Created by Sumeet Kumar on 2/9/20 9:23 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/9/20 9:23 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.bluetooth;

public interface OnBleDeviceDiscovered {
    void bleHrDeviceDiscovered(String uid, String serial);

    void bleBoosterDeviceDiscovered(String uid, String serial);

    void bleRXLDiscovered(String uid, String serial, String name);

    void bleScaleDeviceDiscovered(String uid, String serial);

    void onConnect(String name, int status);

    void onDisconnect(boolean isActive, String name, int code);

    void onConnectFailed(String name, String error);
}