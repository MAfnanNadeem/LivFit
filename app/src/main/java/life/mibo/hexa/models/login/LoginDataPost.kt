package life.mibo.hexa.models.login


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.PostDataModel

data class LoginDataPost(
    @SerializedName("token")
    var token: String?, var body: LoginData
) : PostDataModel(body)