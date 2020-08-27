/*
 *  Created by Sumeet Kumar on 5/5/20 8:58 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/5/20 8:58 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.biometric


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class PostBiometric(data: Data?, token: String?) :
    BasePost<PostBiometric.Data?>(data, "SaveMemberBiometrics", token) {
    data class Data(
        @SerializedName("BMI")
        var bMI: String?,
        @SerializedName("BMR")
        var bMR: String?,
        @SerializedName("BSA")
        var bSA: String?,
        @SerializedName("BodyFat")
        var bodyFat: String?,
        @SerializedName("BodyUnit")
        var bodyUnit: String?,
        @SerializedName("BodyWater")
        var bodyWater: String?,
        @SerializedName("Chest")
        var chest: String?,
        @SerializedName("Elbow")
        var elbow: String?,
        @SerializedName("Energy")
        var energy: String?,
        @SerializedName("FatFreeMassIndex")
        var fatFreeMassIndex: String?,
        @SerializedName("Forearm")
        var forearm: String?,
        @SerializedName("Height")
        var height: String?,
        @SerializedName("HeightUnit")
        var heightUnit: String?,
        @SerializedName("HighHips")
        var highHips: String?,
        @SerializedName("Hips")
        var hips: String?,
        @SerializedName("Waist")
        var waist: String?,
        @SerializedName("Wrist")
        var wrist: String?,
        @SerializedName("Weight")
        var weight: String?,
        @SerializedName("WaistHeightRatio")
        var waistHeightRatio: String?,
        @SerializedName("WaistHipRatio")
        var waistHipRatio: String?,
        @SerializedName("WeightLoss")
        var weightLoss: String?,
        @SerializedName("WeightUnit")
        var weightUnit: String?,
        @SerializedName("IBW")
        var iBW: String?,
        @SerializedName("LeanBodyMass")
        var leanBodyMass: String?,
        @SerializedName("ActivityType")
        var activityType: String?,
        @SerializedName("GoalType")
        var goalType: String?,
        @SerializedName("ShapeType")
        var shapeType: String?,
        @SerializedName("MemberID")
        var memberID: String?
    )
}