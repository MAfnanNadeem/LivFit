package life.mibo.hardware;

import android.content.Context;

import life.mibo.hardware.models.Alarm;
import life.mibo.hardware.models.Alarms;

public class AlarmManager {

    private static AlarmManager mAlarmMInstance;

    private Alarms alarms = new Alarms();

    private AlarmManager(Context context){
        if (mAlarmMInstance != null){
            throw new RuntimeException("getInstance() to get the istance of this class");
        }
        alarms.setContext(context);
    }

    private AlarmManager(){
        if (mAlarmMInstance != null){
            throw new RuntimeException("getInstance() to get the istance of this class");
        }
    }

    public static AlarmManager getInstance(){
        if (mAlarmMInstance == null){
            mAlarmMInstance = new AlarmManager();
        }
        return mAlarmMInstance;
    }

    public static AlarmManager getInstance(Context context){
        if (mAlarmMInstance == null){
            mAlarmMInstance = new AlarmManager(context);
        }
        return mAlarmMInstance;
    }

    public Alarms getAlarms() {
        return alarms;
    }

    public void setAlarmData(Alarms alarms) {
        this.alarms = alarms;
    }

    public void createDummyAlarms(){
        alarms.getCurrentAlarmsSession().add(new Alarm(101,"Channel 1 contact Alarm",""));
        alarms.getCurrentAlarmsSession().add(new Alarm(102,"Channel 2 contact Alarm",""));
        alarms.getCurrentAlarmsSession().add(new Alarm(103,"Channel 3 contact Alarm",""));
        alarms.getCurrentAlarmsSession().add(new Alarm(104,"Channel 4 contact Alarm",""));
        alarms.getCurrentAlarmsSession().add(new Alarm(105,"Channel 5 contact Alarm",""));
        alarms.getRegisteredAlarmsSession().add(new Alarm(101,"Channel 1 contact Alarm",""));
        alarms.getRegisteredAlarmsSession().add(new Alarm(102,"Channel 2 contact Alarm",""));
        alarms.getRegisteredAlarmsSession().add(new Alarm(103,"Channel 3 contact Alarm",""));
        alarms.getRegisteredAlarmsSession().add(new Alarm(104,"Channel 4 contact Alarm",""));
        alarms.getRegisteredAlarmsSession().add(new Alarm(105,"Channel 5 contact Alarm",""));

    }
}
