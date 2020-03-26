/*
 *  Created by Sumeet Kumar on 3/22/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/22/20 5:16 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.fastble.exception;


import life.mibo.hardware.fastble.exception.BleException;

public class GattException extends BleException {

    private int gattStatus;

    public GattException(int gattStatus) {
        super(ERROR_CODE_GATT, "Gatt Exception Occurred! ");
        this.gattStatus = gattStatus;
    }

    public int getGattStatus() {
        return gattStatus;
    }

    public GattException setGattStatus(int gattStatus) {
        this.gattStatus = gattStatus;
        return this;
    }

    @Override
    public String toString() {
        return "GattException{" +
               "gattStatus=" + gattStatus +
               "} " + super.toString();
    }
}
