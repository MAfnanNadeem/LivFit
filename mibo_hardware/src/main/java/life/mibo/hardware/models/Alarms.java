package life.mibo.hardware.models;

import android.content.Context;

import java.util.ArrayList;


import life.mibo.hardware.R;

import static life.mibo.hardware.models.DeviceConstants.*;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_10_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_1_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_2_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_3_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_4_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_5_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_6_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_7_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_8_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_CHANNEL_9_ELECTRODE_CONTACT;
import static life.mibo.hardware.models.DeviceConstants.DEVICE_ALARM_DISCONNECTED;

public class Alarms {

    private Context mContext;

    private ArrayList<Alarm> registeredAlarmsSession = new ArrayList<>();
    private ArrayList<Alarm> currentAlarmsSession = new ArrayList<>();

    boolean newAlarm = false;

    public ArrayList<Alarm> getRegisteredAlarmsSession() {
        return registeredAlarmsSession;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setRegisteredAlarmsSession(ArrayList<Alarm> registeredAlarmsSession) {
        this.registeredAlarmsSession = registeredAlarmsSession;
    }

    public ArrayList<Alarm> getCurrentAlarmsSession() {
        return currentAlarmsSession;
    }

    public void setCurrentAlarmsSession(ArrayList<Alarm> currentAlarmsSession) {
        this.currentAlarmsSession = currentAlarmsSession;
    }

    public boolean isNewAlarm() {
        return newAlarm;
    }

    public void setNewAlarm(boolean newAlarm) {
        this.newAlarm = newAlarm;
    }

    public void clearCurrentAlarms() {
        try {
            currentAlarmsSession.clear();
            newAlarm = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAlarms() {
        try {
            registeredAlarmsSession.clear();
            currentAlarmsSession.clear();
            newAlarm = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewAlarm(Alarm alarm) {
        registeredAlarmsSession.add(alarm);
        currentAlarmsSession.add(alarm);
    }

    public void AddDeviceChannelAlarm(boolean[] channelsAlarm, String uid) {
        for(int i = 0; i<= 9; i++) {
            if (channelsAlarm[i]) {
                AddDeviceAlarmByType(i+1, uid);
            }
        }

    }

    public boolean AddDeviceAlarmByType(int alarmCode, String uid) {
        Alarm newAlarm;
        boolean newAlarmCreated = false;
        switch(alarmCode){
            case DEVICE_ALARM_DISCONNECTED:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_disconnection_device),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_1_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_1),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_2_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_2),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_3_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_3),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_4_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_4),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_5_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_5),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_6_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_6),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_7_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_7),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_8_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_8),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_9_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_9),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            case DEVICE_ALARM_CHANNEL_10_ELECTRODE_CONTACT:
                if(isNewAlarm(alarmCode, uid)) {
                    newAlarm = new Alarm(alarmCode, mContext.getResources().getString(R.string.txt_alarm_contact_group_10),uid);
                    newAlarmCreated = true;
                    addNewAlarm(newAlarm);
                }
                break;
            default:
                break;

        }
        if(newAlarmCreated){
            this.newAlarm = true;
        }
        return newAlarmCreated;
    }

    private boolean isNewAlarm(int alarmCode, String uid){
        boolean aux = true;
        for (Alarm a : currentAlarmsSession) {
            if((a.getAlarmCode()== alarmCode) && a.getUid().equals(uid)){
                aux = false;
            }
        }
        return aux;
    }

}
