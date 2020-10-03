package life.mibo.hardware.events;

import androidx.annotation.Nullable;

import life.mibo.hardware.models.Device;

/**
 * Created by Fer on 09/04/2019.
 */

public class ChangeColorEvent {

    private Device device;
    private int time = 0;
    private int data = 0;
    private int lightType = 2; // 0=Square, 1=Circle, 2=Both
    private String tileId;
    private int color;
    private String uid;

    public ChangeColorEvent(String uid, String tileId, int color, int time, int data) {
        this.uid = uid;
        this.tileId = tileId;
        this.color = color;
        this.time = time;
        this.data = data;
        this.lightType = 2;
    }

    public ChangeColorEvent(String uid, String tileId, int color, int time, int data, int lightType) {
        this.uid = uid;
        this.tileId = tileId;
        this.color = color;
        this.time = time;
        this.data = data;
        this.lightType = lightType;
    }


    public ChangeColorEvent(Device device, String uid) {
        this.device = device;
        this.uid = uid;
    }

    public ChangeColorEvent(Device device, String uid, int time) {
        this.device = device;
        this.uid = uid;
        this.time = time;
    }

    public ChangeColorEvent(Device device, String uid, int time, int data) {
        this.device = device;
        this.uid = uid;
        this.time = time;
        this.data = data;
    }

    public String getUid() {
        return uid;
    }

    public int getTime() {
        return time;
    }

    public Device getDevice() {
        return device;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChangeColorEvent{" +
                ", time=" + time +
                ", uid='" + uid + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            return obj != null && uid.equals(((ChangeColorEvent) obj).uid);
        } catch (Exception e) {

        }
        return super.equals(obj);
    }


    public int getColor() {
        if (device != null)
            return device.getColorPalet();
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTileId() {
        return tileId;
    }


    public int getTileIdInt() {
        try {
            return Integer.parseInt(tileId);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setTileId(String tileId) {
        this.tileId = tileId;
    }

    public int getLightType() {
        return lightType;
    }
}
