package life.mibo.fitbitsdk.service.api.okhttp.retrofit

import life.mibo.fitbitsdk.gsonhelper.SafeGson
import life.mibo.fitbitsdk.service.api.RefreshTokenService
import life.mibo.fitbitsdk.service.api.endpoint.Endpoint
import life.mibo.fitbitsdk.service.api.endpoint.Environment
import life.mibo.fitbitsdk.service.models.auth.OAuthAccessToken
import life.mibo.fitbitsdk.service.models.auth.RequestRefreshTokenModel
import life.mibo.fitbitsdk.service.models.auth.RequestTokenModel
import life.mibo.fitbitsdk.service.api.okhttp.retrofit.AuthService
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitRefreshTokenService(private val env: Environment, private val endpoint: Endpoint, private val builder: OkHttpClient.Builder) : RefreshTokenService {

    private val client: OkHttpClient by lazy {
        builder.build()
    }
    private val authService: AuthService by lazy {
        generateService()
    }

    private fun generateService(): AuthService {
        val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(SafeGson.getSingleton().gsonInstance))
                .baseUrl(endpoint.auth(env))
                .build()
        return retrofit.create(AuthService::class.java)
    }

    private fun createCallback(listener: RefreshTokenService.TokenListener): Callback<OAuthAccessToken> {
        return object : Callback<OAuthAccessToken> {
            override fun onFailure(call: Call<OAuthAccessToken>?, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(call: Call<OAuthAccessToken>?, response: Response<OAuthAccessToken>) {
                val token: OAuthAccessToken? = response.body()
                if (response.isSuccessful && token != null) {
                    listener.onTokenReceived(token)
                } else {
                    val responseBody = response.raw().body
                    val contentType: String = responseBody?.contentType()?.toString() ?: ""
                    val errorBodyStr: String = response.errorBody()?.string() ?: ""
                    listener.onHttpError(RefreshTokenService.HttpError(response.code(), contentType, errorBodyStr))
                }
            }

        }
    }

    override fun refreshToken(outdatedToken: OAuthAccessToken, listener: RefreshTokenService.TokenListener) {
        val refreshToken: String? = outdatedToken.refresh_token
        if (refreshToken?.isNotBlank() == true) {
          //  val refreshTokenModel = RequestRefreshTokenModel(refreshToken)
            authService.refreshToken(endpoint.authEndpoint.basicHeader(env).second, RequestRefreshTokenModel.REFRESH_GRANT_TYPE, refreshToken).enqueue(createCallback(listener))
        } else {
            listener.onError(IllegalArgumentException("Token parameters invalid"))
        }
    }

    override fun getToken(requestTokenModel: RequestTokenModel, listener: RefreshTokenService.TokenListener) {
        authService.authorizeToken(endpoint.authEndpoint.basicHeader(env).second, requestTokenModel).enqueue(createCallback(listener))
    }

    override fun getTokenServiceUrl(): String = path().toString()

    private fun path(): HttpUrl =
            endpoint.auth(env).toHttpUrlOrNull()?.newBuilder()?.addPathSegments("oauth2/token")?.build()
                    ?: throw IllegalArgumentException("Unable to parse auth endpoint: ${endpoint.auth(env)}")

}