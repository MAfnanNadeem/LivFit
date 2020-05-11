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
        var bMI: Double?,
        @SerializedName("BMR")
        var bMR: Double?,
        @SerializedName("BSA")
        var bSA: Double?,
        @SerializedName("BodyFat")
        var bodyFat: Double?,
        @SerializedName("BodyUnit")
        var bodyUnit: String?,
        @SerializedName("BodyWater")
        var bodyWater: Double?,
        @SerializedName("Chest")
        var chest: Double?,
        @SerializedName("Elbow")
        var elbow: Double?,
        @SerializedName("Energy")
        var energy: Double?,
        @SerializedName("FatFreeMassIndex")
        var fatFreeMassIndex: Double?,
        @SerializedName("Forearm")
        var forearm: Double?,
        @SerializedName("Height")
        var height: Double?,
        @SerializedName("HeightUnit")
        var heightUnit: String?,
        @SerializedName("HighHips")
        var highHips: Double?,
        @SerializedName("Hips")
        var hips: Double?,
        @SerializedName("Waist")
        var waist: Double?,
        @SerializedName("Wrist")
        var wrist: Double?,
        @SerializedName("Weight")
        var weight: Double?,
        @SerializedName("WaistHeightRatio")
        var waistHeightRatio: Double?,
        @SerializedName("WaistHipRatio")
        var waistHipRatio: Double?,
        @SerializedName("WeightLoss")
        var weightLoss: Double?,
        @SerializedName("WeightUnit")
        var weightUnit: String?,
        @SerializedName("IBW")
        var iBW: Double?,
        @SerializedName("LeanBodyMass")
        var leanBodyMass: Double?,
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