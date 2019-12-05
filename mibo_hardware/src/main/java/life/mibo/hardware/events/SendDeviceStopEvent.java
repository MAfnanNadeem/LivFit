package life.mibo.hardware.events;

/**
 * Created by Fer on 25/03/2019.
 */

public class SendDeviceStopEvent {
    private String uid;

    public SendDeviceStopEvent(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
