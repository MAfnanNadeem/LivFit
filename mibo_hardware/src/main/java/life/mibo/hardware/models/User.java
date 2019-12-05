package life.mibo.hardware.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import life.mibo.hardware.encryption.MCrypt;
import life.mibo.hardware.models.program.Circuit;
import life.mibo.hardware.models.program.Program;

/**
 * Created by Fer on 14/03/2019.
 */

public class User implements Serializable {

    private String id = "";
    private String name = "";
    private String lastName = "";
    private String image = "";
    private int age = 0;
    private String birthdate = "";
    private String height = "";
    private String contact = "";
    private String email = "";
    private String address1 = "";
    private String address2 = "";
    private String zip = ""; //Postal code
    private String city = "";
    private String country = "";
    private String province = "";
    private String idNumber = "";
    private String latitude = "";
    private String longitude = "";
    private String primaryContactName = "";
    private String primaryContactRelation = "";
    private String primaryPhone = "";
    private String primaryContactEmail = "";
    private String secondaryContactName = "";
    private String secondaryContactRelation = "";
    private String secondaryPhone = "";
    private String secondaryContactEmail = "";
    private MedicalHistory medicalHistory = new MedicalHistory();
    private ArrayList<TrainingGoals> trainingGoals;
    private int remainingSessions = 0;
    private int missedSessions = 0;
    private int completedSessions = 0;


    private int userFriendlycolor = 0;

    private ArrayList<Device> userDevices = new ArrayList<>();


    private boolean isPresent = true;

    private boolean isActive = true;

    private int[] userChannelLevels = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int[] userChannelCaps = new int[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100};

    private int[] currentChannelLevels = new int[]{25, 25, 25, 25, 25, 25, 25, 25, 25, 25};//new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int stimulationState = 0;

    private int mainLevel = 0;

    private int currentHeartRate = 0;

    private int peakHeartRate = 0;

    private int restHeartRate = 0;

    private int currentCalories = 0;


    private int userSessionTimer = 0;

    private String userSessionNotes = "None";

    private Long tsLastHr = System.currentTimeMillis();

    private ArrayList<Integer> acumulatedHeartRate = new ArrayList<>();


    private Long tsLastStatus = System.currentTimeMillis();

    private Circuit circuitProgram;
    private life.mibo.hardware.models.program.Program Program;


    private ArrayList<Device> devices;

    public User(String name, String lastName, String id) {
        this.name = name;
        this.lastName = lastName;
        this.id = id;
    }

    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public User(User userToCopy) {
        this.id = userToCopy.id;
        this.name = userToCopy.name;
        this.lastName = userToCopy.lastName;
        this.image = userToCopy.image;
        this.age = userToCopy.age;
        this.birthdate = userToCopy.birthdate;
        this.height = userToCopy.height;
        this.contact = userToCopy.contact;
        this.email = userToCopy.email;
        this.address1 = userToCopy.address1;
        this.address2 = userToCopy.address2;
        this.zip = userToCopy.zip;
        this.city = userToCopy.city;
        this.country = userToCopy.country;
        this.province = userToCopy.province;
        this.idNumber = userToCopy.idNumber;
        this.latitude = userToCopy.latitude;
        this.longitude = userToCopy.longitude;
        this.primaryContactName = userToCopy.primaryContactName;
        this.primaryContactRelation = userToCopy.primaryContactRelation;
        this.primaryPhone = userToCopy.primaryPhone;
        this.primaryContactEmail = userToCopy.primaryContactEmail;
        this.secondaryContactName = userToCopy.secondaryContactName;
        this.secondaryContactRelation = userToCopy.secondaryContactRelation;
        this.secondaryPhone = userToCopy.secondaryPhone;
        this.secondaryContactEmail = userToCopy.secondaryContactEmail;
        this.remainingSessions = userToCopy.remainingSessions;
        this.missedSessions = userToCopy.missedSessions;
        this.completedSessions = userToCopy.completedSessions;
        this.isPresent = userToCopy.isPresent;
        this.userChannelLevels = userToCopy.userChannelLevels;
        this.currentChannelLevels = userToCopy.currentChannelLevels;
        this.userChannelCaps = userToCopy.userChannelCaps;
    }

    private boolean isSelected = false;

