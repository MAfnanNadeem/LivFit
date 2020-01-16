package life.mibo.hardware.models;


import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;

import static life.mibo.hardware.models.ConnectionTypes.BLE;
import static life.mibo.hardware.models.ConnectionTypes.WIFI;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_NEUTRAL;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_WARNING;
import static life.mibo.hardware.models.DeviceTypes.GENERIC;


/**
 * Created by Fer on 16/03/2019.
 */

public class Device implements Serializable {

    @SerializedName("id")
    private String id = "";

    @SerializedName("uid")
    private String uid = "";

    @SerializedName("type")
    private DeviceTypes type = GENERIC;

    private ConnectionTypes connectionType = WIFI;

    @SerializedName("FriendlyName")
    private String name = "New Device";


    @SerializedName("ColorPalet")
    private int colorPalet = 0;

    @SerializedName("ModelNumber")
    private String modelNumber = "MIBO-000000";

    @SerializedName("SerialNumber")
    private String serial = "000000";

    @SerializedName("FriendlySerialNumber")
    private String friendlySerialNumber = "000";

    @SerializedName("IPAddress")
    private InetAddress ip;

    @SerializedName("Image")
    private String image = "";


    private String characteristics = "";

    private String comments = "";

    private int active = 1;



    private int deviceSessionTimer = 0;

    private boolean isStarted = false;

    @SerializedName("selected")
    private boolean selected = false;

    private boolean assigned = false;

    @SerializedName("user")
    private User userAsigned;

    private int batteryLevel = 0;
    private int signalLevel = 0;

    private boolean[] deviceChannelAlarms = new boolean[]{false, false, false, false, false, false, false, false, false, false};



    private int statusConnected = DEVICE_NEUTRAL;

    public Device() {
        try{
            this.ip = InetAddress.getByName("0.0.0.0");
        }catch(Exception e){
            System.out.println(e);}
    }

    public Device(String name, byte[] uid, InetAddress ip, DeviceTypes type) {
        if(!name.equals(""))
        this.name = name;
        this.connectionType = WIFI;
        UiidToString(uid);
        this.ip = ip;
        this.type = type;
        setSerialFromUid(uid);
    }

    public Device(String name, String uid, String serial, DeviceTypes type) {
        if(!TextUtils.isEmpty(name))
            this.name = name;
        this.connectionType = BLE;
        try{
        this.ip = InetAddress.getByName("0.0.0.0");
        }catch(Exception e){
            System.out.println(e);}

        this.uid = uid.toUpperCase();

        if(serial.length()> 8){
            this.serial = uid.substring(uid.length() - 8).toLowerCase();
        }else{
            this.serial = serial;
        }
        this.type = type;
        this.statusConnected = DEVICE_WARNING;
    }

    public int getColorPalet() {
        return colorPalet;
    }

    public void setColorPalet(int colorPalet) {
        this.colorPalet = colorPalet;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid.toUpperCase();
    }

    public void UiidToString(byte[] uuid) {
        if(uuid.length<6){
            uid = "000000";
        }else{
            uid = (String.format("%02x", uuid[0] & 0xFF) + "" + String.format("%02x", uuid[1] & 0xFF) + "" + String.format("%02x", uuid[2] & 0xFF) + ""
                    + String.format("%02x", uuid[3] & 0xFF) + "" + String.format("%02x", uuid[4] & 0xFF) + "" + String.format("%02x", uuid[5] & 0xFF));
        }
        uid.toUpperCase();
    }

    public static String convetrUiidToString(byte[] uuid) {
        String auxuuid = "";
        if(uuid.length<6){
            auxuuid = "000000";
        }else{
            auxuuid = (String.format("%02x", uuid[0] & 0xFF) + "" + String.format("%02x", uuid[1] & 0xFF) + "" + String.format("%02x", uuid[2] & 0xFF) + ""
                    + String.format("%02x", uuid[3] & 0xFF) + "" + String.format("%02x", uuid[4] & 0xFF) + "" + String.format("%02x", uuid[5] & 0xFF));
        }
        return auxuuid.toUpperCase();
    }

