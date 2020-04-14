/*
 *  Created by Sumeet Kumar on 1/25/20 8:23 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/25/20 8:18 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base

import com.google.gson.annotations.SerializedName

// Base model for post data to cloud, not properly tested type-parameterized object (T)
abstract class BasePost<T>(
    @SerializedName("Data") val data: T,
    @SerializedName("RequestType") var requestType: String?, @SerializedName("token") var authToken: String?
) : BaseModel {


    @SerializedName("ClientID")
    var clientID: String = "Client1214"

    @SerializedName("IPAddress")
    var iPAddress: String = "192.168.195.122"

    @SerializedName("TimeStamp")
    var timeStamp: String = "2019-12-10T04:49:11.6570000"

    @SerializedName("Version")
    var version: String = "1.0.0.0"

    //abstract fun init(data: T, type: String, token: String?)
}