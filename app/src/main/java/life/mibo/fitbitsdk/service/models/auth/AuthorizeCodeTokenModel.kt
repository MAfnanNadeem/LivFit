package life.mibo.fitbitsdk.service.models.auth

import com.google.gson.annotations.SerializedName

class AuthorizeCodeTokenModel(
        @SerializedName("client_id") val clientId: String,
        @SerializedName("grant_type") val grantType: String,
        @SerializedName("redirect_uri") val redirectUri: String,
        @SerializedName("code") val code: String
)