package life.mibo.hardware.models;

import android.bluetooth.BluetoothDevice;
import android.os.CountDownTimer;

import java.util.ArrayList;

import life.mibo.hardware.SessionManager;
import life.mibo.hardware.core.Logger;
import life.mibo.hardware.models.program.Program;

/**
 * Created by Fer on 18/03/2019.
 */

public class UserSession {

    public static final int NOT_STARTED = 0;
    public static final int SESSION_PAUSED = 1;
    public static final int SESSION_IN_PROGRESS = 2;
    public static final int SESSION_FINISHED = 3;

    private String locationId = "";
    private User user;
    private Device device;
    private String currentSessionId = "";
    private String currentSessionName = "";

    private Program currentSessionProgram;
    private String currentSessionstartDatetime = "";
    private String currentSessionendDatetime = "";

    private String currentSessionNotes = "none";

    private int currentSessionMainLevelMultiplier = 0;

    private int currentSessionStatus = 0; //0 no sesion. 1 session loaded but paused.  2 session started

    private int currentSessionTimer = 0;
    private static CountDownTimer cTimer = null;

    private ArrayList<Device> registeredDevices = new ArrayList<>();

    private ArrayList<Device> connectedDevices = new ArrayList<>();
    private BluetoothDevice connectedScale;

    private boolean boosterMode = true; // true wifi false ble
    private boolean isStarted;

    public Device getDevice() {
        return device;
    }

    public User getUser() {
        return user;
    }

    public boolean isStarted() {
        if (device != null)
            return device.getIsStarted();
        return isStarted;
    }

    public static UserSession from(Device device) {
        UserSession session = new UserSession();
        if (device != null) {
            session.device = device;
            session.userId = device.getUid();
            session.user = new User("Sumeet", "Kumar", session.userId);
            session.currentSessionProgram = Program.getDummyProgram();
        }
        return session;
    }

    public String getUid() {
        if (device != null)
            return device.getUid();
        return userId;
    }

    public ArrayList<Device> getRegisteredDevices() {
        return registeredDevices;
    }

    public void addRegisteredDevice(Device device) {
        boolean newDevice = true;
        for (Device d : registeredDevices) {
            if (d.getUid().equals(device.getUid())) {
                newDevice = false;
            }
        }
        if (newDevice) {
            this.registeredDevices.add(device);
        }
    }

    public void removeRegisteredDevice(Device device) {
        if (registeredDevices != null) {
            int aux = -1;
            for (Device t : registeredDevices) {
                if (t.getUid().equals(device.getUid())) {
                    aux = registeredDevices.indexOf(t);
                }
            }
            if (aux != -1)
                registeredDevices.remove(aux);
        }
    }

    public Device getRegisteredDevicebyUid(String uid) {
        boolean newDevice = true;
        for (Device d : registeredDevices) {
            if (d.getUid().equals(uid)) {
                return d;
            }
        }

        return new Device();
    }

    public void setRegisteredDevices(ArrayList<Device> registeredDevices) {
        this.registeredDevices = registeredDevices;
    }

    public ArrayList<Device> getConnectedDevices() {
        return connectedDevices;
    }

    public void addConnectedDevice(Device device) {
        boolean newDevice = true;
        for (Device d : connectedDevices) {
            if (d.getUid().equals(device.getUid())) {
                newDevice = false;
            }
        }
        if (newDevice) {
            this.connectedDevices.add(device);
        }
    }

    public void addScale(BluetoothDevice device) {
        connectedScale = device;
    }

    public BluetoothDevice getScale() {
        return connectedScale;
    }

    public void removeConnectedDevice(Device device) {
        int aux = -1;
        for (Device t : connectedDevices) {
            if (t.getUid().equals(device.getUid())) {
                aux = connectedDevices.indexOf(t);
            }
        }
        if (aux != -1) {
            this.connectedDevices.remove(aux);
        }
    }

