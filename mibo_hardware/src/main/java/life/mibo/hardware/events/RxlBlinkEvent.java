/*
 *  Created by Sumeet Kumar on 3/3/20 8:37 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/26/20 9:05 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

import life.mibo.hardware.models.Device;

public class RxlBlinkEvent {

    private Device device;
    private int timeOn = 0;
    private int timeOff = 0;
    private int cycles = 0;
    private int color = 0;
    private String uid = "";

    public RxlBlinkEvent(String uid, int timeOn, int timeOff, int cycles, int color) {
        this.timeOn = timeOn;
        this.timeOff = timeOff;
        this.cycles = cycles;
        this.color = color;
        this.uid = uid;
    }

    public RxlBlinkEvent(String uid, int time, int cycles, int color) {
        this.timeOn = time;
        this.timeOff = time;
        this.cycles = cycles;
        this.color = color;
        this.uid = uid;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public int getTimeOn() {
        return timeOn;
    }

    public void setTimeOn(int timeOn) {
        this.timeOn = timeOn;
    }

    public int getTimeOff() {
        return timeOff;
    }

    public void setTimeOff(int timeOff) {
        this.timeOff = timeOff;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
