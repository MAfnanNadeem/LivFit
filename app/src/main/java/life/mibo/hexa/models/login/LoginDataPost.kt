package life.mibo.hexa.models.login


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.PostModel

data class LoginDataPost(
    @SerializedName("token")
    var token: String?, var body: LoginData
) : PostModel(body)