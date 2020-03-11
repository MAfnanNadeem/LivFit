package life.mibo.hardware;

import java.util.ArrayList;
import java.util.Arrays;

import life.mibo.hardware.models.Device;
import life.mibo.hardware.models.DeviceTypes;
import life.mibo.hardware.models.Session;
import life.mibo.hardware.models.Trainer;
import life.mibo.hardware.models.User;
import life.mibo.hardware.models.UserSession;
import life.mibo.hardware.models.program.Block;
import life.mibo.hardware.models.program.Circuit;
import life.mibo.hardware.models.program.Program;

/**
 * Created by Fer on 18/03/2019.
 */

public class SessionManager {


    public static final int NO_LOGIN = 0;
    public static final int ID_LOGIN = 1;
    public static final int TRAINER_LOGIN = 2;

    private static SessionManager mSessionMInstance;

    private Session session;
    private UserSession userSession;

    private int deviceBatteryLevel = 0;
    private boolean deviceCharging = false;
    private int deviceWifiLevel = 0;
    private int deviceWifiState = 0;
    private String deviceWifiName = "";


    private int loginStatus = NO_LOGIN;

    private SessionManager(){
        if (mSessionMInstance != null){
            throw new RuntimeException("getInstance() to get the instance of this class");
        }
    }

    public static SessionManager getInstance(){
        if (mSessionMInstance == null){
            mSessionMInstance = new SessionManager();
        }
        return mSessionMInstance;
    }

    public static void initUser() {
        if (getInstance().userSession == null)
            getInstance().userSession = new UserSession();
    }

