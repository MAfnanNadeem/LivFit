package life.mibo.hardware.events;

/**
 * Created by Fer on 25/03/2019.
 */

public class GetMainLevelEvent {

    private int level;
    private String uid;

    public GetMainLevelEvent(int level, String uid) {
        this.level = level;
        this.uid = uid;
    }

    public int getLevel() {
        return level;
    }

    public String getUid() {
        return uid;
    }
}
