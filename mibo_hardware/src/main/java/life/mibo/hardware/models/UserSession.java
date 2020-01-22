package life.mibo.hardware.models;

import android.bluetooth.BluetoothDevice;
import android.os.CountDownTimer;

import java.util.ArrayList;

import life.mibo.hardware.core.Logger;
import life.mibo.hardware.models.program.Block;
import life.mibo.hardware.models.program.Program;

/**
 * Created by Sumeet on 18/12/2019.
 */
// Copy of Session class for Consumer App (LivFit/Hexa)
public class UserSession {

    public static final int NOT_STARTED = 0;
    public static final int SESSION_PAUSED = 1;
    public static final int SESSION_IN_PROGRESS = 2;
    public static final int SESSION_FINISHED = 3;

    private String locationId = "";
    private User user;
    //private Device device;
    private Device booster;
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

//    public Device getDevice() {
//        return device;
//    }

    public User getUser() {
        return user;
    }

//    public boolean isStarted() {
//        if (device != null)
//            return device.getIsStarted();
//        return isStarted;
//    }

    public static UserSession create() {
        return new UserSession();
    }

//    public void setDevice(Device device) {
//        this.device = device;
//        if (device != null)
//            this.userId = device.getUid();
//    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProgram(Program program) {
        this.currentSessionProgram = program;
    }

    public Program getProgram() {
        return currentSessionProgram;
    }

    public static UserSession from(Device device) {
        UserSession session = new UserSession();
        if (device != null) {
            session.addDevice(device);
            session.userId = device.getUid();
            session.user = new User("Sumeet", "Kumar", session.userId);

            Program program = new Program();
            program.setDuration(300);
            program.setId("1");
            program.setName("Strength Training (Local)");
            program.setDescription("Local-EMS-Muscle/strength building program(15min)");
            program.setBorgRating(17);

            Block block1 = new Block();
            block1.setBlockDuration(8000);
            block1.setPauseDuration(4000);
            block1.setActionDuration(4000);
            block1.setUpRampDuration(0);
            block1.setDownRampDuration(0);
            block1.setPulseWidth(350);
            block1.setFrequency(85);
            Block[] blocks = new Block[]{block1};
            program.setBlocks(blocks);

            session.currentSessionProgram = program;
        }
        return session;
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
        //boolean newDevice = true;
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

    public ArrayList<Device> getDevices() {
        return connectedDevices;
    }

    public void addDevice(Device device) {
        addConnectedDevice(device);
    }

    public void removeDevice(Device device) {
        removeConnectedDevice(device);
    }

    public void removeDevice(String uid) {
        int aux = -1;

        for (int i = 0; i < connectedDevices.size(); i++) {
            if (connectedDevices.get(i).getUid().equals(uid)) {
                aux = i;
                break;
            }
        }
        if (aux != -1) {
            this.connectedDevices.remove(aux);
        }
    }

    private void addConnectedDevice(Device device) {
        for (Device d : connectedDevices) {
            if (d.getUid().equals(device.getUid())) {
                return;
            }
        }
        this.connectedDevices.add(device);
    }

    private void removeConnectedDevice(Device device) {
        int aux = -1;

        for (int i = 0; i < connectedDevices.size(); i++) {
            if (connectedDevices.get(i).getUid().equals(device.getUid())) {
                aux = i;
                break;
            }
        }
        //connectedDevices.remove(device);
        if (aux != -1) {
            this.connectedDevices.remove(aux);
        }
    }

    public void addScale(BluetoothDevice device) {
        connectedScale = device;
    }

    public BluetoothDevice getScale() {
        return connectedScale;
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

    private String userId;
    private String uid;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getUid(int pos) {
        if (pos >= 0 && pos < connectedDevices.size()) {
            Device device = connectedDevices.get(pos);
            if (device != null)
                return device.getUid();
        }
        return userId;
    }

    public void setBooster(Device booster) {
        this.booster = booster;
    }

    public Device getBooster() {
        if (booster == null) {
            for (Device device : connectedDevices) {
                if (device.getType() == DeviceTypes.BLE_STIMULATOR || device.getType() == DeviceTypes.WIFI_STIMULATOR) {
                    booster = device;
                    break;
                }
            }
        }
        return booster;
    }

    public ArrayList<Device> getRxl() {
        ArrayList<Device> list = new ArrayList<>();

        if (!connectedDevices.isEmpty()) {
            for (Device device : connectedDevices) {
                if (device.getType() == DeviceTypes.RXL_WIFI || device.getType() == DeviceTypes.RXL_BLE) {
                    list.add(device);
                }
            }
        }
        return list;
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
                Logger.e("UserSession CountDownTimer "+millisUntilFinished);
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
                //EventBus.getDefault().postSticky(new SessionFinishEvent());
                setCurrentSessionStatus(SESSION_FINISHED);
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
//        Logger.w("NOTHING................ checkStatus: wifi " + status[0] + " ble " + status[1] +
//                " program " + status[2] + " runn " + status[3] + " color " + status[4]);
//
//        if (getCurrentSessionStatus() == 2 || getCurrentSessionStatus() == 1) {
//            if (device.getUid().equals(uid)) {
//                if (!status[2]) {//check if program
//                    getRegisteredDevicebyUid(uid);
//                    if (listener != null)
//                        listener.SendProgramEvent(getCurrentSessionProgram(), uid);
//                    Logger.w("checkDeviceStatus SendProgramEvent");
//                    //EventBus.getDefault().postSticky(new SendProgramEvent(getCurrentSessionProgram(), uid));
//                }
//                if (!status[4]) {//check if color if (listener != null)
//                    if (listener != null)
//                        listener.ChangeColorEvent(device, uid);
//
//                    Logger.w("checkDeviceStatus ChangeColorEvent");
//                   // EventBus.getDefault().postSticky(new ChangeColorEvent(getUserByBoosterUid(uid).getUserBooster(), uid));
//                }
//                if (status[3]) {//check if run
//
//                }
//                if (status.length >= 6) {
//                    if (!status[5]) {//check if channels are loaded
//                        if (listener != null)
//                            listener.SendChannelsLevelEvent(user.getCurrentChannelLevels(), uid);
//                        //EventBus.getDefault().postSticky(new SendChannelsLevelEvent(getUserByBoosterUid(uid).getCurrentChannelLevels(), uid));
//                        Logger.w("checkDeviceStatus SendChannelsLevelEvent");
//                    }
//                }
//
//            }
//        }
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

    public String debug() {
        return "UserSession{" +
                "locationId='" + locationId + '\'' +
                ", user=" + user +
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
                '}';
    }
}
