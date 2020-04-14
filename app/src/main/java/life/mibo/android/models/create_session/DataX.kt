/*
 *  Created by Sumeet Kumar on 1/22/20 5:55 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 5:55 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.create_session


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("SessionID")
    var sessionID: Int?,
    @SerializedName("Success")
    var message: String?
)