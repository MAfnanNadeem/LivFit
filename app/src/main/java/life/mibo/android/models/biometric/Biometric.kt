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
import life.mibo.hardware.encryption.MCrypt
import life.mibo.hardware.encryption.MCrypt2
import java.math.BigDecimal
import java.math.RoundingMode

class Biometric(data: List<Data?>?) : BaseResponse<List<Biometric.Data?>?>(data) {

    data class Data(
        @SerializedName("ActivityType")
        var activityType: String?,
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
        @SerializedName("CreatedAt")
        var createdAt: CreatedAt?,
        @SerializedName("Elbow")
        var elbow: String?,
        @SerializedName("Energy")
        var energy: String?,
        @SerializedName("FatFreeWeight")
        var fatFreeWeight: String?,
        @SerializedName("Forearm")
        var forearm: String?,
        @SerializedName("GoalType")
        var goalType: String?,
        @SerializedName("Height")
        var height: String?,
        @SerializedName("HeightUnit")
        var heightUnit: String?,
        @SerializedName("HighHips")
        var highHips: String?,
        @SerializedName("Hips")
        var hips: String?,
        @SerializedName("IBW")
        var iBW: String?,
        @SerializedName("LeanBodyMass")
        var leanBodyMass: String?,
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("ShapeType")
        var shapeType: String?,
        @SerializedName("Waist")
        var waist: String?,
        @SerializedName("WaistHeightRatio")
        var waistHeightRatio: String?,
        @SerializedName("WaistHipRatio")
        var waistHipRatio: String?,
        @SerializedName("Weight")
        var weight: String?,
        @SerializedName("WeightLoss")
        var weightLoss: String?,
        @SerializedName("WeightUnit")
        var weightUnit: String?,
        @SerializedName("Wrist")
        var wrist: String?
    ) : BaseModel
    {

    }

    class Decrypted() : BaseModel {

        companion object {
            fun from(data: Biometric.Data): Decrypted {
                val crypt = MCrypt2()
                val item = Decrypted()
                item.memberID = data.memberID
                item.activityType = getString(data.activityType, crypt)
                item.bMI = getDouble(data.bMI, crypt)
                item.bMR = getDouble(data.bMR, crypt)
                item.bSA = getString(data.bSA, crypt)
                item.bodyFat = getString(data.bodyFat, crypt)
                item.bodyUnit = getString(data.bodyUnit, crypt)
                item.bodyWater = getString(data.bodyWater, crypt)
                item.chest = getDouble(data.chest, crypt)
                item.createdAt = data.createdAt
                item.elbow = getDouble(data.elbow, crypt)
                item.energy = getString(data.energy, crypt)
                item.fatFreeWeight = getString(data.fatFreeWeight, crypt)
                item.forearm = getDouble(data.forearm, crypt)
                item.goalType = getString(data.goalType, crypt)
                item.height = getDouble(data.height, crypt)
                item.heightUnit = getString(data.heightUnit, crypt)
                item.highHips = getDouble(data.highHips, crypt)
                item.hips = getDouble(data.hips, crypt)
                item.iBW = getString(data.iBW, crypt)
                item.leanBodyMass = getString(data.leanBodyMass, crypt)
                item.shapeType = getString(data.shapeType, crypt)
                item.waist = getDouble(data.waist, crypt)
                item.waistHeightRatio = getString(data.waistHeightRatio, crypt)
                item.waistHipRatio = getString(data.waistHipRatio, crypt)
                item.weight = getDouble(data.weight, crypt)
                item.weightLoss = getString(data.weightLoss, crypt)
                item.weightUnit = getString(data.weightUnit, crypt)
                item.wrist = getDouble(data.wrist, crypt)
                return item
            }

            fun getDouble(value: String?, crypt: MCrypt): Double {
                if (value == null)
                    return 0.0
                return try {
                    BigDecimal(getString(value, crypt).toDouble()).setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                } catch (e: java.lang.Exception) {
                    0.0
                }
            }

            fun getString(value: String?, crypt: MCrypt): String {
                if (value == null)
                    return ""
                return try {
                    String(crypt.decrypt(value))
                } catch (e: java.lang.Exception) {
                    ""
                }
            }

            fun getString(value: String?, crypt: MCrypt2): String {
                //return crypt.decrypt(value) ?: ""
                return value ?: ""
            }

            fun getDouble(value: String?, crypt: MCrypt2): Double {
                if (value == null)
                    return 0.0
                return try {
                    BigDecimal(getString(value, crypt).toDouble()).setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                } catch (e: java.lang.Exception) {
                    0.0
                }
            }

        }

        var activityType: String? = null
        var bMI: Double? = 0.0
        var bMR: Double? = 0.0
        var bSA: String? = null
        var bodyFat: String? = null
        var bodyUnit: String? = null
        var bodyWater: String? = null
        var chest: Double? = 0.0
        var createdAt: CreatedAt? = null
        var elbow: Double? = 0.0
        var energy: String? = null
        var fatFreeWeight: String? = null
        var forearm: Double? = 0.0
        var goalType: String? = null
        var height: Double? = 0.0
        var heightUnit: String? = null
        var highHips: Double? = 0.0
        var hips: Double? = 0.0
        var iBW: String? = null
        var leanBodyMass: String? = null
        var memberID: String? = "0"
        var shapeType: String? = null
        var waist: Double? = 0.0
        var waistHeightRatio: String? = null
        var waistHipRatio: String? = null
        var weight: Double? = 0.0
        var weightLoss: String? = null
        var weightUnit: String? = null
        var wrist: Double? = 0.0
    }


}