package life.mibo.hexa.models.member


import com.google.gson.annotations.SerializedName

data class TrainingGoals(
    @SerializedName("otherGoal")
    var otherGoal: Any?,
    @SerializedName("otherGoalDesc")
    var otherGoalDesc: Any?,
    @SerializedName("performanceEnhancement")
    var performanceEnhancement: Any?,
    @SerializedName("staminaBuilding")
    var staminaBuilding: Any?,
    @SerializedName("toning")
    var toning: Any?,
    @SerializedName("weightloss")
    var weightloss: Any?
)