/*
 *  Created by Sumeet Kumar on 6/16/20 12:07 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/16/20 12:07 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.user_details


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class UpdateMemberDetails(data: Data?, token: String?) :
    BasePost<UpdateMemberDetails.Data?>(data, "UpdateMemberDetails", token) {
    data class Data(
        @SerializedName("City")
        var city: String?,
        @SerializedName("Country")
        var country: String?,
        @SerializedName("DOB")
        var dOB: String?,
        @SerializedName("FirstName")
        var firstName: String?,
        @SerializedName("LastName")
        var lastName: String?,
        @SerializedName("Gender")
        var gender: String?,
        @SerializedName("CountryCode")
        var countryCode: String?,
        @SerializedName("Phone")
        var number: String?,
        @SerializedName("MemberID")
        var memberID: Int?,
        @SerializedName("TrainerID")
        var trainerID: Int? = null
    )
}