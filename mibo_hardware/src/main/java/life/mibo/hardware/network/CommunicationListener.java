package life.mibo.hardware.network;

import android.bluetooth.le.ScanResult;

import java.net.InetAddress;

import life.mibo.hardware.models.Device;

public interface CommunicationListener {

    void onBluetoothDeviceFound(ScanResult result);

    void onConnectionStatus(String getname);

    void onAlarmEvent();

    void onDeviceDiscoveredEvent(String s);
    void onDeviceDisconnect(String uid);

    void onDeviceDiscoveredEvent(Device s);

    void HrEvent(byte[] hr, String uid, int property);

    void DeviceStatusEvent(String uid);

    void ChangeColorEvent(Device d, String uid);

    void GetMainLevelEvent(int mainLevel, String uid);

    void GetLevelsEvent(String uid);

    void onStatus(int time, int action, int pause, int currentBlock, int currentProgram, String uid);
    void onStatus(byte[] command, String uid);

    void onCommandReceived(int code, byte[] command, String uid);

    void DevicePlayPauseEvent(String uid);

    void udpDeviceReceiver(byte[] msg, InetAddress ip);
}