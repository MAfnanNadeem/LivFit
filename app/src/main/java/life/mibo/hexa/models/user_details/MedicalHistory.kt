/*
 *  Created by Sumeet Kumar on 1/9/20 2:17 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.user_details


import com.google.gson.annotations.SerializedName

data class MedicalHistory(
    @SerializedName("alcohol")
    var alcohol: Any?,
    @SerializedName("allergies")
    var allergies: Any?,
    @SerializedName("allergiesDesc")
    var allergiesDesc: Any?,
    @SerializedName("anyOther")
    var anyOther: Any?,
    @SerializedName("anyOtherDesc")
    var anyOtherDesc: Any?,
    @SerializedName("asthma")
    var asthma: Any?,
    @SerializedName("asthmaDesc")
    var asthmaDesc: Any?,
    @SerializedName("blood_group")
    var bloodGroup: String?,
    @SerializedName("bloodPressure")
    var bloodPressure: Any?,
    @SerializedName("bloodPressureDesc")
    var bloodPressureDesc: Any?,
    @SerializedName("brokenBones")
    var brokenBones: Any?,
    @SerializedName("brokenBonesDesc")
    var brokenBonesDesc: Any?,
    @SerializedName("chestPains")
    var chestPains: Any?,
    @SerializedName("chestPainsDesc")
    var chestPainsDesc: Any?,
    @SerializedName("currentlyPregnant")
    var currentlyPregnant: Any?,
    @SerializedName("currentlyPregnantDesc")
    var currentlyPregnantDesc: Any?,
    @SerializedName("diabetes")
    var diabetes: Any?,
    @SerializedName("diabetesType")
    var diabetesType: Any?,
    @SerializedName("epilepsySeizures")
    var epilepsySeizures: Any?,
    @SerializedName("epilepsySeizuresDesc")
    var epilepsySeizuresDesc: Any?,
    @SerializedName("heartAttack")
    var heartAttack: Any?,
    @SerializedName("heartAttackDesc")
    var heartAttackDesc: Any?,
    @SerializedName("heartDisease")
    var heartDisease: Any?,
    @SerializedName("heartDiseaseDesc")
    var heartDiseaseDesc: Any?,
    @SerializedName("heartMurmur")
    var heartMurmur: Any?,
    @SerializedName("heartMurmurDesc")
    var heartMurmurDesc: Any?,
    @SerializedName("heartRateMonitors")
    var heartRateMonitors: Any?,
    @SerializedName("heartRateMonitorsDesc")
    var heartRateMonitorsDesc: Any?,
    @SerializedName("height")
    var height: String?,
    @SerializedName("height_unit")
    var heightUnit: String?,
    @SerializedName("medicalHistory")
    var medicalHistory: Any?,
    @SerializedName("mentalDisabilities")
    var mentalDisabilities: Any?,
    @SerializedName("mentalDisabilitiesDesc")
    var mentalDisabilitiesDesc: Any?,
    @SerializedName("muscleJointProblems")
    var muscleJointProblems: Any?,
    @SerializedName("muscleJointProblemsDesc")
    var muscleJointProblemsDesc: Any?,
    @SerializedName("oedema")
    var oedema: Any?,
    @SerializedName("oedemaDesc")
    var oedemaDesc: Any?,
    @SerializedName("palpitations")
    var palpitations: Any?,
    @SerializedName("palpitationsDesc")
    var palpitationsDesc: Any?,
    @SerializedName("physicalDisabilities")
    var physicalDisabilities: Any?,
    @SerializedName("physicalDisabilitiesDesc")
    var physicalDisabilitiesDesc: Any?,
    @SerializedName("pintPerWeek")
    var pintPerWeek: Any?,
    @SerializedName("pneumonia")
    var pneumonia: Any?,
    @SerializedName("pneumoniaDesc")
    var pneumoniaDesc: Any?,
    @SerializedName("recentChildbirth")
    var recentChildbirth: Any?,
    @SerializedName("recentChildbirthDesc")
    var recentChildbirthDesc: Any?,
    @SerializedName("recentSurgery")
    var recentSurgery: Any?,
    @SerializedName("recentSurgeryDesc")
    var recentSurgeryDesc: Any?,
    @SerializedName("shortnessOfBreath")
    var shortnessOfBreath: Any?,
    @SerializedName("shortnessOfBreathDesc")
    var shortnessOfBreathDesc: Any?,
    @SerializedName("smoker")
    var smoker: Any?,
    @SerializedName("tachycardia")
    var tachycardia: Any?,
    @SerializedName("tachycardiaDesc")
    var tachycardiaDesc: Any?,
    @SerializedName("ulcers")
    var ulcers: Any?,
    @SerializedName("ulcersDesc")
    var ulcersDesc: Any?,
    @SerializedName("weight")
    var weight: String?,
    @SerializedName("weight_unit")
    var weightUnit: String?
)