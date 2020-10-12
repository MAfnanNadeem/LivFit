package life.mibo.fitbitsdk.service.api.okhttp.interceptor

import android.util.Log
import life.mibo.fitbitsdk.service.api.OAuthDataService
import life.mibo.fitbitsdk.service.api.RefreshTokenService
import life.mibo.fitbitsdk.service.api.endpoint.AuthEndpoint
import life.mibo.fitbitsdk.service.models.auth.OAuthAccessToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class AuthenticationInterceptor(val oAuthDataService: OAuthDataService) : Interceptor {
     val TAG = AuthenticationInterceptor::class.java.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        //Auth calls should be pre-authed with a `Basic` token
        if(chain.request().url.toString().equals(oAuthDataService.getTokenServiceUrl(), true)) {
            return chain.proceed(chain.request())
        }

        //Check token validity
        val token = oAuthDataService.token ?: return noTokenResponse(chain.request())
        //If valid, attach as Header to request, continue in the chain
        val tokenNeedsRefreshed:Boolean = token.needsRefresh()

        if(!tokenNeedsRefreshed) {
            Log.d(TAG, "intercept - token refresh not needed")
            return proceedInChain(chain, token)
        }
        //All active Threads will pause here
        return synchronized(oAuthDataService) {
            val accessToken = oAuthDataService.token
            when {
                accessToken != null -> {
                    Log.d(TAG, "intercept - token refresh needed")

                    return try {
                        proceedInChain(chain, oAuthDataService.refreshTokenWithLatch(accessToken))
                    } catch (error: RefreshTokenService.HttpError) {
                        Response.Builder()
                                .code(error.code)
                                .protocol(Protocol.HTTP_1_1)
                                .body(ResponseBody.create(error.contentType.toMediaTypeOrNull(), error.errorBody))
                                .message(error.errorBody)
                                .request(chain.request())
                                .build()
                    }
                }
                else ->{
                    Log.d(TAG, "intercept - no token present")
                    return noTokenResponse(chain.request())
                }
            }
        }
    }

    private fun noTokenResponse(request: Request) = Response.Builder()
            .protocol(Protocol.HTTP_1_1)
            .code(HTTP_UNAUTHORIZED)
            .body(ResponseBody.create("text/plain".toMediaTypeOrNull(), "No token supplied"))
            .message("No token supplied")
            .request(request)
            .build()

    private fun proceedInChain(chain: Interceptor.Chain, validToken: OAuthAccessToken): Response {
        val request = chain.request()
        val header: Pair<String, String> = AuthEndpoint.bearerToken(validToken)
        val authedRequest = request.newBuilder().addHeader(header.first, header.second).build()
        return chain.proceed(authedRequest)
    }
}