/*
 *  Created by Sumeet Kumar on 1/23/20 3:24 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 8:33 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

/**
 * Created by Sumeet Kumar on 1/23/20 3:24 PM.
 */

public class RxtTileConfigEvent {


    private String uid;
    private byte[] command;

    public RxtTileConfigEvent(byte[] command, String uid) {
        this.command = command;
        this.uid = uid;
    }

}
