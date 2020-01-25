package life.mibo.hardware.models;

/**
 * Created by Fer on 18/03/2019.
 */

public class TrainingGoals implements BaseModel {
    private String weightloss;
    private String toning;
    private String performanceEnhancement;
    private String staminaBuilding;
    private String otherGoal;
    private String otherGoalDesc;

    public void setWeightloss(String value){
        weightloss = value;
    }
    public void setToning(String value){
        toning = value;
    }
    public void setPerformanceEnhancement(String value){
        performanceEnhancement = value;
    }
    public void setStaminaBuilding(String value){
        staminaBuilding = value;
    }
    public void setOtherGoal(String value){
        otherGoal = value;
    }
    public void setOtherGoalDesc(String value){
        otherGoalDesc = value;
    }

   public String getWeightloss(){
       return weightloss;
   }
   public String getToning(){
       return toning;
   }
   public String getPerformanceEnhancement(){
       return performanceEnhancement;
   }
   public String getStaminaBuilding(){
       return staminaBuilding;
   }
   public String getOtherGoal(){
       return otherGoal;
   }
   public String getOtherGoalDesc(){
       return otherGoalDesc;
   }
}
