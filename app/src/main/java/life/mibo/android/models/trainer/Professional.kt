/*
 *  Created by Sumeet Kumar on 5/13/20 11:50 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/13/20 11:50 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer

import com.google.gson.annotations.SerializedName

data class Professional(
    @SerializedName("Avatar")
    var avatar: String?,
    @SerializedName("Designation")
    var designation: String?,
    @SerializedName("dob")
    var dob: String?,
    @SerializedName("Gender")
    var gender: String?,
    @SerializedName("ID")
    var id: Int?,
    @SerializedName("Name")
    var name: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("Specializations")
    var specializations: List<String?>?,
    @SerializedName("Certifications")
    var certifications: List<Certifications?>?,
    @SerializedName("Phone")
    var phone: String?,
    @SerializedName("Country")
    var country: String? = "",
    @SerializedName("City")
    var city: String? = "",
    @SerializedName("linked")
    var linked: String? = "0"
) {
    fun match(query: String): Boolean {
        if (name?.toLowerCase()?.contains(query) == true)
            return true
        if (city?.toLowerCase()?.contains(query) == true)
            return true
        if (country?.toLowerCase()?.contains(query) == true)
            return true
        return false
    }
}
