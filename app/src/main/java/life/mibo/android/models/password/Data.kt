/*
 *  Created by Sumeet Kumar on 2/6/20 8:58 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/6/20 8:58 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.password


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("currentPassword")
    var currentPassword: String?,
    @SerializedName("newPassword")
    var newPassword: String?,
    @SerializedName("UserID")
    var userID: Int?
)