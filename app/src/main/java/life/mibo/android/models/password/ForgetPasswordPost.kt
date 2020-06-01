/*
 *  Created by Sumeet Kumar on 5/28/20 9:39 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/28/20 9:39 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.password


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class ForgetPasswordPost(data: Data?) :
    BasePost<ForgetPasswordPost.Data?>(data, "PasswordOTP", "") {
    data class Data(
        @SerializedName("PhoneOrEmail")
        var phoneOrEmail: String?,
        @SerializedName("CountryCode")
        var countryCode: String? = "",
        @SerializedName("Type")
        var type: Int? = 2
    )
}