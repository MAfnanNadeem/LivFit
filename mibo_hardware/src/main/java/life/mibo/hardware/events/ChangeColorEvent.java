package life.mibo.hardware.events;

import life.mibo.hardware.models.Device;

/**
 * Created by Fer on 09/04/2019.
 */

public class ChangeColorEvent {

    private Device device;

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

    public Device getDevice() {
        return device;
    }

}
