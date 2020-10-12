package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class Total {

    @Expose
    private Long activeScore;
    @Expose
    private Long caloriesOut;
    @Expose
    private Distance distance;
    @Expose
    private Floors floors;
    @Expose
    private Steps steps;

    public Long getActiveScore() {
        return activeScore;
    }

    public void setActiveScore(Long activeScore) {
        this.activeScore = activeScore;
    }

    public Long getCaloriesOut() {
        return caloriesOut;
    }

    public void setCaloriesOut(Long caloriesOut) {
        this.caloriesOut = caloriesOut;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Floors getFloors() {
        return floors;
    }

    public void setFloors(Floors floors) {
        this.floors = floors;
    }

    public Steps getSteps() {
        return steps;
    }

    public void setSteps(Steps steps) {
        this.steps = steps;
    }

}
