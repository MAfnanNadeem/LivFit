package life.mibo.hardware.events;

/**
 * Created by Fer on 25/03/2019.
 */

public class SendDevicePlayEvent {
    private String uid;

    public SendDevicePlayEvent(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
