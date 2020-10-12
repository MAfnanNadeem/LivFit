package life.mibo.fitbitsdk.service.models;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class Features {

    @Expose
    private Boolean exerciseGoal;
    @Expose
    private PhoneNumberFriendFinding phoneNumberFriendFinding;

    public Boolean getExerciseGoal() {
        return exerciseGoal;
    }

    public void setExerciseGoal(Boolean exerciseGoal) {
        this.exerciseGoal = exerciseGoal;
    }

    public PhoneNumberFriendFinding getPhoneNumberFriendFinding() {
        return phoneNumberFriendFinding;
    }

    public void setPhoneNumberFriendFinding(PhoneNumberFriendFinding phoneNumberFriendFinding) {
        this.phoneNumberFriendFinding = phoneNumberFriendFinding;
    }

}
