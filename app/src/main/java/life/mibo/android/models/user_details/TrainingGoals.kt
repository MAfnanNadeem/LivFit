/*
 *  Created by Sumeet Kumar on 1/9/20 2:17 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.user_details


import com.google.gson.annotations.SerializedName

data class TrainingGoals(
    @SerializedName("otherGoal")
    var otherGoal: Any?,
    @SerializedName("otherGoalDesc")
    var otherGoalDesc: Any?,
    @SerializedName("performanceEnhancement")
    var performanceEnhancement: Int?,
    @SerializedName("staminaBuilding")
    var staminaBuilding: Any?,
    @SerializedName("toning")
    var toning: Any?,
    @SerializedName("weightloss")
    var weightloss: Int?
)