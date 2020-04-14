/*
 *  Created by Sumeet Kumar on 2/3/20 12:27 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/19/20 9:50 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import life.mibo.android.models.base.BaseResponse;

public class EventBusEvent {

    private Object data;
    private int id;

    public EventBusEvent(int id, @Nullable Object data) {
        this.data = data;
        this.id = id;
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
        return "EventBusEvent id=" + id;
    }


    public static String join(CharSequence delimiter,
                              Iterable<? extends CharSequence> elements) {

        return "";
    }

    <V extends BaseResponse> void showError(V elements) {


    }
}
