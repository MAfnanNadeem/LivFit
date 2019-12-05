package life.mibo.hardware.events;

public class DevicePlayPauseEvent {
    private String uid;

    public DevicePlayPauseEvent(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }



}


