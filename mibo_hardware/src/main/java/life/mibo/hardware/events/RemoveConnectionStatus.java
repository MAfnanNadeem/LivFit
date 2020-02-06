/*
 *  Created by Sumeet Kumar on 2/2/20 11:31 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 12/5/19 4:04 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

/**
 * Created by Fer on 30/04/2019.
 */

public class RemoveConnectionStatus {
    private String uid;

    public RemoveConnectionStatus(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}

