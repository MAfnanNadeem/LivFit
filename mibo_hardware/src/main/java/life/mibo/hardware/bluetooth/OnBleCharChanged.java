/*
 *  Created by Sumeet Kumar on 2/9/20 9:23 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/9/20 9:23 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.bluetooth;

import com.onecoder.devicelib.base.protocol.entity.ScaleStableData;

import life.mibo.hardware.models.ScaleData;

public interface OnBleCharChanged {
    void bleHrChanged(int hr, String uid);

    void bleScale(float weight, ScaleData data, int code, Object other);

    void bleBoosterChanged(byte[] data, String uid, int property);
}