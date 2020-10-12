package life.mibo.fitbitsdk.service.api

import life.mibo.fitbitsdk.service.models.auth.OAuthAccessToken
import retrofit2.Call
import retrofit2.http.*

interface TokenService{

    @Headers("Content-Type: application/json")
    @POST("oauth2/revoke")
    fun revokeToken(@Query("token") token: String): Call<Void>

//    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("oauth2/token")
    fun authorizeCode(@Field("client_id") clientId: String,
                      @Field("grant_type") grantType: String,
                      @Field("redirect_uri") redirectUri: String,
                      @Field("code") code: String): Call<OAuthAccessToken>
}