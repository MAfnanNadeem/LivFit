/*
 *  Created by Sumeet Kumar on 1/28/20 11:35 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 3:33 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import life.mibo.hardware.models.Device;
//import life.mibo.hexa.pods.pod.Pod;

public class PodEvent {

    private Device pod;
    private String uid;
    // private boolean isPod4, isPod6, turnOn = false;
    private int color, time;
    private boolean all = false;

    public PodEvent(String uid, @Nullable Device pod) {
        this.pod = pod;
        this.uid = uid;
        // this.turnOn = turnOn;
    }

    public PodEvent(String uid, int color, int time) {
        this.uid = uid;
        this.color = color;
        this.time = time;
        all = false;
    }

    public PodEvent(String uid, int color, int time, boolean all) {
        this.uid = uid;
        this.color = color;
        this.time = time;
        this.all = all;
    }

    public int getTime() {
        return time;
    }

    public int getColor() {
        return color;
    }

    public boolean isAll() {
        return all;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPod(Device pod) {
        this.pod = pod;
    }

    public Device getPod() {
        return pod;
    }

    @NonNull
    @Override
    public String toString() {
        return "PodEvent uid=" + uid;
    }

    //@Override
    public boolean equals(@Nullable PodEvent obj) {
        if (pod != null && obj != null && obj.pod != null)
            return pod.getUid() == obj.pod.getUid();
        return uid == (obj != null ? obj.uid : -1);
        //return super.equals(obj);
    }
}
