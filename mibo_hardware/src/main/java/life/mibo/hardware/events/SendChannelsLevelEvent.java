package life.mibo.hardware.events;

/**
 * Created by Fer on 25/03/2019.
 */

public class SendChannelsLevelEvent {

    private int[] channels;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    public SendChannelsLevelEvent(int[] channels, String uid) {
        this.channels = channels;
        this.uid = uid;
    }

    public int[] getLevels() {
        return channels;
    }

}