    public Session getSession() {
        return session;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public UserSession getUserSession() {
        if (userSession == null)
            userSession = new UserSession();
        return userSession;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void createDummyProgram() {
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

        getUserSession().setProgram(program);
    }

    public void createDummySession(){
        session = new Session();
            session.setTrainer(new Trainer());
            session.getTrainer().setId(1);
            session.getTrainer().setFirstName("Sumeet");
            session.getTrainer().setLastName("Kumar");
            session.getTrainer().setDesignation("MI.BO");
            //session.getTrainer().setLocationInfo(new LocationInfo());
            session.getTrainer().setImageThumbnail("");
            session.getTrainer().setAge(28);
            session.getTrainer().setContact("058 552 4744");
            session.setBoosterMode(true);


        //session.setCurrentSessionProgram(program);

//        session.getTrainer().setTrainerPrograms(getLocalPrograms());
//        session.getTrainer().setTrainerCircuits(getLocalCircuits());

        ArrayList<User> Users = new ArrayList<User>() ;
        User newUser = new User("Name1", "LName1", "1");
        Users.add(newUser);
        newUser = new User("Name2", "LName12", "2");
        Users.add(newUser);
        newUser = new User("Name3", "LName13", "3");
        Users.add(newUser);
        session.setCurrentSessionParticipants(Users);

    }

    public static ArrayList<Program> getLocalPrograms() {

        ArrayList<Program> progList = new ArrayList<Program>() ;
        ////////////Simple EMS Programs
        Program program = new Program();
        program.setDuration(900);
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
        progList.add(program);
        //////////////////////
        program = new Program();
        program.setDuration(900);
        program.setId("2");
        program.setName("Cardio (Local)");
        program.setDescription("Local-EMS-Cardio training program(15min)");
        program.setBorgRating(12);
        block1 = new Block();
        block1.setBlockDuration(1000);
        block1.setPauseDuration(0);
        block1.setActionDuration(1000);
        block1.setUpRampDuration(0);
        block1.setDownRampDuration(0);
        block1.setPulseWidth(350);
        block1.setFrequency(7);
        blocks = new Block[]{block1};
        program.setBlocks(blocks);
        progList.add(program);
        //////////////////////
        program = new Program();
        program.setDuration(300);
        program.setId("3");
        program.setName("Cool Down (Local)");
        program.setBorgRating(8);
        program.setDescription("Local-EMS-Relax Program(5min)");
        block1 = new Block();
        block1.setBlockDuration(2000);
        block1.setPauseDuration(1000);
        block1.setActionDuration(1000);
        block1.setUpRampDuration(0);
        block1.setDownRampDuration(0);
        block1.setPulseWidth(150);
        block1.setFrequency(100);
        blocks = new Block[]{block1};
        program.setBlocks(blocks);
        progList.add(program);
        //////////////////////
        program = new Program();
        program.setDuration(900);
        program.setId("4");
        program.setName("Flexibility (Local)");
        program.setBorgRating(15);
        program.setDescription("Local-EMS-Flexibility(15min)");
        block1 = new Block();
        block1.setBlockDuration(29000);
        block1.setPauseDuration(15000);
        block1.setActionDuration(10000);
        block1.setUpRampDuration(2000);
        block1.setDownRampDuration(2000);
        block1.setPulseWidth(200);
        block1.setFrequency(40);
        blocks = new Block[]{block1};
        program.setBlocks(blocks);
        progList.add(program);
        /////////////////////
        program = new Program();
        program.setDuration(1800);
        program.setId("5");
        program.setName("Endurance (Local)");
        program.setBorgRating(16);
        program.setDescription("Local-EMS-Flexibility(35min)");
        block1 = new Block();
        block1.setBlockDuration(41000);
        block1.setPauseDuration(20000);
        block1.setActionDuration(15000);
        block1.setUpRampDuration(3000);
        block1.setDownRampDuration(3000);
        block1.setPulseWidth(300);
        block1.setFrequency(45);
        blocks = new Block[]{block1};
        program.setBlocks(blocks);
        progList.add(program);
        //////////////////////

        //////// TENS Programs
        program = new Program();
        program.setDuration(3600);
        program.setId("10");
        program.setName("Acute Pain (Local)");
        program.setDescription("Local-TENS-C-Acute Pain(60min) Freq:100Hz Pulse 180us");
        program.setBorgRating(6);
        block1 = new Block();
        block1.setBlockDuration(1000);
        block1.setPauseDuration(0);
        block1.setActionDuration(1000);
        block1.setUpRampDuration(0);
        block1.setDownRampDuration(0);
        block1.setPulseWidth(180);
        block1.setFrequency(100);
        blocks = new Block[]{block1};
        program.setBlocks(blocks);
        progList.add(program);
        /////////////////////////
        program = new Program();
        program.setDuration(1800);
        program.setId("11");
        program.setName("Chronic Pain (Local)");
        program.setDescription("Local-TENS-C-Chronic Pain(30min) Freq:10Hz Pulse 180us");
        program.setBorgRating(6);
        block1 = new Block();
        block1.setBlockDuration(1000);
        block1.setPauseDuration(0);
        block1.setActionDuration(1000);
        block1.setUpRampDuration(0);
        block1.setDownRampDuration(0);
        block1.setPulseWidth(180);
        block1.setFrequency(10);
        blocks = new Block[]{block1};
        program.setBlocks(blocks);
        progList.add(program);
        program = new Program();
        program.setDuration(1800);
        program.setId("12");
        program.setName("Back Pain (Local)");
        program.setDescription("Local-TENS-C-Back Pain(30min) Freq:30Hz Pulse 220us");
        program.setBorgRating(6);
        block1 = new Block();
        block1.setBlockDuration(1000);
        block1.setPauseDuration(0);
        block1.setActionDuration(1000);
        block1.setUpRampDuration(0);
        block1.setDownRampDuration(0);
        block1.setPulseWidth(220);
        block1.setFrequency(30);
        blocks = new Block[]{block1};
        program.setBlocks(blocks);
        progList.add(program);
        program = new Program();
        program.setDuration(1800);
        program.setId("13");
        program.setName("Phantom Limb Pain (Local)");
        program.setDescription("Local-TENS-C-Chronic Pain(30min) Freq:180Hz Pulse 140us");
        program.setBorgRating(6);
        block1 = new Block();
        block1.setBlockDuration(1000);
        block1.setPauseDuration(0);
        block1.setActionDuration(1000);
        block1.setUpRampDuration(0);
        block1.setDownRampDuration(0);
        block1.setPulseWidth(200);
        block1.setFrequency(100);
        blocks = new Block[]{block1};
        program.setBlocks(blocks);
        progList.add(program);

        return progList;
    }

    private ArrayList<Circuit> getLocalCircuits() {

        ArrayList<Circuit> circuitList = new ArrayList<Circuit>() ;
        ArrayList<Program> progList = new ArrayList<Program>() ;
        ////////////Simple EMS Programs
        Program program = new Program();
        program.setDuration(900);
        program.setId("6");
        program.setName("Resistance");
        program.setDescription("EMS-Resistance(15min)");
        Block block1 = new Block();
        block1.setBlockDuration(8800);
        block1.setPauseDuration(0000);
        block1.setActionDuration(8000);
        block1.setUpRampDuration(400);
        block1.setDownRampDuration(400);
        block1.setPulseWidth(350);
        block1.setFrequency(20);
        Block block2 = new Block();//
        block2.setBlockDuration(4200);
        block2.setPauseDuration(0000);
        block2.setActionDuration(4000);
        block2.setUpRampDuration(100);
        block2.setDownRampDuration(100);
        block2.setPulseWidth(350);
        block2.setFrequency(7);
        Block[] blocks = new Block[]{block1,block2};
        program.setBlocks(blocks);
        progList.add(program);
        //////////////////////
        program = new Program();
        program.setDuration(900);
        program.setId("7");
        program.setName("Functional Core");
        program.setDescription("EMS-Functional Core(15min)");
        block1 = new Block();
        block1.setBlockDuration(9600);
        block1.setPauseDuration(0);
        block1.setActionDuration(8000);
        block1.setUpRampDuration(800);
        block1.setDownRampDuration(800);
        block1.setPulseWidth(350);
        block1.setFrequency(75);
        block2 = new Block();//
        block2.setBlockDuration(4200);
        block2.setPauseDuration(0000);
        block2.setActionDuration(4000);
        block2.setUpRampDuration(100);
        block2.setDownRampDuration(100);
        block2.setPulseWidth(350);
        block2.setFrequency(7);
        blocks = new Block[]{block1,block2};
        program.setBlocks(blocks);
        progList.add(program);
        //////////////////////
        program = new Program();
        program.setDuration(900);
        program.setId("8");
        program.setName("Explosive Strength");
        program.setDescription("EMS-Explosive Strength(15min)");
        block1 = new Block();
        block1.setBlockDuration(4600);
        block1.setPauseDuration(0000);
        block1.setActionDuration(4000);
        block1.setUpRampDuration(300);
        block1.setDownRampDuration(300);
        block1.setPulseWidth(250);
        block1.setFrequency(120);
        block2 = new Block();//
        block2.setBlockDuration(8200);
        block2.setPauseDuration(0000);
        block2.setActionDuration(8000);
        block2.setUpRampDuration(100);
        block2.setDownRampDuration(100);
        block2.setPulseWidth(250);
        block2.setFrequency(7);
        blocks = new Block[]{block1,block2};
        program.setBlocks(blocks);
        progList.add(program);
        //////////////////////
        program = new Program();
        program.setDuration(900);
        program.setId("9");
        program.setName("Toning");
        program.setDescription("EMS-Toning(15min)");
        block1 = new Block();
        block1.setBlockDuration(7000);
        block1.setPauseDuration(0000);
        block1.setActionDuration(6000);
        block1.setUpRampDuration(500);
        block1.setDownRampDuration(500);
        block1.setPulseWidth(350);
        block1.setFrequency(50);
        block2 = new Block();//
        block2.setBlockDuration(4200);
        block2.setPauseDuration(0000);
        block2.setActionDuration(4000);
        block2.setUpRampDuration(100);
        block2.setDownRampDuration(100);
        block2.setPulseWidth(350);
        block2.setFrequency(7);
        blocks = new Block[]{block1,block2};
        program.setBlocks(blocks);
        progList.add(program);

        //////////////////////
        Circuit circuit1 = new Circuit();
        circuit1.setName("Circuit 1");
        circuit1.setDescription("Resistance(15min), Functional Core(15min), Explosive Strength(15min), Toning(15min)");
        circuit1.setPrograms(Arrays.copyOf(progList.toArray(), progList.toArray().length, Program[].class));
        circuitList.add(circuit1);

        return circuitList;
    }

    public void fillDummyUsers() {
        Integer aux = 0;
        for (Device d : session.getConnectedDevices()) {
            if(d.getType().equals(DeviceTypes.BLE_STIMULATOR) || d.getType().equals(DeviceTypes.WIFI_STIMULATOR)){
                aux++;
                if(session.getCurrentSessionParticipants().size()<aux){
                    User newUser = new User("Name"+aux, "LName"+aux, ""+aux);
                    session.getCurrentSessionParticipants().add(newUser);
                }
            }
        }
    }
    public void set10DummyUsers() {
        Integer aux = 0;
        for (int i=0;i <10;i++) {

                aux++;
                if(session.getCurrentSessionParticipants().size()<10){
                    User newUser = new User("Name"+aux, "LName"+aux, ""+aux);
                    session.getCurrentSessionParticipants().add(newUser);
                }

        }
    }

    public int getDeviceBatteryLevel() {
        return deviceBatteryLevel;
    }

    public void setDeviceBatteryLevel(int deviceBatteryLevel) {
        this.deviceBatteryLevel = deviceBatteryLevel;
    }

    public boolean isDeviceCharging() {
        return deviceCharging;
    }

    public void setDeviceCharging(boolean deviceCharging) {
        this.deviceCharging = deviceCharging;
    }

    public int getDeviceWifiLevel() {
        return deviceWifiLevel;
    }

    public void setDeviceWifiLevel(int deviceWifiLevel) {
        this.deviceWifiLevel = deviceWifiLevel;
    }

    public int getDeviceWifiState() {
        return deviceWifiState;
    }

    public void setDeviceWifiState(int deviceWifiState) {
        this.deviceWifiState = deviceWifiState;
    }

    public String getDeviceWifiName() {
        return deviceWifiName;
    }

    public void setDeviceWifiName(String deviceWifiName) {
        this.deviceWifiName = deviceWifiName;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }


}
