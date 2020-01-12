package life.mibo.hexa.models.register


import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel

data class RegisterGuestMember(
    @SerializedName("Data")
    var `data`: Data?,
    @SerializedName("ClientID")
    var clientID: String = "Client1213",
    @SerializedName("IPAddress")
    var iPAddress: String = "192.168.195.122",
    @SerializedName("RequestType")
    var requestType: String = "RegisterUser",
    @SerializedName("TimeStamp")
    var timeStamp: String = "2019-12-10T04:49:11.6570000",
    @SerializedName("Version")
    var version: String = "1.0.0.0"
): BaseModel()