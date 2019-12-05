package life.mibo.hardware.models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Fer on 16/03/2019.
 */

public class DeviceBle implements Serializable {

    @SerializedName("deviceName")
    String deviceName;

    @SerializedName("deviceAdress")
    String deviceAdress;

    public DeviceBle(String deviceName, String deviceAdress) {
        this.deviceName = deviceName;
        this.deviceAdress = deviceAdress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAdress() {
        return deviceAdress;
    }

    public JSONObject toJSON() {
        JSONObject deviceBle = new JSONObject();

        try {
            deviceBle.accumulate("deviceName", getDeviceName());
            deviceBle.accumulate("deviceAddress", getDeviceAdress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return deviceBle;
    }
}
