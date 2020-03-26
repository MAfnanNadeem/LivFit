/*
 *  Created by Sumeet Kumar on 3/22/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/22/20 5:16 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.fastble.callback;


import life.mibo.hardware.fastble.exception.BleException;

public abstract class BleWriteCallback extends BleBaseCallback {

    public abstract void onWriteSuccess(int current, int total, byte[] justWrite);

    public abstract void onWriteFailure(BleException exception);

}
