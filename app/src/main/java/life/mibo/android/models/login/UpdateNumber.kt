/*
 *  Created by Sumeet Kumar on 9/1/20 4:54 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/1/20 4:54 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.login


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class UpdateNumber(data: Data?) : BasePost<UpdateNumber.Data?>(data, "UpdateNumber", "") {

    data class Data(
        @SerializedName("CountryCode")
        var countryCode: String?,
        @SerializedName("PhoneNumber")
        var phoneNumber: String?,
        @SerializedName("UserID")
        var userID: String?
    )
}