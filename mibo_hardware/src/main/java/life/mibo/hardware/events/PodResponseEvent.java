/*
 *  Created by Sumeet Kumar on 1/28/20 11:36 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 3:45 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

/**
 * Created by Sumeet Kumar on 1/23/20 3:24 PM.
 */

public class PodResponseEvent {


    private String uid;
    private byte[] command;


    public PodResponseEvent(byte[] command, String uid) {
        this.command = command;
        this.uid = uid;

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

    public int getTapTime() {
        if (command.length > 2) {
            int a = command[2] & 0xff;
            int b = command[3] & 0xff;
            b *= 256;
            b += a;
            return b;
        }
        return 0;
    }

    public String  getTimeString() {
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
            time.append(String.format("%.3f", b/1000));
        }
        time.append(" sec");
        return time.toString();
    }


}
