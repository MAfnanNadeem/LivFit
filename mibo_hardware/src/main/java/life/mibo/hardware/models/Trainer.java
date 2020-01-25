package life.mibo.hardware.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import life.mibo.hardware.encryption.MCrypt;
import life.mibo.hardware.models.program.Program;
import life.mibo.hardware.models.program.Circuit;
import life.mibo.hardware.models.program.Program;

/**
 * Created by Fer on 16/03/2019.
 */

public class Trainer implements BaseModel {

    int id;
    String firstName;
    String lastName;
    String designation;
    String trainerType;
    LocationInfo locationInfo;
    String imageThumbnail;
    int age;
    String contact;
    String access_token;
    String token_type;
    int expires_in;

    private ArrayList<Program> trainerPrograms = new ArrayList<Program>() ;
    private ArrayList<Circuit> trainerCircuits = new ArrayList<Circuit>() ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocationInfo getLocationInfo() {
        if(locationInfo ==null)
            locationInfo = new LocationInfo();
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String gettrainerType() {
        return trainerType;
    }

    public void settrainerType(String trainerType) {
        this.trainerType = trainerType;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public ArrayList<Program> getTrainerPrograms() {
        return trainerPrograms;
    }

    public void setTrainerPrograms(ArrayList<Program> trainerPrograms) {
        this.trainerPrograms = trainerPrograms;
    }

    public ArrayList<Circuit> getTrainerCircuits() {
        return trainerCircuits;
    }

    public void setTrainerCircuits(ArrayList<Circuit> trainerCircuits) {
        this.trainerCircuits = trainerCircuits;
    }

    public void setTrainerFromJSONAPI(JSONObject trainerData){
        MCrypt mcrypt = new MCrypt();
        try {
            setId(trainerData.getInt("id"));
            setFirstName(new String( mcrypt.decrypt(trainerData.getString("firstName"))));
            setLastName(new String( mcrypt.decrypt(trainerData.getString("lastName"))));
            setDesignation(new String( mcrypt.decrypt(trainerData.getString("designation"))));
            getLocationInfo().setLocationInfoFromJSONAPI(trainerData.getJSONObject("locationInfo"));
            setImageThumbnail(new String( mcrypt.decrypt(trainerData.getString("imageThumbnail"))));
            setAge(Integer.parseInt(new String( mcrypt.decrypt(trainerData.getString("age")))));
            setContact(new String( mcrypt.decrypt(trainerData.getString("contact"))));
            setAccess_token(trainerData.getString("access_token"));
            settrainerType(new String( mcrypt.decrypt(trainerData.getString("trainerType"))));



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void generateProgramsFromJSON( JSONArray programsData){
        trainerPrograms.clear();
        ArrayList<Program> fetchedPrograms = new ArrayList<>();
        int UNPARSED_PRORAMS_LENGHT = programsData.length();
        for(int i = 0; i < UNPARSED_PRORAMS_LENGHT; i++) {
            Program program = new Program();
            try {
                JSONObject unparsedProgram = programsData.getJSONObject(i);
                program.setProgramDataFromJSONAPI(unparsedProgram);


                fetchedPrograms.add(program);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //setTrainerPrograms(getLocalPrograms());
        this.trainerPrograms.addAll(fetchedPrograms);
    }

}
