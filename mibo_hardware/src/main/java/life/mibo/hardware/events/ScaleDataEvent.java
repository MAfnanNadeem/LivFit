/*
 *  Created by Sumeet Kumar on 3/5/20 1:56 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/5/20 1:56 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.events;

import life.mibo.hardware.models.ScaleData;

public class ScaleDataEvent {

    private float weight;
    private ScaleData data;


    public ScaleDataEvent(ScaleData data, float weight) {
        this.data = data;
        this.weight = 0f;
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public ScaleData getData() {
        return data;
    }
}
