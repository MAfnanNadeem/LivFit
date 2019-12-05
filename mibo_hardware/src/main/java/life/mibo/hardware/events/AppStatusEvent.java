package life.mibo.hardware.events;

public class AppStatusEvent {
    private int wifiSignal;
    private int battery;
    private boolean charging;

    public AppStatusEvent(int wifiSignal,int batteryLevel, boolean isCharging) {
        this.wifiSignal = wifiSignal;
        this.battery = batteryLevel;
        this.charging = isCharging;
    }

    public int getWifiSignal() {
        return wifiSignal;
    }

    public int getBattery() {
        return battery;
    }

    public boolean isCharging() {
        return charging;
    }



}


