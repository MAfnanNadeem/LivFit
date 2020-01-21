package life.mibo.hardware.events;

import life.mibo.hardware.models.Device;

public class DeviceStatusEvent {
    private String uid;
    private Device device;


    public DeviceStatusEvent(String uid) {
        this.uid = uid;

    }

    public DeviceStatusEvent(Device device) {
        this.uid = "";
        this.device = device;

    }

    public String getUid() {
        return uid;
    }

    public Device getDevice() {
        return device;
    }
}


