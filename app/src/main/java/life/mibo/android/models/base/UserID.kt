/*
 *  Created by Sumeet Kumar on 5/13/20 10:33 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/13/20 10:33 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base


import com.google.gson.annotations.SerializedName

class UserID(data: Data, requestType: String, token: String?) : BasePost<UserID.Data>(data, requestType, token) {
    data class Data(
        @SerializedName("UserID")
        var userID: String?
    )
}