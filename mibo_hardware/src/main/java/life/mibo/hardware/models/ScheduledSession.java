package life.mibo.hardware.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import life.mibo.miboproapp.api.MCrypt;

public class ScheduledSession {


    private String sessionId = "";
    private String startDatetime = "";
    private String endDatetime = "";
    private String notes = "";
    private boolean startedSession = false;

    private boolean completedSession = false;

    private String groupName = "";

    ArrayList<User> signedUsers = new ArrayList<>();

    SimpleDateFormat formatDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getSessionId() {
        return sessionId;
    }

    public Long getSessionIdLong() {
        return Long.parseLong(sessionId);
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isStartedSession() {
        return startedSession;
    }

    public void setStartedSession(boolean startedSession) {
        this.startedSession = startedSession;
    }

    public boolean isCompletedSession() {
        return completedSession;
    }

    public void setCompletedSession(boolean completedSession) {
        this.completedSession = completedSession;
    }

    public String getStartDateString() {
        return startDatetime;
    }

    public Date getStartDate() {
        try {
            return formatDate.parse(startDatetime);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    public String getEndDateString() {
        return endDatetime;
    }

    public Date getEndDate() {
        try {
            return formatDate.parse(endDatetime);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public void setEndDatetime(String endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<User> getSignedUsers() {
        return signedUsers;
    }

    public ArrayList<User> getPresentUsersInSession() {
        ArrayList<User> usersInSession= new ArrayList<>();
        for (User u : signedUsers) {
            if(u.isPresent()) {
                usersInSession.add(u);
            }
        }

        return usersInSession;
    }

    public User getUserById(String Id){
        for(User u : signedUsers) {
            if(u.getId().equals(Id)){
                return u;
            }
        }
        return new User("x","x");
    }

    public void setSignedUsers(ArrayList<User> signedUsers) {
        this.signedUsers = signedUsers;
    }
    public void getScheduledSessionFromCalendarJSONAPI(JSONObject Session) {
            MCrypt mcrypt = new MCrypt();
            try {
                setSessionId(Session.getString("sessionId"));
                setStartDatetime(Session.getString("startDatetime"));
                setEndDatetime(Session.getString("endDatetime"));
                setNotes(Session.getString("notes"));
                setGroupName(Session.getString("session_group_name"));

                if(Session.getInt("started")==1) {
                    setStartedSession(true);
                }
                if(Session.getInt("completed")==1) {
                    setCompletedSession(true);
                }

                signedUsers = new ArrayList<>();
                int USERS_LENGHT = Session.getJSONArray("members").length();
                for(int i = 0; i < USERS_LENGHT; i++) {
                    User user = new User();

                    JSONObject unparsedUser = Session.getJSONArray("members").getJSONObject(i);
                    user.setId(unparsedUser.getString("id"));
                    user.setName(new String(mcrypt.decrypt(unparsedUser.getString("firstName"))));
                    user.setLastName(new String(mcrypt.decrypt(unparsedUser.getString("lastName"))));
                    user.setPresent(new String(mcrypt.decrypt(unparsedUser.getString("isPresent"))).equals("1"));
                    user.setImage(new String(mcrypt.decrypt(unparsedUser.getString("imageThumbnail"))));
                    user.setAge(Integer.parseInt(new String(mcrypt.decrypt(unparsedUser.getString("age")))));
                    user.setContact(new String(mcrypt.decrypt(unparsedUser.getString("contact"))));
                    user.setMissedSessions(Integer.parseInt(unparsedUser.getJSONObject("attendance").getString("missed")));
                    user.setRemainingSessions(Integer.parseInt(unparsedUser.getJSONObject("attendance").getString("remaining")));
                    user.setCompletedSessions(Integer.parseInt(unparsedUser.getJSONObject("attendance").getString("completed")));

                    int[] auxcurrentChannelLevels = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                    for (int c = 0; c < unparsedUser.getJSONArray("channelValues").length(); ++c) {
                        auxcurrentChannelLevels[c] = unparsedUser.getJSONArray("channelValues").optInt(c);
                    }
                    user.setCurrentChannelLevels(auxcurrentChannelLevels);

                    signedUsers.add(new User(user));

                    //add multiple times to simulate multiple users
//                    user.setId((Integer.parseInt(user.getId())+1)+"");
//                    String nameuax = user.getName();
//                    user.setName(nameuax+"2");
//                    signedUsers.add(new User(user));
//                    user.setId((Integer.parseInt(user.getId())+1)+"");
//                    user.setName(nameuax+"3");
//                    signedUsers.add(new User(user));
//                    user.setId((Integer.parseInt(user.getId())+1)+"");
//                    user.setName(nameuax+"4");
//                    signedUsers.add(new User(user));
//                    user.setId((Integer.parseInt(user.getId())+1)+"");
//                    user.setName(nameuax+"5");
//                    signedUsers.add(new User(user));
//                    user.setId((Integer.parseInt(user.getId())+1)+"");
//                    user.setName(nameuax+"6");
//                    signedUsers.add(new User(user));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
