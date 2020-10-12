package life.mibo.fitbitsdk.service.api.okhttp.interceptor

import life.mibo.fitbitsdk.service.api.OAuthDataService
import life.mibo.fitbitsdk.service.api.endpoint.AuthEndpoint
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * OkHttp interceptor that responds to 401s returned by the auth servers
 */
class Authenticator(private val oAuthDataService: OAuthDataService) : Authenticator {
    companion object {
        //1 for the initial attempt, 1 for the retry
        const val MAX_AUTH_RETRIES = 2
    }

    /**
     * @return null will skip the retry and return the initial response up the stack
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        //We're here because we received 401

        //401s from auth are strictly disallowed retry policies. Do not pass go. Do not collect $200
        if(route?.address?.url.toString().equals(oAuthDataService.getTokenServiceUrl(), true)) {
            return null
        }

        val token = oAuthDataService.token ?: return null

        synchronized(oAuthDataService) {
            //This request has already hit the server more than once, past performance tells that's enough
            return if(responseCount(response) >= MAX_AUTH_RETRIES) {
                oAuthDataService.token = null
                null
            } else {
                try {
                    val request = response.request
                    val headerToken = AuthEndpoint.bearerToken(oAuthDataService.refreshTokenWithLatch(token))
                    request.newBuilder().addHeader(headerToken.first, headerToken.second).build()
                } catch (error: Throwable) {
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var responseShallow: Response? = response
        var result = 0
        while (responseShallow != null) {
            responseShallow = responseShallow.priorResponse
            result++
        }
        return result
    }
}