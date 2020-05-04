/*
 *  Created by Sumeet Kumar on 2/9/20 9:23 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/9/20 9:23 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.bluetooth;

public interface OnBleCharChanged {
    void bleHrChanged(int hr, String uid);

    void bleBoosterChanged(byte[] data, String uid, int property);
}