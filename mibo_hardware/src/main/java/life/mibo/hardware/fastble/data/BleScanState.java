/*
 *  Created by Sumeet Kumar on 3/22/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/22/20 5:16 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.fastble.data;



public enum BleScanState {

    STATE_IDLE(-1),
    STATE_SCANNING(0X01);

    private int code;

    BleScanState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
