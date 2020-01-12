/*
 *  Created by Sumeet Kumar on 1/9/20 2:13 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 2:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.weight


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel

data class WeightAll(
    @SerializedName("ClientID")
    var clientID: String?,
    @SerializedName("Data")
    var `data`: DataX?,
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
): BaseModel()
{
    constructor(userId: String, token: String?) : this(
        "Client1213", DataX(userId), "192.168.195.122", "AllWeight",
        "2019-12-10T04:49:11.6570000", token, "1.0.0.0"
    )
}