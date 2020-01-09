/*
 *  Created by Sumeet Kumar on 1/9/20 2:17 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.user_details


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address1")
    var address1: String?,
    @SerializedName("address2")
    var address2: Any?,
    @SerializedName("age")
    var age: String?,
    @SerializedName("city")
    var city: String?,
    @SerializedName("contact")
    var contact: String?,
    @SerializedName("country")
    var country: String?,
    @SerializedName("dob")
    var dob: String?,
    @SerializedName("firstName")
    var firstName: String?,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("identificationNumber")
    var identificationNumber: String?,
    @SerializedName("imageThumbnail")
    var imageThumbnail: String?,
    @SerializedName("lastName")
    var lastName: String?,
    @SerializedName("latitude")
    var latitude: String?,
    @SerializedName("longitude")
    var longitude: String?,
    @SerializedName("medicalHistory")
    var medicalHistory: MedicalHistory?,
    @SerializedName("number_verify")
    var numberVerify: Int?,
    @SerializedName("primaryContactEmail")
    var primaryContactEmail: String?,
    @SerializedName("primaryContactName")
    var primaryContactName: String?,
    @SerializedName("primaryContactRelation")
    var primaryContactRelation: String?,
    @SerializedName("primaryPhone")
    var primaryPhone: String?,
    @SerializedName("province")
    var province: Any?,
    @SerializedName("secondaryContactEmail")
    var secondaryContactEmail: Any?,
    @SerializedName("secondaryContactName")
    var secondaryContactName: Any?,
    @SerializedName("secondaryContactRelation")
    var secondaryContactRelation: Any?,
    @SerializedName("secondaryPhone")
    var secondaryPhone: String?,
    @SerializedName("trainingGoals")
    var trainingGoals: TrainingGoals?,
    @SerializedName("zip")
    var zip: String?
)