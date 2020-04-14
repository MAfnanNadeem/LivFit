/*
 *  Created by Sumeet Kumar on 2/6/20 8:58 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/6/20 8:58 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.password


import com.google.gson.annotations.SerializedName

data class ChangePassword(
    @SerializedName("ClientID")
    var clientID: String?,
    @SerializedName("Data")
    var `data`: Data?,
    @SerializedName("IPAddress")
    var iPAddress: String?,
    @SerializedName("RequestType")
    var requestType: String?,
    @SerializedName("TimeStamp")
    var timeStamp: String?,
    @SerializedName("token")
    var token: String?,
    @SerializedName("Version")
    var version: String?
)