    public ConnectionTypes getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionTypes connectionType) {
        this.connectionType = connectionType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InetAddress getIp() {
        return ip;
    }


    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getSerial() {
        return serial;
    }

    public void  setSerialFromUid(byte[] uid){

        if(uid.length<6){
            serial =  "000000";
        }else{
            serial =  (String.format("%02x", uid[2] & 0xFF) + "" + String.format("%02x", uid[3] & 0xFF) + ""
                    + String.format("%02x", uid[4] & 0xFF) + "" + String.format("%02x", uid[5] & 0xFF));
        }
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getFriendlySerialNumber() {
        return friendlySerialNumber;
    }

    public void setFriendlySerialNumber(String friendlySerialNumber) {
        this.friendlySerialNumber = friendlySerialNumber;
    }

    public DeviceTypes getType() {
        return type;
    }

    public void setType(DeviceTypes type) {
        this.type = type;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public int getStatusConnected() {
        return statusConnected;
    }

    public void setStatusConnected(int statusConnected) {
        this.statusConnected = statusConnected;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public boolean getIsStarted() {
        return isStarted;
    }

    public void setIsStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public int getDeviceSessionTimer() {
        return deviceSessionTimer;
    }

    public void setDeviceSessionTimer(int deviceSessionTimer) {
        this.deviceSessionTimer = deviceSessionTimer;
    }


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public boolean[] getDeviceChannelAlarms() {
        return deviceChannelAlarms;
    }

    public boolean setNewDeviceChannelAlarms(boolean[] newalarms) {
        boolean auxNewalarm = false;
        for(int i=0; i<=7; i++) {
            if(newalarms[i] != deviceChannelAlarms[i]) {
                auxNewalarm = true;
            }
        }
        deviceChannelAlarms = newalarms;
        return auxNewalarm;
    }

    public void setDeviceChannelAlarms(int index, boolean value) {
        this.deviceChannelAlarms[index] = value;
    }


    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(int Signal) {
        this.signalLevel = Signal;
    }

    public int getSignalLevelPercent() {
        int aux = 0;
        if(signalLevel<-85) {
            aux = 0;
        } else
        if(signalLevel>-45) {
            aux = 100;
        } else{
            aux = (int)(((double)(signalLevel+40)/(double)40)*-100.0);
        }

        return aux;
    }

    public JSONObject getJsonToRegisterDevice() {
        JSONObject device = new JSONObject();
        try {
            device.accumulate("UUID", getUid());
            device.accumulate("Type", getType());
            device.accumulate("FriendlyName", getName());
            device.accumulate("FriendlySerialNumber", getFriendlySerialNumber());
            device.accumulate("ColorPalet", getColorPalet());
            device.accumulate("Image", "");
            device.accumulate("ModelNumber", getModelNumber());
            device.accumulate("SerialNumber", getSerial());
            device.accumulate("IPAddress", getIp());
            device.accumulate("Status", "Connected");
            device.accumulate("Comments", "");
            device.accumulate("Characteristics", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return device;
    }

    public void setDataDeviceFromRegisterJSON(JSONObject device){
        try {
            this.setId(Integer.toString(device.getInt("id")));
            this.setUid(device.getString("UUID"));
            this.setSerial(device.getString("SerialNumber"));
            if(device.getString("Type").equals("BLE_STIMULATOR")){
                this.setType(DeviceTypes.BLE_STIMULATOR);
            } else
            if(device.getString("Type").equals("WIFI_STIMULATOR")){
                this.setType(DeviceTypes.WIFI_STIMULATOR);
            } else
            if(device.getString("Type").equals("HR_MONITOR")){
                this.setType(DeviceTypes.HR_MONITOR);
            }
            if(device.getString("Type").equals("SCALE")){
                this.setType(DeviceTypes.SCALE);
            }

            this.setName(device.getString("FriendlyName"));
            this.setFriendlySerialNumber(device.getString("FriendlySerialNumber"));
            this.setColorPalet(Integer.parseInt(device.getString("ColorPalet")));
            this.setCharacteristics(device.getString("Characteristics"));
            this.setComments(device.getString("Comments"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", type=" + type +
                ", connectionType=" + connectionType +
                ", name='" + name + '\'' +
                ", colorPalet=" + colorPalet +
                ", modelNumber='" + modelNumber + '\'' +
                ", serial='" + serial + '\'' +
                ", friendlySerialNumber='" + friendlySerialNumber + '\'' +
                ", ip=" + ip +
                ", image='" + image + '\'' +
                ", characteristics='" + characteristics + '\'' +
                ", comments='" + comments + '\'' +
                ", active=" + active +
                ", deviceSessionTimer=" + deviceSessionTimer +
                ", isStarted=" + isStarted +
                ", selected=" + selected +
                ", assigned=" + assigned +
                ", userAsigned=" + userAsigned +
                ", batteryLevel=" + batteryLevel +
                ", signalLevel=" + signalLevel +
                ", deviceChannelAlarms=" + Arrays.toString(deviceChannelAlarms) +
                ", statusConnected=" + statusConnected +
                '}';
    }

    public String print() {
        return "Device{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", type=" + type +
                ", connectionType=" + connectionType +
                ", name='" + name + '\'' +
                ", colorPalet=" + colorPalet +
                ", modelNumber='" + modelNumber + '\'' +
                ", serial='" + serial + '\'' +
                ", friendlySerialNumber='" + friendlySerialNumber + '\'' +
                ", ip=" + ip +
                ", image='" + image + '\'' +
                ", characteristics='" + characteristics + '\'' +
                ", comments='" + comments + '\'' +
                ", active=" + active +
                ", deviceSessionTimer=" + deviceSessionTimer +
                ", isStarted=" + isStarted +
                ", selected=" + selected +
                ", assigned=" + assigned +
                ", userAsigned=" + userAsigned +
                ", batteryLevel=" + batteryLevel +
                ", signalLevel=" + signalLevel +
                ", deviceChannelAlarms=" + Arrays.toString(deviceChannelAlarms) +
                ", statusConnected=" + statusConnected +
                '}';
    }
}