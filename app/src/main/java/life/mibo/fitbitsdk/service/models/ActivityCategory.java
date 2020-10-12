package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class ActivityCategory {

    @Expose
    private String accessLevel;
    @Expose
    private List<ActivityLevel> activityLevels;
    @Expose
    private Boolean hasSpeed;
    @Expose
    private Long id;
    @Expose
    private String name;

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<ActivityLevel> getActivityLevels() {
        return activityLevels;
    }

    public void setActivityLevels(List<ActivityLevel> activityLevels) {
        this.activityLevels = activityLevels;
    }

    public Boolean getHasSpeed() {
        return hasSpeed;
    }

    public void setHasSpeed(Boolean hasSpeed) {
        this.hasSpeed = hasSpeed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
