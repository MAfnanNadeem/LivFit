package life.mibo.hardware.events;

import life.mibo.hardware.models.Device;

/**
 * Created by Fer on 09/04/2019.
 */

public class ChangeColorEvent {

    private Device device;
    private int time = 0;
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

    public int getTime() {
        return time;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "ChangeColorEvent{" +
                ", time=" + time +
                ", uid='" + uid + '\'' +
                '}';
    }
}
