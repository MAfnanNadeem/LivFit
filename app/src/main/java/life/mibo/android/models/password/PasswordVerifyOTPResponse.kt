/*
 *  Created by Sumeet Kumar on 5/28/20 11:48 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/28/20 11:48 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.password


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class PasswordVerifyOTPResponse(data: Data?) : BaseResponse<PasswordVerifyOTPResponse.Data?>(data) {
    data class Data(
        @SerializedName("message")
        var message: String?,
        @SerializedName("UserID")
        var userID: String?
    )
}