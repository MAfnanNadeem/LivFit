/*
 *  Created by Sumeet Kumar on 5/28/20 11:42 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/28/20 10:32 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.password


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class ForgetPasswordVerifyOtp(data: Data?) :
    BasePost<ForgetPasswordVerifyOtp.Data?>(data, "PasswordVerifyOTP", "") {
    data class Data(
        @SerializedName("PhoneOrEmail")
        var phoneOrEmail: String?,
        @SerializedName("Otp")
        var otp: String?,
        @SerializedName("CountryCode")
        var countryCode: String? = "",
        @SerializedName("Type")
        var type: Int? = 2
    )
}