package life.mibo.hardware.events;

/**
 * Created by Fer on 30/04/2019.
 */

public class NewConnectionStatus {
    private String uid;

    public NewConnectionStatus( String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}

