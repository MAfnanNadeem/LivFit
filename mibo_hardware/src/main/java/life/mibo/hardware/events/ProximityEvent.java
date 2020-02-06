/*
 *  Created by Sumeet Kumar on 2/4/20 4:01 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 5:24 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;


public class ProximityEvent {

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String uid;
    private int type;

    public ProximityEvent(String uid, int type) {
        this.uid = uid;
        this.type = type;
    }


}
