/*
 *  Created by Sumeet Kumar on 4/11/20 12:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/11/20 12:06 PM
 *  Mibo Hexa - imagepicker
 */

package life.mibo.imagepicker.rxbus.event;

public class RequestStorageReadAccessPermissionEvent {

    public static final int TYPE_CAMERA = 0;
    public static final int TYPE_WRITE = 1;

    private final boolean success;
    private final int type;

    public RequestStorageReadAccessPermissionEvent(boolean success, int type) {
        this.success = success;
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getType() {
        return type;
    }

}
