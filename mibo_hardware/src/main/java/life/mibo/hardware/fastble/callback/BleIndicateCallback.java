/*
 *  Created by Sumeet Kumar on 3/22/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/22/20 5:16 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.fastble.callback;


import life.mibo.hardware.fastble.callback.BleBaseCallback;
import life.mibo.hardware.fastble.exception.BleException;

public abstract class BleIndicateCallback extends BleBaseCallback {

    public abstract void onIndicateSuccess();

    public abstract void onIndicateFailure(BleException exception);

    public abstract void onCharacteristicChanged(byte[] data, String uid);
}
