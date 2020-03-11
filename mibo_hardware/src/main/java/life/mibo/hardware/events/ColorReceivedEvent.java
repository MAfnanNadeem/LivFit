/*
 *  Created by Sumeet Kumar on 1/23/20 3:37 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 5:24 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

import life.mibo.hardware.models.Device;

/**
 * Created by Fer on 09/04/2019.
 */

public class ColorReceivedEvent {

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    public ColorReceivedEvent(String uid) {
        this.uid = uid;
    }

}
