/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.login


import com.google.gson.annotations.SerializedName

data class LoginUser(
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
    @SerializedName("Version")
    var version: String?
) {
    constructor(username: String, password: String) : this(
        "Client1213",
        Data(username, password),
        "192.168.195.122",
        "LoginUser",
        "2019-12-10T04:49:11.6570000",
        "1.0.0.0"
    )
}