    public User() {
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setId(String value) {
        id = value;
    }

    public void setName(String value) {
        name = value;
    }

    public void setLastName(String value) {
        lastName = value;
    }

    public void setImage(String value) {
        image = value;
    }

    public void setAge(int value) {
        age = value;
    }

    public void setBirthdate(String value) {
        birthdate = value;
    }

    public void setContact(String value) {
        contact = value;
    }

    public void setEmail(String value) {
        email = value;
    }

    public void setAddress1(String value) {
        address1 = value;
    }

    public void setAddress2(String value) {
        address2 = value;
    }

    public void setZip(String value) {
        zip = value;
    }

    public void setCity(String value) {
        city = value;
    }

    public void setCountry(String value) {
        country = value;
    }

    public void setProvince(String value) {
        province = value;
    }

    public void setIdNumber(String value) {
        idNumber = value;
    }

    public void setLatitude(String value) {
        latitude = value;
    }

    public void setLongitude(String value) {
        longitude = value;
    }

    public void setPrimaryContactName(String value) {
        primaryContactName = value;
    }

    public void setPrimaryContactRelation(String value) {
        primaryContactRelation = value;
    }

    public void setPrimaryPhone(String value) {
        primaryPhone = value;
    }

    public void setPrimaryContactEmail(String value) {
        primaryContactEmail = value;
    }

    public void setSecondaryContactName(String value) {
        secondaryContactName = value;
    }

    public void setSecondaryContactRelation(String value) {
        secondaryContactRelation = value;
    }

    public void setSecondaryPhone(String value) {
        secondaryPhone = value;
    }

    public void setSecondaryContactEmail(String value) {
        secondaryContactEmail = value;
    }

    public String getUserSessionNotes() {
        if (userSessionNotes.equals("")) {
            userSessionNotes = "None";
        }
        return userSessionNotes;
    }

    public void setUserSessionNotes(String userSessionNotes) {
        this.userSessionNotes = userSessionNotes;
    }

    public int getUserSessionTimer() {
        return userSessionTimer;
    }

    public void setUserSessionTimer(int userSessionTimer) {
        this.userSessionTimer = userSessionTimer;
    }

    public void incrementUserSessionTimer() {
        this.userSessionTimer++;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getImage() {
        return image;
    }

    public int getAge() {
        return age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public String getPrimaryContactRelation() {
        return primaryContactRelation;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    public String getSecondaryContactName() {
        return secondaryContactName;
    }

    public String getSecondaryContactRelation() {
        return secondaryContactRelation;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public String getSecondaryContactEmail() {
        return secondaryContactEmail;
    }

    public MedicalHistory getMedicalHistory() {
        return medicalHistory;
    }

    public ArrayList<TrainingGoals> trainingGoals() {
        return trainingGoals;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setMedicalHistory(MedicalHistory medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public ArrayList<TrainingGoals> getTrainingGoals() {
        return trainingGoals;
    }

    public void setTrainingGoals(ArrayList<TrainingGoals> trainingGoals) {
        this.trainingGoals = trainingGoals;
    }

    public Device getUserBooster() {
        for (Device t : userDevices) {
            if (t.getType() == DeviceTypes.WIFI_STIMULATOR || t.getType() == DeviceTypes.BLE_STIMULATOR) {
                return t;
            }
        }
        return new Device("x", "", "x", DeviceTypes.WIFI_STIMULATOR);
    }

    public Device getUserHrMonitor() {
        for (Device t : userDevices) {
            if (t.getType() == DeviceTypes.HR_MONITOR) {
                return t;
            }
        }
        return new Device("x", "", "x", DeviceTypes.HR_MONITOR);
    }


    public void setUserDevices(ArrayList<Device> userDevices) {
        this.userDevices = userDevices;
    }


    public void addUserDevice(Device userDevice) {
        boolean newDevice = true;
        int auxindex = -1;
//        for (Device t : userDevices) {
//            if (t.getUid().equals(userDevice.getUid())) {
//                newDevice = false;
//            }
//        }
//        if (newDevice) {
//            this.userDevices.add(userDevice);
//        }
        for (Device t : userDevices) {
            if (t.getType() == DeviceTypes.WIFI_STIMULATOR || t.getType() == DeviceTypes.BLE_STIMULATOR) {
                auxindex = userDevices.indexOf(t);
            }
        }
        if (auxindex != -1) {
            userDevices.remove(auxindex);
        }
        this.userDevices.add(userDevice);

    }

    public void addUserHrDevice(Device userDevice) {
        boolean newDevice = true;
        int auxindex = -1;
        for (Device t : userDevices) {
            if (t.getType() == DeviceTypes.HR_MONITOR) {
                auxindex = userDevices.indexOf(t);
            }
        }
        if (auxindex != -1) {
            userDevices.remove(auxindex);
        }
        this.userDevices.add(userDevice);

    }

    public void removeUserDevice(String id) {
        int aux = -1;
        for (Device t : userDevices) {
            if (t.getUid().equals(id)) {
                aux = userDevices.indexOf(t);
            }
        }
        if (aux != -1)
            userDevices.remove(aux);
    }

    public int[] getUserChannelLevels() {
        return userChannelLevels;
    }

    public void setUserChannelLevels(int[] userChannelLevels) {
        this.userChannelLevels = userChannelLevels;
    }

    public int[] getCurrentChannelLevels() {
        return currentChannelLevels;
    }

    public void setCurrentChannelLevels(int[] currentChannelLevels) {
        this.currentChannelLevels = currentChannelLevels;
    }

    public void incrementChannelLevelUserSelected(int muscleGroup) {
        if (getCurrentChannelLevels()[muscleGroup - 1] < 100)
            getCurrentChannelLevels()[muscleGroup - 1]++;
    }

    public void decrementChannelLevelUserSelected(int muscleGroup) {
        if (getCurrentChannelLevels()[muscleGroup - 1] > 0)
            getCurrentChannelLevels()[muscleGroup - 1]--;
    }

    public int getMainLevel() {
        return mainLevel;
    }

    public void setMainLevel(int mainLevel) {
        this.mainLevel = mainLevel;
    }

    public void incrementMainLevelUser() {
        if (getMainLevel() < 100)
            setMainLevel(getMainLevel() + 1);
    }

    public void decrementMainLevelUser() {
        if (getMainLevel() > 0)
            setMainLevel(getMainLevel() - 1);
    }

    public int getMainLevelUserPlusOne() {
        if (getMainLevel() >= 100) {
            return getMainLevel();
        } else {
            return getMainLevel() + 1;
        }
    }

    public int getMainLevelUserMinusOne() {
        if (getMainLevel() <= 0) {
            return getMainLevel();
        } else {
            return getMainLevel() - 1;
        }
    }

    public int getHr() {
        return currentHeartRate;
    }

    public void setHr(int hr) {
        this.currentHeartRate = hr;
        if (this.currentHeartRate > getPeakHeartRate()) {
            setPeakHeartRate(hr);
        }
        if (this.currentHeartRate < getRestHeartRate()) {
            setPeakHeartRate(hr);
        }

        if (tsLastHr + 10000 < System.currentTimeMillis()) {
            tsLastHr = System.currentTimeMillis();
            addAcumulatedHeartRate(hr);
        }

    }

    public int getPeakHeartRate() {
        return peakHeartRate;
    }

    public void setPeakHeartRate(int peakHeartRate) {
        this.peakHeartRate = peakHeartRate;
    }

    public int getRestHeartRate() {
        return restHeartRate;
    }

    public void setRestHeartRate(int restHeartRate) {
        this.restHeartRate = restHeartRate;
    }

    public int[] getAcumulatedHeartRate() {
        int[] mArray;
        Object[] mObjArray = acumulatedHeartRate.toArray();
        mArray = new int[acumulatedHeartRate.size()];
        for (int i = 0; i < mArray.length; i++) {
            mArray[i] = (int) mObjArray[i];
        }
        return mArray;
    }

    public void setAcumulatedHeartRate(ArrayList<Integer> acumulatedkHeartRate) {
        this.acumulatedHeartRate = acumulatedkHeartRate;
    }

    public void addAcumulatedHeartRate(int hr) {
        this.acumulatedHeartRate.add(hr);
    }

    public int getCurrentCalories() {
        return currentCalories;
    }

    public void setCurrentCalories(int currentCalories) {
        this.currentCalories = currentCalories;
    }

    public Circuit getCircuitProgram() {
        return circuitProgram;
    }

    public void setCircuitProgram(Circuit circuitProgram) {
        this.circuitProgram = circuitProgram;
    }

    public life.mibo.hardware.models.program.Program getProgram() {
        return Program;
    }

    public void setProgram(life.mibo.hardware.models.program.Program program) {
        Program = new Program(program);
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    public int getStimulationState() {
        return stimulationState;
    }

    public void setStimulationState(int remainingProgramAction, int remainingProgramPause) {
        if (remainingProgramAction > 0) {
            this.stimulationState = 0;
        } else {
            this.stimulationState = 1;
        }
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof User) {
            sameSame = this.id.equals(((User) object).id);
        }

        return sameSame;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(this.id);
    }

    public static boolean containsId(ArrayList<User> list, String id) {
        for (User object : list) {
            if (object.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getRemainingSessions() {
        return remainingSessions;
    }

    public void setRemainingSessions(int remainingSessions) {
        this.remainingSessions = remainingSessions;
    }

    public int getMissedSessions() {
        return missedSessions;
    }

    public void setMissedSessions(int missedSessions) {
        this.missedSessions = missedSessions;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions;
    }

    public ArrayList<Device> getUserDevices() {
        return userDevices;
    }

    public void setStimulationState(int stimulationState) {
        this.stimulationState = stimulationState;
    }

    public int getCurrentHeartRate() {
        return currentHeartRate;
    }

    public void setCurrentHeartRate(int currentHeartRate) {
        this.currentHeartRate = currentHeartRate;
    }

    public int getUserFriendlycolor() {
        return userFriendlycolor;
    }

    public void setUserFriendlycolor(int userFriendlycolor) {
        this.userFriendlycolor = userFriendlycolor;
    }

    public Long getTsLastStatus() {
        return tsLastStatus;
    }

    public void setTsLastStatus(Long tsLastStatus) {
        this.tsLastStatus = tsLastStatus;
    }

    public void setUserSimpleDataFromJSONAPI(JSONObject userData) {
        MCrypt mcrypt = new MCrypt();
        try {
            setId(userData.getString("id"));
            setName(new String(mcrypt.decrypt(userData.getString("firstName"))));
            setLastName(new String(mcrypt.decrypt(userData.getString("lastName"))));
            setEmail(new String(mcrypt.decrypt(userData.getString("email"))));
            setImage(new String(mcrypt.decrypt(userData.getString("imageThumbnail"))));
            setAge(Integer.parseInt(new String(mcrypt.decrypt(userData.getString("age")))));
            setContact(new String(mcrypt.decrypt(userData.getString("contact"))));


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserDetailsFromJSONAPI(JSONObject userData) {
        MCrypt mcrypt = new MCrypt();
        try {
            setId(userData.getString("id"));
            setName(new String(mcrypt.decrypt(userData.getString("firstName"))));
            setLastName(new String(mcrypt.decrypt(userData.getString("lastName"))));
            setEmail(new String(mcrypt.decrypt(userData.getString("email"))));
            setImage(new String(mcrypt.decrypt(userData.getString("imageThumbnail"))));
            setAge(Integer.parseInt(new String(mcrypt.decrypt(userData.getString("age")))));
            setContact(new String(mcrypt.decrypt(userData.getString("contact"))));
            setBirthdate(new String(mcrypt.decrypt(userData.getString("dob"))));
            setContact(new String(mcrypt.decrypt(userData.getString("contact"))));
            setAddress1(new String(mcrypt.decrypt(userData.getString("address1"))));
            setAddress2(new String(mcrypt.decrypt(userData.getString("address2"))));
            setZip(new String(mcrypt.decrypt(userData.getString("zip"))));
            setCity(new String(mcrypt.decrypt(userData.getString("city"))));
            setCountry(new String(mcrypt.decrypt(userData.getString("country"))));
            setProvince(new String(mcrypt.decrypt(userData.getString("province"))));
            setIdNumber(new String(mcrypt.decrypt(userData.getString("identificationNumber"))));
            setLatitude(new String(mcrypt.decrypt(userData.getString("latitude"))));
            setLongitude(new String(mcrypt.decrypt(userData.getString("longitude"))));
            setPrimaryContactName(new String(mcrypt.decrypt(userData.getString("primaryContactName"))));
            setPrimaryContactRelation(new String(mcrypt.decrypt(userData.getString("primaryContactRelation"))));
            setPrimaryPhone(new String(mcrypt.decrypt(userData.getString("primaryPhone"))));
            setPrimaryContactEmail(new String(mcrypt.decrypt(userData.getString("primaryContactEmail"))));
            setSecondaryContactName(new String(mcrypt.decrypt(userData.getString("secondaryContactName"))));
            setSecondaryContactRelation(new String(mcrypt.decrypt(userData.getString("secondaryContactRelation"))));
            setSecondaryPhone(new String(mcrypt.decrypt(userData.getString("secondaryPhone"))));
            setSecondaryContactEmail(new String(mcrypt.decrypt(userData.getString("secondaryContactEmail"))));


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
