package life.mibo.fitbitsdk.service.api

import life.mibo.fitbitsdk.service.models.auth.OAuthAccessToken
import life.mibo.fitbitsdk.service.storage.OAuthAccessTokenStorage
import java.util.concurrent.CountDownLatch


open class OAuthDataService(private val storageDelegate: OAuthAccessTokenStorage,
                            private val refreshTokenDelegate: RefreshTokenService,
                            private val networkConnectivityDelegate: NetworkConnectivityChecker,
                            private val onTokenDroppedListener: TokenDroppedListener) :
        OAuthAccessTokenStorage, RefreshTokenService by refreshTokenDelegate,
        NetworkConnectivityChecker by networkConnectivityDelegate {

    override var token : OAuthAccessToken?
            get() = storageDelegate.token
            set(value) {
                storageDelegate.token = value
                if (value == null) {
                    onTokenDroppedListener.onTokenDropped()
                }
            }

    @Throws(RefreshTokenService.HttpError::class, Throwable::class)
    fun refreshTokenWithLatch(token: OAuthAccessToken): OAuthAccessToken {

        var throwable: Throwable? = null
        val latch = CountDownLatch(1)
        refreshToken(token, object: RefreshTokenService.TokenListener {
            override fun onTokenReceived(token: OAuthAccessToken) {
                token.updateExpiration()
                this@OAuthDataService.token = token
                latch.countDown()
            }

            override fun onHttpError(error: RefreshTokenService.HttpError) {
                this@OAuthDataService.token = null
                throwable = error
                latch.countDown()
            }

            override fun onError(error: Throwable) {
                throwable = error
                latch.countDown()
            }

        })
        //await this Thread until the refresh completes
        latch.await()
        throw throwable ?: return this@OAuthDataService.token!!
    }
}