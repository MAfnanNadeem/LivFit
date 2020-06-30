/*
 *  Created by Sumeet Kumar on 5/4/20 10:45 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 12/5/19 4:04 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

public class IndicationEvent {
    private String uid;
    private byte[] data;

    public IndicationEvent(String uid, byte[] hr) {
        this.uid = uid;
        this.data = hr;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}