    public void setConnectedDevices(ArrayList<Device> registeredDevices) {
        this.connectedDevices = registeredDevices;
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public void setCurrentSessionId(String currentSessionId) {
        this.currentSessionId = currentSessionId;
    }

    public String getCurrentSessionNotes() {
        return currentSessionNotes;
    }

    public void setCurrentSessionNotes(String currentSessionNotes) {
        this.currentSessionNotes = currentSessionNotes;
    }

    public String getCurrentSessionName() {
        return currentSessionName;
    }

    public void setCurrentSessionName(String currentSessionName) {
        this.currentSessionName = currentSessionName;
    }

    public String getCurrentSessionstartDatetime() {
        return currentSessionstartDatetime;
    }

    public void setCurrentSessionstartDatetime(String currentSessionstartDatetime) {
        this.currentSessionstartDatetime = currentSessionstartDatetime;
    }

    public String getCurrentSessionendDatetime() {
        return currentSessionendDatetime;
    }

    public void setCurrentSessionendDatetime(String currentSessionendDatetime) {
        this.currentSessionendDatetime = currentSessionendDatetime;
    }

    public boolean isBoosterMode() {
        return boosterMode;
    }

    public void setBoosterMode(boolean boosterMode) {
        this.boosterMode = boosterMode;
    }


    public User getUserById(String Id) {
        return new User("x", "x");
    }

    public User getUserByBoosterUid(String Uid) {
        return user;
        //return new User("x", "x");
    }

    public User getUserByHrUid(String Uid) {
        return new User("x", "x");
    }

    public boolean isColorSelected(int color) {
        return false;
    }


    public int getCurrentSessionStatus() {
        return currentSessionStatus;//0 no sesion. 1 session loaded but paused.  2 session started. 3 session finish
    }

    public void setCurrentSessionStatus(int currentSessionStatus) {
        this.currentSessionStatus = currentSessionStatus;
    }


    public int getCurrentSessionTimer() {
        return currentSessionTimer;
    }

    public void setCurrentSessionTimer(int seconds) {
        this.currentSessionTimer = seconds;
    }

    public int getCurrentSessionMainLevelMultiplier() {
        return currentSessionMainLevelMultiplier;
    }

    public void setCurrentSessionMainLevelMultiplier(int currentSessionMainLevelMultiplier) {
        this.currentSessionMainLevelMultiplier = currentSessionMainLevelMultiplier;
    }

    public Program getCurrentSessionProgram() {
        return currentSessionProgram;
    }

    public void setCurrentSessionProgram(Program currentSessionProgram) {
        this.currentSessionProgram = new Program(currentSessionProgram);
    }

    String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    //start timer function
    public void startTimer(long s) {
        if (cTimer != null) {
            cTimer.cancel();
            cTimer = null;
        }

        cTimer = new CountDownTimer(s * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                setCurrentSessionTimer((int) (millisUntilFinished / 1000));
//                for (int i = 0; i < currentSessionParticipants.size(); i++) {
//                    if (currentSessionParticipants.get(i).isActive()) {
//                        currentSessionParticipants.get(i).incrementUserSessionTimer();
//                    }
//
//                }
                // Log.e("timersess", ""+millisUntilFinished/1000);
            }

            public void onFinish() {
                cancelTimer();
                if (listener != null)
                    listener.SessionFinishEvent();
                //EventBus.getDefault().postSticky(new SessionFinishEvent());
                setCurrentSessionStatus(3);
            }
        };
        cTimer.start();
    }

    //cancel timer
    public void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    public CountDownTimer getSessionTimer() {
        return cTimer;
    }

    public void checkDeviceStatus(boolean[] status, String uid) {
        Logger.w("NOTHING................ checkStatus: wifi " + status[0] + " ble " + status[1] +
                " program " + status[2] + " runn " + status[3] + " color " + status[4]);

        if (getCurrentSessionStatus() == 2 || getCurrentSessionStatus() == 1) {
            if (device.getUid().equals(uid)) {
                if (!status[2]) {//check if program
                    getRegisteredDevicebyUid(uid);
                    if (listener != null)
                        listener.SendProgramEvent(getCurrentSessionProgram(), uid);
                    Logger.w("checkDeviceStatus SendProgramEvent");
                    //EventBus.getDefault().postSticky(new SendProgramEvent(getCurrentSessionProgram(), uid));
                }
                if (!status[4]) {//check if color if (listener != null)
                    if (listener != null)
                        listener.ChangeColorEvent(device, uid);

                    Logger.w("checkDeviceStatus ChangeColorEvent");
                   // EventBus.getDefault().postSticky(new ChangeColorEvent(getUserByBoosterUid(uid).getUserBooster(), uid));
                }
                if (status[3]) {//check if run

                }
                if (status.length >= 6) {
                    if (!status[5]) {//check if channels are loaded
                        if (listener != null)
                            listener.SendChannelsLevelEvent(user.getCurrentChannelLevels(), uid);
                        //EventBus.getDefault().postSticky(new SendChannelsLevelEvent(getUserByBoosterUid(uid).getCurrentChannelLevels(), uid));
                        Logger.w("checkDeviceStatus SendChannelsLevelEvent");
                    }
                }

            }
        }
    }

