/*
 *  Created by Sumeet Kumar on 1/14/20 8:13 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/14/20 8:13 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NotifyEvent {
    private Object data;
    private int id;

    public NotifyEvent(int id, @Nullable Object data) {
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
        return "NotifyEvent id=" + id;
    }
}
