package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ActivitySteps {
    @SerializedName("activities-steps")
    @Expose
    private List<Steps> steps = new ArrayList<>();


    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }
}
