package life.mibo.hardware.events;

public class DevicePauseResumeEvent {
    private String uid;
    private int data = -1;

    public DevicePauseResumeEvent(String uid) {
        this.uid = uid;
    }

    public DevicePauseResumeEvent(String uid, int data) {
        this.uid = uid;
        this.data = data;
    }

    public String getUid() {
        return uid;
    }

    public int getData() {
        return data;
    }

    public boolean start() {
        return data == 1;
    }

    public boolean pause() {
        return data == 2;
    }

}


