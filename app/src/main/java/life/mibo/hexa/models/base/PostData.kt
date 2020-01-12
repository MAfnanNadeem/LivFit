/*
 *  Created by Sumeet Kumar on 1/12/20 11:51 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 11:50 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.base


import com.google.gson.annotations.SerializedName

data class PostData(
    @SerializedName("ClientID")
    var clientID: String?,
    @SerializedName("Data")
    var `data`: Post?,
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
){
    constructor(userId: String, token: String?, type: String?) : this(
        "Client1213", Post(userId), "192.168.195.122", type,
        "2019-12-10T04:49:11.6570000", token, "1.0.0.0"
    )
}