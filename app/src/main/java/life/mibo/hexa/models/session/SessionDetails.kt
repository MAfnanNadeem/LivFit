/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.session


import com.google.gson.annotations.SerializedName

data class SessionDetails(
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
) {
    constructor(userId: String, token: String?) : this(
        "Client1213", Data(userId), "192.168.195.122", "LatestSessionDetails",
        "2019-12-10T04:49:11.6570000", token, "1.0.0.0"
    )
}