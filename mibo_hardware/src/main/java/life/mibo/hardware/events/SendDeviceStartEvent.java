package life.mibo.hardware.events;

public class SendDeviceStartEvent {
    private String uid;

    public SendDeviceStartEvent(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
