package life.mibo.fitbitsdk.service.api.endpoint

import android.util.Base64
import life.mibo.fitbitsdk.service.models.auth.OAuthAccessToken

enum class Environment {
    PRODUCTION, DISASTER_RECOVERY, STAGING, INTEGRATION, DEVELOPMENT
}

class AuthEndpoint (val oauthClientId: String, val oauthSecret: String){
    companion object {
        const val OAUTH_HEADER_KEY: String = "Authorization"
        const val OAUTH_BASIC: String = "Basic"
        const val OAUTH_BEARER: String = "Bearer"

        fun formatBearerToken(accessToken: String) = "$OAUTH_BEARER $accessToken"

        fun bearerToken(token: OAuthAccessToken): Pair<String, String> {
            val accessToken = token.access_token
            if(accessToken?.isNotBlank() == true) {
                return OAUTH_HEADER_KEY to formatBearerToken(accessToken)
            }
            throw IllegalArgumentException("OAuthAccessToken has an invalid refresh token")
        }
    }

    fun basicHeader(env: Environment): Pair<String, String> {
        val basicToken = Base64.encode("$oauthClientId:$oauthSecret".toByteArray(), Base64.NO_WRAP)
        return OAUTH_HEADER_KEY to "$OAUTH_BASIC ${String(basicToken)}"
    }

    fun auth(env: Environment) : String {
        return when(env) {
            Environment.PRODUCTION -> "https://api.fitbit.com/"
            else -> ""
        }
    }
}

abstract class Endpoint {
    abstract val authEndpoint: AuthEndpoint

    fun auth(env: Environment): String = authEndpoint.auth(env)
}