    public void checkDeviceStatus2(boolean[] status, String uid) {
        Logger.e("NOTHING................ checkStatus: wifi " + status[0] + " ble " + status[1] +
                " program " + status[2] + " runn " + status[3] + " color " + status[4]);
//        if (getCurrentSessionStatus() == 2 || getCurrentSessionStatus() == 1) {
//            for (User u : getCurrentSessionParticipants()) {
//                if (u.getUserBooster().getUid().equals(uid)) {
//                    if (!status[2]) {//check if program
//                        getRegisteredDevicebyUid(uid);
//                        if (listener != null) {
//                            listener.SendProgramEvent(getCurrentSessionProgram(), uid);
//                            listener.SendChannelsLevelEvent(getUserByBoosterUid(uid).getCurrentChannelLevels(), uid);
//                        }
//                        //EventBus.getDefault().postSticky(new SendProgramEvent(getCurrentSessionProgram(),uid));
//                        //EventBus.getDefault().postSticky(new SendChannelsLevelEvent(getUserByBoosterUid(uid).getCurrentChannelLevels(), uid));
//
//                    }
//                    if (!status[4]) {//check if color
//                        if (listener != null)
//                            listener.ChangeColorEvent(getUserSelected().getUserBooster(), uid);
//                        //EventBus.getDefault().postSticky(new ChangeColorEvent(getUserSelected().getUserBooster(), uid));
//
//                    }
//                    if (status[3]) {//check if run
//
//                    }
//                }
//            }
//        }
//        boolean auxNewalarm = false;
//        for(int i=0; i<=7; i++) {
//            if(newalarms[i] != deviceChannelAlarms[i]) {
//                auxNewalarm = true;
//            }
//        }
//        deviceChannelAlarms = newalarms;
    }

    public SessionReport getSessionReport() {
        SessionReport report = new SessionReport();
        report.setSessionID(Integer.parseInt(currentSessionId));
        //report.setTrainerID(trainer.getId());
        report.setLocationID(Integer.parseInt(locationId));
        report.setStartDatetime(currentSessionstartDatetime);
        report.setEndDatetime(currentSessionendDatetime);
        report.setDuration(currentSessionProgram.getDuration().getValueInt());
        report.setProgramCircuitName(currentSessionProgram.getName());
        report.setBreaks(0);
        if (currentSessionNotes.equals("")) {
            currentSessionNotes = "None";
        }
        report.setTrainerIssuesLog(currentSessionNotes);
        //report.setMemberReport(new MemberReport[currentSessionParticipants.size()]);

//        for (int i = 0; i < currentSessionParticipants.size(); i++) {
//            MemberReport auxMember = new MemberReport();
//            auxMember.SetMemberReport(currentSessionParticipants.get(i));
//            report.getMemberReport()[i] = auxMember;
//        }
        return report;
    }

    private Sessionlistener listener;

    public void setListener(Sessionlistener listener) {
        this.listener = listener;
    }

    public Sessionlistener getListener() {
        return listener;
    }

    public interface Sessionlistener {
        void SendProgramEvent(Program program, String uid);

        void SendChannelsLevelEvent(int[] channels, String uid);

        void SessionFinishEvent();

        void ChangeColorEvent(Device device, String uid);
    }

    public String debug() {
        return "UserSession{" +
                "locationId='" + locationId + '\'' +
                ", user=" + user +
                ", device=" + device +
                ", currentSessionId='" + currentSessionId + '\'' +
                ", currentSessionName='" + currentSessionName + '\'' +
                ", currentSessionProgram=" + currentSessionProgram +
                ", currentSessionstartDatetime='" + currentSessionstartDatetime + '\'' +
                ", currentSessionendDatetime='" + currentSessionendDatetime + '\'' +
                ", currentSessionNotes='" + currentSessionNotes + '\'' +
                ", currentSessionMainLevelMultiplier=" + currentSessionMainLevelMultiplier +
                ", currentSessionStatus=" + currentSessionStatus +
                ", currentSessionTimer=" + currentSessionTimer +
                ", registeredDevices=" + registeredDevices +
                ", connectedDevices=" + connectedDevices +
                ", connectedScale=" + connectedScale +
                ", boosterMode=" + boosterMode +
                ", isStarted=" + isStarted +
                ", userId='" + userId + '\'' +
                ", listener=" + listener +
                '}';
    }
}
