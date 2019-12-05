package life.mibo.hardware.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Gym {
    private int gymID;
    private String name;
    private String registrationCode;
    private int type;
    private String ownerName;
    private String contact;
    private String email;
    private String address1;
    private String address2;
    private String zip;
    private String city;
    private String country;
    private String province;
    private String latitude;
    private String longitude;
    private String contractStartDate;
    private String contractEndDate;
    private int fxPartner;
    private String fxContractNumber;
    private String fxSolution;
    private String fxAttachment;
    private String primaryContactName;
    private String primaryContactDesignation;
    private String primaryContact;
    private String primaryContactEmail;
    private String secondaryContactName;
    private String secondaryContactDesignation;
    private String secondaryContact;
    private String secondaryContactEmail;
    private int workfit;
    private int privfit;
    private int livfit;
    private int activstyle;
    private int performa;
    private int reforma;
    private int academyCenter;

    public Gym(int gymID, String name, String registrationCode, int type, String ownerName,
               String contact, String email, String address1, String address2, String zip,
               String city, String country, String province, String latitude, String longitude,
               String contractStartDate, String contractEndDate, int fxPartner,
               String fxContractNumber, String fxSolution, String fxAttachment,
               String primaryContactName, String primaryContactDesignation, String primaryContact,
               String primaryContactEmail, String secondaryContactName,
               String secondaryContactDesignation, String secondaryContact,
               String secondaryContactEmail, int workfit, int privfit, int livfit,
               int activstyle, int performa, int reforma, int academyCenter) {
        this.gymID = gymID;
        this.name = name;
        this.registrationCode = registrationCode;
        this.type = type;
        this.ownerName = ownerName;
        this.contact = contact;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.fxPartner = fxPartner;
        this.fxContractNumber = fxContractNumber;
        this.fxSolution = fxSolution;
        this.fxAttachment = fxAttachment;
        this.primaryContactName = primaryContactName;
        this.primaryContactDesignation = primaryContactDesignation;
        this.primaryContact = primaryContact;
        this.primaryContactEmail = primaryContactEmail;
        this.secondaryContactName = secondaryContactName;
        this.secondaryContactDesignation = secondaryContactDesignation;
        this.secondaryContact = secondaryContact;
        this.secondaryContactEmail = secondaryContactEmail;
        this.workfit = workfit;
        this.privfit = privfit;
        this.livfit = livfit;
        this.activstyle = activstyle;
        this.performa = performa;
        this.reforma = reforma;
        this.academyCenter = academyCenter;
    }

    public JSONObject toJSON() {
        JSONObject gym = new JSONObject();

        try {
            gym.accumulate("gymID", getGymID());
            gym.accumulate("name", getName());
            gym.accumulate("registrationCode", getRegistrationCode());
            gym.accumulate("type", getType());
            gym.accumulate("ownerName", getOwnerName());
            gym.accumulate("contact", getContact());
            gym.accumulate("email", getEmail());
            gym.accumulate("address1", getAddress1());
            gym.accumulate("address2", getAddress2());
            gym.accumulate("zip", getZip());
            gym.accumulate("city", getCity());
            gym.accumulate("country", getCountry());
            gym.accumulate("province", getProvince());
            gym.accumulate("latitude", getLatitude());
            gym.accumulate("longitude", getLongitude());
            gym.accumulate("contractStartDate", getContractStartDate());
            gym.accumulate("contractEndDate", getContractEndDate());
            gym.accumulate("fxPartner", getFxPartner());
            gym.accumulate("fxContractNumber", getFxContractNumber());
            gym.accumulate("fxSolution", getFxSolution());
            gym.accumulate("fxAttachment", getFxAttachment());
            gym.accumulate("primaryContactName", getPrimaryContactName());
            gym.accumulate("primaryContactDesignation", getPrimaryContactDesignation());
            gym.accumulate("primaryContact", getPrimaryContact());
            gym.accumulate("primaryContactEmail", getPrimaryContactEmail());
            gym.accumulate("secondaryContactName", getSecondaryContactName());
            gym.accumulate("secondaryContactDesignation", getSecondaryContactDesignation());
            gym.accumulate("secondaryContact", getSecondaryContact());
            gym.accumulate("secondaryContactEmail", getSecondaryContactEmail());
            gym.accumulate("workfit", getWorkfit());
            gym.accumulate("privfit", getPrivfit());
            gym.accumulate("livfit", getLivfit());
            gym.accumulate("activstyle", getActivstyle());
            gym.accumulate("performa", getPerforma());
            gym.accumulate("reforma", getReforma());
            gym.accumulate("academyCenter", getAcademyCenter());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return gym;
    }

    public int getGymID() {
        return gymID;
    }

    public void setGymID(int gymID) {
        this.gymID = gymID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(String contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public String getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(String contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public int getFxPartner() {
        return fxPartner;
    }

    public void setFxPartner(int fxPartner) {
        this.fxPartner = fxPartner;
    }

    public String getFxContractNumber() {
        return fxContractNumber;
    }

    public void setFxContractNumber(String fxContractNumber) {
        this.fxContractNumber = fxContractNumber;
    }

    public String getFxSolution() {
        return fxSolution;
    }

    public void setFxSolution(String fxSolution) {
        this.fxSolution = fxSolution;
    }

    public String getFxAttachment() {
        return fxAttachment;
    }

    public void setFxAttachment(String fxAttachment) {
        this.fxAttachment = fxAttachment;
    }

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    public String getPrimaryContactDesignation() {
        return primaryContactDesignation;
    }

    public void setPrimaryContactDesignation(String primaryContactDesignation) {
        this.primaryContactDesignation = primaryContactDesignation;
    }

    public String getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

    public String getSecondaryContactName() {
        return secondaryContactName;
    }

    public void setSecondaryContactName(String secondaryContactName) {
        this.secondaryContactName = secondaryContactName;
    }

    public String getSecondaryContactDesignation() {
        return secondaryContactDesignation;
    }

    public void setSecondaryContactDesignation(String secondaryContactDesignation) {
        this.secondaryContactDesignation = secondaryContactDesignation;
    }

    public String getSecondaryContact() {
        return secondaryContact;
    }

    public void setSecondaryContact(String secondaryContact) {
        this.secondaryContact = secondaryContact;
    }

    public String getSecondaryContactEmail() {
        return secondaryContactEmail;
    }

    public void setSecondaryContactEmail(String secondaryContactEmail) {
        this.secondaryContactEmail = secondaryContactEmail;
    }

    public int getWorkfit() {
        return workfit;
    }

    public void setWorkfit(int workfit) {
        this.workfit = workfit;
    }

    public int getPrivfit() {
        return privfit;
    }

    public void setPrivfit(int privfit) {
        this.privfit = privfit;
    }

    public int getLivfit() {
        return livfit;
    }

    public void setLivfit(int livfit) {
        this.livfit = livfit;
    }

    public int getActivstyle() {
        return activstyle;
    }

    public void setActivstyle(int activstyle) {
        this.activstyle = activstyle;
    }

    public int getPerforma() {
        return performa;
    }

    public void setPerforma(int performa) {
        this.performa = performa;
    }

    public int getReforma() {
        return reforma;
    }

    public void setReforma(int reforma) {
        this.reforma = reforma;
    }

    public int getAcademyCenter() {
        return academyCenter;
    }

    public void setAcademyCenter(int academyCenter) {
        this.academyCenter = academyCenter;
    }
}
