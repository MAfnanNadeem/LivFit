/*
 *  Created by Sumeet Kumar on 1/23/20 3:24 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 8:33 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

import androidx.annotation.NonNull;

import java.util.Arrays;

import life.mibo.hardware.core.Logger;

/**
 * Created by Sumeet Kumar on 1/23/20 3:24 PM.
 */

public class RxlStatusEvent {


    private String uid;
    private byte[] command;


    public RxlStatusEvent(byte[] command, String uid) {
        this.command = command;
        this.uid = uid;

    }

    public RxlStatusEvent(byte[] command, String uid, int data) {
        this.command = command;
        this.uid = uid;
        this.data = data;

    }

    public String getUid() {
        return uid;
    }

    public byte[] getCommand() {
        return command;
    }

    public int getTime() {
        if (command.length > 2) {
            int a = command[2] & 0xff;
            int b = command[3] & 0xff;
            b *= 256;
            b += a;
            return b;
        }
        return 0;
    }

    private int data = 0;

    public int getData() {
        Logger.e("RxlStatusEvent getData " + Arrays.toString(command));
        if (command.length > 3)
            return command[4] & 0xff;
        return data;
    }

    public String getTimeString() {
        StringBuilder time = new StringBuilder();
        if (command.length > 2) {
            float a = command[2] & 0xff;
            float b = command[3] & 0xff;
            //time.append(a);
            //time.append(" ");
            //time.append(b);
            //time.append(" - ");
            b = b * 256;
            b += a;
            time.append(String.format("%.0f", b));
            time.append(" ms");
            time.append(" - ");
            time.append(String.format("%.3f", b / 1000));
        }
        time.append(" sec");
        return time.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return "uid " + uid + " cmd " + Arrays.toString(command) + " : data " + data;
    }
}
