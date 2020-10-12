package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class User {

    @Expose
    private Long age;
    @Expose
    private Boolean ambassador;
    @Expose
    private Boolean autoStrideEnabled;
    @Expose
    private String avatar;
    @Expose
    private String avatar150;
    @Expose
    private String avatar640;
    @Expose
    private Long averageDailySteps;
    @Expose
    private Boolean challengesBeta;
    @Expose
    private String clockTimeDisplayFormat;
    @Expose
    private Boolean corporate;
    @Expose
    private Boolean corporateAdmin;
    @Expose
    private String dateOfBirth;
    @Expose
    private String displayName;
    @Expose
    private String displayNameSetting;
    @Expose
    private String distanceUnit;
    @Expose
    private String encodedId;
    @Expose
    private Features features;
    @Expose
    private String firstName;
    @Expose
    private String fullName;
    @Expose
    private String gender;
    @Expose
    private String glucoseUnit;
    @Expose
    private Double height;
    @Expose
    private String heightUnit;
    @Expose
    private Boolean isChild;
    @Expose
    private String languageLocale;
    @Expose
    private String lastName;
    @Expose
    private String locale;
    @Expose
    private String memberSince;
    @Expose
    private Boolean mfaEnabled;
    @Expose
    private Long offsetFromUTCMillis;
    @Expose
    private String sleepTracking;
    @Expose
    private String startDayOfWeek;
    @Expose
    private Double strideLengthRunning;
    @Expose
    private String strideLengthRunningType;
    @Expose
    private Double strideLengthWalking;
    @Expose
    private String strideLengthWalkingType;
    @Expose
    private String swimUnit;
    @Expose
    private String timezone;
    @Expose
    private List<TopBadge> topBadges;
    @Expose
    private Double weight;
    @Expose
    private String weightUnit;

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Boolean getAmbassador() {
        return ambassador;
    }

    public void setAmbassador(Boolean ambassador) {
        this.ambassador = ambassador;
    }

    public Boolean getAutoStrideEnabled() {
        return autoStrideEnabled;
    }

    public void setAutoStrideEnabled(Boolean autoStrideEnabled) {
        this.autoStrideEnabled = autoStrideEnabled;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar150() {
        return avatar150;
    }

    public void setAvatar150(String avatar150) {
        this.avatar150 = avatar150;
    }

    public String getAvatar640() {
        return avatar640;
    }

    public void setAvatar640(String avatar640) {
        this.avatar640 = avatar640;
    }

    public Long getAverageDailySteps() {
        return averageDailySteps;
    }

    public void setAverageDailySteps(Long averageDailySteps) {
        this.averageDailySteps = averageDailySteps;
    }

    public Boolean getChallengesBeta() {
        return challengesBeta;
    }

    public void setChallengesBeta(Boolean challengesBeta) {
        this.challengesBeta = challengesBeta;
    }

    public String getClockTimeDisplayFormat() {
        return clockTimeDisplayFormat;
    }

    public void setClockTimeDisplayFormat(String clockTimeDisplayFormat) {
        this.clockTimeDisplayFormat = clockTimeDisplayFormat;
    }

    public Boolean getCorporate() {
        return corporate;
    }

    public void setCorporate(Boolean corporate) {
        this.corporate = corporate;
    }

    public Boolean getCorporateAdmin() {
        return corporateAdmin;
    }

    public void setCorporateAdmin(Boolean corporateAdmin) {
        this.corporateAdmin = corporateAdmin;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayNameSetting() {
        return displayNameSetting;
    }

    public void setDisplayNameSetting(String displayNameSetting) {
        this.displayNameSetting = displayNameSetting;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getEncodedId() {
        return encodedId;
    }

    public void setEncodedId(String encodedId) {
        this.encodedId = encodedId;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGlucoseUnit() {
        return glucoseUnit;
    }

    public void setGlucoseUnit(String glucoseUnit) {
        this.glucoseUnit = glucoseUnit;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public Boolean getIsChild() {
        return isChild;
    }

    public void setIsChild(Boolean isChild) {
        this.isChild = isChild;
    }

    public String getLanguageLocale() {
        return languageLocale;
    }

    public void setLanguageLocale(String languageLocale) {
        this.languageLocale = languageLocale;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }

    public Boolean getMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(Boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public Long getOffsetFromUTCMillis() {
        return offsetFromUTCMillis;
    }

    public void setOffsetFromUTCMillis(Long offsetFromUTCMillis) {
        this.offsetFromUTCMillis = offsetFromUTCMillis;
    }

    public String getSleepTracking() {
        return sleepTracking;
    }

    public void setSleepTracking(String sleepTracking) {
        this.sleepTracking = sleepTracking;
    }

    public String getStartDayOfWeek() {
        return startDayOfWeek;
    }

    public void setStartDayOfWeek(String startDayOfWeek) {
        this.startDayOfWeek = startDayOfWeek;
    }

    public Double getStrideLengthRunning() {
        return strideLengthRunning;
    }

    public void setStrideLengthRunning(Double strideLengthRunning) {
        this.strideLengthRunning = strideLengthRunning;
    }

    public String getStrideLengthRunningType() {
        return strideLengthRunningType;
    }

    public void setStrideLengthRunningType(String strideLengthRunningType) {
        this.strideLengthRunningType = strideLengthRunningType;
    }

    public Double getStrideLengthWalking() {
        return strideLengthWalking;
    }

    public void setStrideLengthWalking(Double strideLengthWalking) {
        this.strideLengthWalking = strideLengthWalking;
    }

    public String getStrideLengthWalkingType() {
        return strideLengthWalkingType;
    }

    public void setStrideLengthWalkingType(String strideLengthWalkingType) {
        this.strideLengthWalkingType = strideLengthWalkingType;
    }

    public String getSwimUnit() {
        return swimUnit;
    }

    public void setSwimUnit(String swimUnit) {
        this.swimUnit = swimUnit;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<TopBadge> getTopBadges() {
        return topBadges;
    }

    public void setTopBadges(List<TopBadge> topBadges) {
        this.topBadges = topBadges;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

}
