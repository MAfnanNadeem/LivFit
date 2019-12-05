package life.mibo.hardware.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SessionReport {

    private int sessionID;
    private int trainerID;
    private int locationID;
    private String startDatetime;
    private String endDatetime;
    private int duration;
    private String programCircuitName;
    private int breaks;
    private String trainerIssuesLog;


    private MemberReport[] memberReport;

    public MemberReport[] getMemberReport() {
        return memberReport;
    }

    public void setMemberReport(MemberReport[] memberReport) {
        this.memberReport = memberReport;
    }
    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(int trainerID) {
        this.trainerID = trainerID;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    public String getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(String endDatetime) {
        this.endDatetime = endDatetime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getProgramCircuitName() {
        return programCircuitName;
    }

    public void setProgramCircuitName(String programCircuitName) {
        this.programCircuitName = programCircuitName;
    }

    public int getBreaks() {
        return breaks;
    }

    public void setBreaks(int breaks) {
        this.breaks = breaks;
    }

    public String getTrainerIssuesLog() {
        return trainerIssuesLog;
    }

    public void setTrainerIssuesLog(String trainerIssuesLog) {
        this.trainerIssuesLog = trainerIssuesLog;
    }


    public JSONObject toJSON() {
        JSONObject data = new JSONObject();
        try {
            data.accumulate("SessionID", sessionID);
            data.accumulate("TrainerID", trainerID);
            data.accumulate("LocationID", locationID);
            data.accumulate("startDatetime", startDatetime);
            data.accumulate("endDatetime", endDatetime);
            data.accumulate("duration", duration);
            data.accumulate("programCircuitName", programCircuitName);
            data.accumulate("breaks", breaks);
            data.accumulate("trainerIssuesLog", trainerIssuesLog);
            JSONArray auxMemberReport = new JSONArray();
            for(int i = 0; i < memberReport.length ; i++){
                auxMemberReport.put(memberReport[i].toJSON());
            }
            data.accumulate("members", auxMemberReport);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }


}
