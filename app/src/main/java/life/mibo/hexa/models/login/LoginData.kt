package life.mibo.hexa.models.login


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel

data class LoginData(
    @SerializedName("Email")
    var email: String?,
    @SerializedName("Password")
    var password: String?
): BaseModel()