/*
 *  Created by Sumeet Kumar on 3/5/20 1:56 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/5/20 1:56 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

import androidx.annotation.Nullable;

import life.mibo.hardware.models.Device;

public class DelayColorEvent {

    private Device device;
    private int time = 0;
    private int data = 0;
    private int delay = 0;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;


    public DelayColorEvent(Device device, String uid, int time, int data, int delay) {
        this.device = device;
        this.uid = uid;
        this.time = time;
        this.data = data;
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getTime() {
        return time;
    }

    public Device getDevice() {
        return device;
    }


    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChangeColorEvent{" +
                ", time=" + time +
                ", uid='" + uid + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            return obj != null && uid.equals(((DelayColorEvent) obj).uid);
        } catch (Exception e) {

        }
        return super.equals(obj);
    }
}
