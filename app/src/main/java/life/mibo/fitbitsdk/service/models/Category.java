package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class Category {

    @Expose
    private List<ActivityCategory> activities;
    @Expose
    private Long id;
    @Expose
    private String name;

    public List<ActivityCategory> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityCategory> activities) {
        this.activities = activities;
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
