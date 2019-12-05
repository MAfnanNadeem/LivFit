package life.mibo.hardware.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import life.mibo.hardware.SessionManager;

public class MemberReport {
    int memberId = 0;
    int userRating = 0;
    String trainerFeedback = "";
    int[] channelValues = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int restingHR = 0;
    int peakHR = 0;
    int[] variableHR;
    int caloriesBurnt = 0;//: 1400,
    int sessionCount = 0;//: 1
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getTrainerFeedback() {
        return trainerFeedback;
    }

    public void setTrainerFeedback(String trainerFeedback) {
        this.trainerFeedback = trainerFeedback;
    }

    public int[] getChannelValues() {
        return channelValues;
    }

    public void setChannelValues(int[] channelValues) {
        this.channelValues = channelValues;
    }

    public int getRestingHR() {
        return restingHR;
    }

    public void setRestingHR(int restingHR) {
        this.restingHR = restingHR;
    }

    public int getPeakHR() {
        return peakHR;
    }

    public void setPeakHR(int peakHR) {
        this.peakHR = peakHR;
    }

    public int[] getVariableHR() {
        return variableHR;
    }

    public void setVariableHR(int[] variableHR) {
        this.variableHR = variableHR;
    }

    public int getCaloriesBurnt() {
        return caloriesBurnt;
    }

    public void setCaloriesBurnt(int caloriesBurnt) {
        this.caloriesBurnt = caloriesBurnt;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }

    public void SetMemberReport(User user) {
        setMemberId(Integer.parseInt(user.getId()));
        setUserRating(0);
        setTrainerFeedback(user.getUserSessionNotes());
        setChannelValues(user.getCurrentChannelLevels());
        setRestingHR(user.getRestHeartRate());
        setPeakHR(user.getPeakHeartRate());
        setVariableHR(user.getAcumulatedHeartRate());
        setCaloriesBurnt( (int) ((double) Integer.parseInt(user.getMedicalHistory().getWeight()) * (double) 60 *
                ((double) SessionManager.getInstance().getSession().getCurrentSessionProgram().getBorgRating()
                )*
                3.5 * ((double) user.getUserSessionTimer() / (double) 60 / (double) 60) / (double) 200));
        setSessionCount(user.getCompletedSessions());
    }


    public JSONObject toJSON() {
        JSONObject data = new JSONObject();
            try {
                data.accumulate("memberId", memberId);
                data.accumulate("userRating", userRating);
                data.accumulate("trainerFeedback", trainerFeedback);
                data.accumulate("channelValues", new JSONArray(channelValues));
                data.accumulate("restingHR", restingHR);
                data.accumulate("peakHR", peakHR);
                data.accumulate("variableHR", new JSONArray(variableHR));
                data.accumulate("caloriesBurnt", caloriesBurnt);
                data.accumulate("sessionCount", sessionCount);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
    }
}
