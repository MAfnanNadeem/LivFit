/*
 *  Created by Sumeet Kumar on 4/13/20 2:20 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/13/20 2:20 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.member


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class ChangePassword(data: Data?, token: String?) :
    BasePost<ChangePassword.Data?>(data, "ChangePassword", token) {
    class Data(
        @SerializedName("current_password")
        var currentPassword: String?,
        @SerializedName("new_password")
        var newPassword: String?,
        @SerializedName("UserID")
        var userID: String?
    )
}