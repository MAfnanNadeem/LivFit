/*
 *  Created by Sumeet Kumar on 1/19/20 9:45 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/19/20 9:45 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LightsEvent {

    private Object data;
    private int id;

    public LightsEvent(int id, @Nullable Object data) {
        this.data = data;
        this.id = id;
    }
    public LightsEvent(int type, int commands) {

    }

    public LightsEvent turnOn() {
        return this;
    }

    public LightsEvent turnOff() {
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "LightsEvent id=" + id;
    }
}
