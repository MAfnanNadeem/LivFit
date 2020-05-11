/*
 *  Created by Sumeet Kumar on 5/6/20 2:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/6/20 2:59 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.biometric


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseModel
import life.mibo.android.models.base.BaseResponse

class Biometric(data: List<Data?>?) : BaseResponse<List<Biometric.Data?>?>(data) {

    data class Data(
        @SerializedName("ActivityType")
        var activityType: String?,
        @SerializedName("BMI")
        var bMI: Double?,
        @SerializedName("BMR")
        var bMR: Double?,
        @SerializedName("BSA")
        var bSA: String?,
        @SerializedName("BodyFat")
        var bodyFat: String?,
        @SerializedName("BodyUnit")
        var bodyUnit: String?,
        @SerializedName("BodyWater")
        var bodyWater: String?,
        @SerializedName("Chest")
        var chest: Double?,
        @SerializedName("CreatedAt")
        var createdAt: CreatedAt?,
        @SerializedName("Elbow")
        var elbow: Double?,
        @SerializedName("Energy")
        var energy: String?,
        @SerializedName("FatFreeWeight")
        var fatFreeWeight: String?,
        @SerializedName("Forearm")
        var forearm: Double?,
        @SerializedName("GoalType")
        var goalType: String?,
        @SerializedName("Height")
        var height: Double?,
        @SerializedName("HeightUnit")
        var heightUnit: String?,
        @SerializedName("HighHips")
        var highHips: Double?,
        @SerializedName("Hips")
        var hips: Double?,
        @SerializedName("IBW")
        var iBW: String?,
        @SerializedName("LeanBodyMass")
        var leanBodyMass: String?,
        @SerializedName("MemberID")
        var memberID: Double?,
        @SerializedName("ShapeType")
        var shapeType: String?,
        @SerializedName("Waist")
        var waist: Double?,
        @SerializedName("WaistHeightRatio")
        var waistHeightRatio: String?,
        @SerializedName("WaistHipRatio")
        var waistHipRatio: String?,
        @SerializedName("Weight")
        var weight: Double?,
        @SerializedName("WeightLoss")
        var weightLoss: String?,
        @SerializedName("WeightUnit")
        var weightUnit: String?,
        @SerializedName("Wrist")
        var wrist: Double?
    ) : BaseModel

    data class CreatedAt(
        @SerializedName("date")
        var date: String?,
        @SerializedName("timezone")
        var timezone: String?,
        @SerializedName("timezone_type")
        var timezoneType: String?
    )

}