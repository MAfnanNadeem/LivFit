package life.mibo.hardware.models;


import life.mibo.hardware.SessionManager;

public class Alarm {

    private Integer AlarmCode = 0;
    private Long TimeStamp = new Long(0);
    private String Description = "";
    private String uid = "";
    private String userName = "";
    private String friendlySerial = "";

    public Alarm (Integer alarmCode, String description, String uid){
       this.AlarmCode = alarmCode;
       this.TimeStamp =  System.currentTimeMillis()/1000;
       this.Description = description;
       this.uid = uid;
       this.userName = SessionManager.getInstance().getSession().getUserByBoosterUid(uid).getName();
       this.friendlySerial = SessionManager.getInstance().getSession().getRegisteredDevicebyUid(uid).getFriendlySerialNumber();
    }
    public Integer getAlarmCode() {
        return AlarmCode;
    }

    public void setAlarmCode(Integer alarmCode) {
        AlarmCode = alarmCode;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFullAlarmText() {
        return (Description+": "+userName+ "-"+friendlySerial);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendlySerial() {
        return friendlySerial;
    }

    public void setFriendlySerial(String friendlySerial) {
        this.friendlySerial = friendlySerial;
    }
}
