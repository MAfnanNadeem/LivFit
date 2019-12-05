package life.mibo.hardware.events;

public class DeviceStatusEvent {
    private String uid;


    public DeviceStatusEvent(String uid) {
        this.uid = uid;

    }

    public String getUid() {
        return uid;
    }

}


