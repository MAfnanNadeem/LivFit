package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class ManualValuesSpecified {

    @Expose
    private Boolean calories;
    @Expose
    private Boolean distance;
    @Expose
    private Boolean steps;

    public Boolean getCalories() {
        return calories;
    }

    public void setCalories(Boolean calories) {
        this.calories = calories;
    }

    public Boolean getDistance() {
        return distance;
    }

    public void setDistance(Boolean distance) {
        this.distance = distance;
    }

    public Boolean getSteps() {
        return steps;
    }

    public void setSteps(Boolean steps) {
        this.steps = steps;
    }

}
