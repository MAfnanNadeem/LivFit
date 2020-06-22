package life.mibo.hardware.events;

public class DevicePlayPauseEvent {
    private String uid;
    private int data = -1;

    public DevicePlayPauseEvent(String uid) {
        this.uid = uid;
    }

    public DevicePlayPauseEvent(String uid, int data) {
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

    public boolean startResponse() {
        return data == 3;
    }

    public boolean pauseResponse() {
        return data == 4;
    }

}


