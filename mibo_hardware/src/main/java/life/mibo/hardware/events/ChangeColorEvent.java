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
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

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

    public int getTime() {
        return time;
    }

    public Device getDevice() {
        return device;
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
}
