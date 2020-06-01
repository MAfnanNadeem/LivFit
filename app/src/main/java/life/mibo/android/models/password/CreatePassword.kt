/*
 *  Created by Sumeet Kumar on 5/28/20 11:00 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/28/20 11:00 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.password


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class CreatePassword(data: Data?) : BasePost<CreatePassword.Data?>(data, "CreatePassword", "") {

    data class Data(
        @SerializedName("Password")
        var password: String?,
        @SerializedName("UserID")
        var userID: String?
    )
}