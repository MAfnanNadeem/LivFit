package life.mibo.fitbitsdk.service.api

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class FitbitAuthenticator : Authenticator {


    private fun refreshToken(): String {
        return ""
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response?.request?.header("Authorization") != null) {
            return null // Give up, we've already attempted to authenticate.
        }
        val token = refreshToken()
        return response?.request?.newBuilder()?.header("Authorization", token)?.build()
    }

}