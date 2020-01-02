package life.mibo.hexa.models.base

import com.google.gson.annotations.SerializedName

open class PostModel(
    @SerializedName("Data")
    var `data`: Any?,
    @SerializedName("ClientID")
    var clientID: String = "Client1213",
    @SerializedName("IPAddress")
    var iPAddress: String = "192.168.195.122",
    @SerializedName("RequestType")
    var requestType: String = "RegisterGuestMember",
    @SerializedName("TimeStamp")
    var timeStamp: String = "2019-12-10T04:49:11.6570000",
    @SerializedName("Version")
    var version: String = "1.0.0.0"
) : BaseModel() {

    companion object {
        fun create(any: Any): PostModel {
            return PostModel(any)
        }
    }
}
