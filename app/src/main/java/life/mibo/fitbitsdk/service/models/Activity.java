package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class Activity {

    @Expose
    private Long activeDuration;
    @Expose
    private List<ActivityLevel> activityLevel;
    @Expose
    private String activityName;
    @Expose
    private Long activityTypeId;
    @Expose
    private Long calories;
    @Expose
    private String caloriesLink;
    @Expose
    private Double distance;
    @Expose
    private String distanceUnit;
    @Expose
    private Long duration;
    @Expose
    private Long elevationGain;
    @Expose
    private String lastModified;
    @Expose
    private Long logId;
    @Expose
    private String logType;
    @Expose
    private ManualValuesSpecified manualValuesSpecified;
    @Expose
    private Long originalDuration;
    @Expose
    private String originalStartTime;
    @Expose
    private Double pace;
    @Expose
    private Double speed;
    @Expose
    private String startTime;
    @Expose
    private Long steps;

    public Long getActiveDuration() {
        return activeDuration;
    }

    public void setActiveDuration(Long activeDuration) {
        this.activeDuration = activeDuration;
    }

    public List<ActivityLevel> getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(List<ActivityLevel> activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Long getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Long activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public Long getCalories() {
        return calories;
    }

    public void setCalories(Long calories) {
        this.calories = calories;
    }

    public String getCaloriesLink() {
        return caloriesLink;
    }

    public void setCaloriesLink(String caloriesLink) {
        this.caloriesLink = caloriesLink;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(Long elevationGain) {
        this.elevationGain = elevationGain;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public ManualValuesSpecified getManualValuesSpecified() {
        return manualValuesSpecified;
    }

    public void setManualValuesSpecified(ManualValuesSpecified manualValuesSpecified) {
        this.manualValuesSpecified = manualValuesSpecified;
    }

    public Long getOriginalDuration() {
        return originalDuration;
    }

    public void setOriginalDuration(Long originalDuration) {
        this.originalDuration = originalDuration;
    }

    public String getOriginalStartTime() {
        return originalStartTime;
    }

    public void setOriginalStartTime(String originalStartTime) {
        this.originalStartTime = originalStartTime;
    }

    public Double getPace() {
        return pace;
    }

    public void setPace(Double pace) {
        this.pace = pace;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Long getSteps() {
        return steps;
    }

    public void setSteps(Long steps) {
        this.steps = steps;
    }

}
