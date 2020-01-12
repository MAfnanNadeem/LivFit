/*
 * $Created by $Sumeet $Kumar 2020.
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.verify_otp


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel

data class VerifyOTP(
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
) : BaseModel(){
    constructor(userId: String, otp: String) : this(
        "Client1213",
        Data(userId, otp),
        "192.168.195.122",
        "VerifyOTP",
        "2019-12-10T04:49:11.6570000",
        "1.0.0.0"
    )
}