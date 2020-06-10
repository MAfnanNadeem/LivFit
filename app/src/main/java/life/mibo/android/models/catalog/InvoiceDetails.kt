/*
 *  Created by Sumeet Kumar on 6/4/20 10:57 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/4/20 10:57 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class InvoiceDetails(data: Data?) : BaseResponse<InvoiceDetails.Data?>(data) {

    data class Data(
        @SerializedName("Invoice")
        var invoice: Invoice?
    )

    data class Invoice(
        @SerializedName("booking_advice_date")
        var bookingAdviceDate: String?,
        @SerializedName("booking_advice_no")
        var bookingAdviceNo: String?,
        @SerializedName("conFee")
        var conFee: Double?,
        @SerializedName("packages")
        var packages: List<Package?>?,
        @SerializedName("payment_mode")
        var paymentMode: String?,
        @SerializedName("total_amount")
        var totalAmount: Double?,
        @SerializedName("user")
        var user: User?,
        @SerializedName("vat_gst_number")
        var vatGstNumber: String?
    )

    data class Package(
        @SerializedName("currency")
        var currency: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("price")
        var price: Double?,
        @SerializedName("vat")
        var vat: Double?
    )

    data class User(
        @SerializedName("active")
        var active: Int?,
        @SerializedName("address1")
        var address1: String?,
        @SerializedName("address2")
        var address2: String?,
        @SerializedName("age")
        var age: String?,
        @SerializedName("alcohol")
        var alcohol: Any?,
        @SerializedName("allergies")
        var allergies: Any?,
        @SerializedName("allergies_desc")
        var allergiesDesc: Any?,
        @SerializedName("any_other")
        var anyOther: Any?,
        @SerializedName("any_other_desc")
        var anyOtherDesc: Any?,
        @SerializedName("area_code")
        var areaCode: String?,
        @SerializedName("assigned")
        var assigned: Int?,
        @SerializedName("asthma")
        var asthma: Any?,
        @SerializedName("asthma_desc")
        var asthmaDesc: Any?,
        @SerializedName("avatar")
        var avatar: String?,
        @SerializedName("avatar_base64")
        var avatarBase64: String?,
        @SerializedName("blood_group")
        var bloodGroup: String?,
        @SerializedName("blood_pressure")
        var bloodPressure: Any?,
        @SerializedName("blood_pressure_desc")
        var bloodPressureDesc: Any?,
        @SerializedName("broken_bones")
        var brokenBones: Any?,
        @SerializedName("broken_bones_desc")
        var brokenBonesDesc: Any?,
        @SerializedName("chest_pains")
        var chestPains: Any?,
        @SerializedName("chest_pains_desc")
        var chestPainsDesc: Any?,
        @SerializedName("city")
        var city: String?,
        @SerializedName("country")
        var country: String?,
        @SerializedName("country_code")
        var countryCode: String?,
        @SerializedName("coupon_code")
        var couponCode: String?,
        @SerializedName("created_at")
        var createdAt: String?,
        @SerializedName("currently_pregnant")
        var currentlyPregnant: Any?,
        @SerializedName("currently_pregnant_desc")
        var currentlyPregnantDesc: Any?,
        @SerializedName("deleted")
        var deleted: Int?,
        @SerializedName("diabetes")
        var diabetes: Any?,
        @SerializedName("diabetes_type")
        var diabetesType: Any?,
        @SerializedName("dob")
        var dob: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("epilepsy_seizures")
        var epilepsySeizures: Any?,
        @SerializedName("epilepsy_seizures_desc")
        var epilepsySeizuresDesc: Any?,
        @SerializedName("facebook_key")
        var facebookKey: Any?,
        @SerializedName("first_login")
        var firstLogin: Int?,
        @SerializedName("firstname")
        var firstname: String?,
        @SerializedName("gender")
        var gender: String?,
        @SerializedName("google_key")
        var googleKey: Any?,
        @SerializedName("heart_attack")
        var heartAttack: Any?,
        @SerializedName("heart_attack_desc")
        var heartAttackDesc: Any?,
        @SerializedName("heart_disease")
        var heartDisease: Any?,
        @SerializedName("heart_disease_desc")
        var heartDiseaseDesc: Any?,
        @SerializedName("heart_murmur")
        var heartMurmur: Any?,
        @SerializedName("heart_murmur_desc")
        var heartMurmurDesc: Any?,
        @SerializedName("heart_rate_monitors")
        var heartRateMonitors: Any?,
        @SerializedName("heart_rate_monitors_desc")
        var heartRateMonitorsDesc: Any?,
        @SerializedName("height")
        var height: String?,
        @SerializedName("height_unit")
        var heightUnit: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("identification_number")
        var identificationNumber: Any?,
        @SerializedName("lastname")
        var lastname: String?,
        @SerializedName("latitude")
        var latitude: String?,
        @SerializedName("longitude")
        var longitude: String?,
        @SerializedName("medical_history")
        var medicalHistory: Any?,
        @SerializedName("membership_end_date")
        var membershipEndDate: Any?,
        @SerializedName("membership_start_date")
        var membershipStartDate: Any?,
        @SerializedName("mental_disabilities")
        var mentalDisabilities: Any?,
        @SerializedName("mental_disabilities_desc")
        var mentalDisabilitiesDesc: Any?,
        @SerializedName("muscle_joint_problems")
        var muscleJointProblems: Any?,
        @SerializedName("muscle_joint_problems_desc")
        var muscleJointProblemsDesc: Any?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("number_verify")
        var numberVerify: Int?,
        @SerializedName("oedema")
        var oedema: Any?,
        @SerializedName("oedema_desc")
        var oedemaDesc: Any?,
        @SerializedName("offline_payment")
        var offlinePayment: String?,
        @SerializedName("online_payment")
        var onlinePayment: String?,
        @SerializedName("other_goal")
        var otherGoal: Any?,
        @SerializedName("other_goal_desc")
        var otherGoalDesc: Any?,
        @SerializedName("otp")
        var otp: Any?,
        @SerializedName("palpitations")
        var palpitations: Any?,
        @SerializedName("palpitations_desc")
        var palpitationsDesc: Any?,
        @SerializedName("performance_enhancement")
        var performanceEnhancement: Any?,
        @SerializedName("phone")
        var phone: String?,
        @SerializedName("physical_disabilities")
        var physicalDisabilities: Any?,
        @SerializedName("physical_disabilities_desc")
        var physicalDisabilitiesDesc: Any?,
        @SerializedName("pint_per_week")
        var pintPerWeek: Any?,
        @SerializedName("pneumonia")
        var pneumonia: Any?,
        @SerializedName("pneumonia_desc")
        var pneumoniaDesc: Any?,
        @SerializedName("primary_area_code")
        var primaryAreaCode: String?,
        @SerializedName("primary_contact_email")
        var primaryContactEmail: String?,
        @SerializedName("primary_contact_name")
        var primaryContactName: String?,
        @SerializedName("primary_contact_relation")
        var primaryContactRelation: String?,
        @SerializedName("primary_country_code")
        var primaryCountryCode: String?,
        @SerializedName("primary_phone")
        var primaryPhone: String?,
        @SerializedName("province")
        var province: Any?,
        @SerializedName("recent_childbirth")
        var recentChildbirth: Any?,
        @SerializedName("recent_childbirth_desc")
        var recentChildbirthDesc: Any?,
        @SerializedName("recent_surgery")
        var recentSurgery: Any?,
        @SerializedName("recent_surgery_desc")
        var recentSurgeryDesc: Any?,
        @SerializedName("secondary_area_code")
        var secondaryAreaCode: Any?,
        @SerializedName("secondary_contact_email")
        var secondaryContactEmail: Any?,
        @SerializedName("secondary_contact_name")
        var secondaryContactName: Any?,
        @SerializedName("secondary_contact_relation")
        var secondaryContactRelation: Any?,
        @SerializedName("secondary_country_code")
        var secondaryCountryCode: Any?,
        @SerializedName("secondary_phone")
        var secondaryPhone: Any?,
        @SerializedName("shortness_of_breath")
        var shortnessOfBreath: Any?,
        @SerializedName("shortness_of_breath_desc")
        var shortnessOfBreathDesc: Any?,
        @SerializedName("smoker")
        var smoker: Any?,
        @SerializedName("stamina_building")
        var staminaBuilding: Any?,
        @SerializedName("suit_assigned")
        var suitAssigned: Int?,
        @SerializedName("tachycardia")
        var tachycardia: Any?,
        @SerializedName("tachycardia_desc")
        var tachycardiaDesc: Any?,
        @SerializedName("toning")
        var toning: Any?,
        @SerializedName("twiiter_key")
        var twiiterKey: Any?,
        @SerializedName("ulcers")
        var ulcers: Any?,
        @SerializedName("ulcers_desc")
        var ulcersDesc: Any?,
        @SerializedName("unique_id")
        var uniqueId: String?,
        @SerializedName("updated_at")
        var updatedAt: String?,
        @SerializedName("user_id")
        var userId: Int?,
        @SerializedName("user_type")
        var userType: Any?,
        @SerializedName("waiver_form")
        var waiverForm: String?,
        @SerializedName("weight")
        var weight: String?,
        @SerializedName("weight_unit")
        var weightUnit: String?,
        @SerializedName("weightloss")
        var weightloss: Int?,
        @SerializedName("zip")
        var zip: String?
    )
}