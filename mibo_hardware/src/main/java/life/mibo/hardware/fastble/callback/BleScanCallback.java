/*
 *  Created by Sumeet Kumar on 3/22/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/22/20 5:16 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.fastble.callback;


import life.mibo.hardware.fastble.callback.BleScanPresenterImp;
import life.mibo.hardware.fastble.data.BleDevice;

import java.util.List;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(List<BleDevice> scanResultList);

    public void onLeScan(BleDevice bleDevice) {
    }
}
