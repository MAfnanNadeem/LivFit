/*
 *  Created by Sumeet Kumar on 1/15/20 3:33 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:33 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.program


import com.google.gson.annotations.SerializedName

data class ProgramPost(
    @SerializedName("ClientID")
    var clientID: String?,
    @SerializedName("Data")
    var `data`: DataPost?,
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
) {
    constructor(
        token: String?, pageNo: Int? = 1, pageSize: Int? = 50,
        search: String? = "", trainerId: String? = ""
    ) : this(
        "Client1213", DataPost(pageNo, pageSize, search, trainerId), "192.168.195.122", "SearchPrograms",
        "2019-12-10T04:49:11.6570000", token, "1.0.0.0"
    )
}