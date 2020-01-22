/*
 *  Created by Sumeet Kumar on 1/21/20 9:29 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/21/20 9:29 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PodEvent {

    private Pod pod;
    private Object data;
    private String uid;
    private boolean isPod4, isPod6, turnOn = false;

    public PodEvent(String uid, @Nullable Object data) {
        this.data = data;
        this.uid = uid;
    }

    public PodEvent(String uid, @Nullable Pod pod, boolean turnOn) {
        this.pod = pod;
        this.uid = uid;
        this.turnOn = turnOn;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    public Pod getPod() {
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

    public boolean isTurnOn() {
        return turnOn;
    }

    public void setTurnOn(boolean turnOn) {
        this.turnOn = turnOn;
    }
}
