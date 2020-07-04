/*
 *  Created by Sumeet Kumar on 7/2/20 2:38 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/2/20 2:38 PM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.models;

import com.onecoder.devicelib.base.protocol.entity.ScaleStableData;
import com.onecoder.devicelib.utils.Utils;

public class ScaleData {
    public static final int UNIT_KG = 0;
    public static final int UNIT_LB = 1;
    public static final int UNIT_ST = 2;
    public static final int UNIT_G = 3;
    private long weightUtc;
    private float weight;
    private int weightUnit = 0;
    private float eleImpedance;
    private float encryptImpedance;
    private int deviceType;

    public ScaleData() {
    }

    public static ScaleData from(ScaleStableData scale) {
        if (scale == null)
            return null;
        ScaleData data = new ScaleData();
        data.deviceType = scale.getDeviceType();
        data.weight = scale.getWeight();
        data.weightUnit = scale.getWeightUnit();
        data.weightUtc = scale.getWeightUtc();
        return data;
    }

    public long getWeightUtc() {
        return this.weightUtc;
    }

    public void setWeightUtc(long weightUtc) {
        this.weightUtc = weightUtc;
    }

    public int getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = (float) Utils.getReservedDecimal((double) weight, 2);
    }

    public int getWeightUnit() {
        return this.weightUnit;
    }

    public void setWeightUnit(int weightUnit) {
        this.weightUnit = weightUnit;
    }

    public float getEleImpedance() {
        return this.eleImpedance;
    }

    public void setEleImpedance(float eleImpedance) {
        this.eleImpedance = (float) Utils.getReservedDecimal((double) eleImpedance, 1);
    }

    public float getEncryptImpedance() {
        return this.encryptImpedance;
    }

    public void setEncryptImpedance(float encryptImpedance) {
        this.encryptImpedance = (float) Utils.getReservedDecimal((double) encryptImpedance, 1);
    }
}
