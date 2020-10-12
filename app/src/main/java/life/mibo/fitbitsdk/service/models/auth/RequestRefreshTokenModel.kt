package life.mibo.fitbitsdk.service.models.auth


open class RequestRefreshTokenModel(val refresh_token: String, val scope: String = DEFAULT_SCOPE, val grant_type: String = REFRESH_GRANT_TYPE) {
    companion object {
        const val REFRESH_GRANT_TYPE = "refresh_token"
        const val DEFAULT_SCOPE = "urn:mboframeworkapi"
    }
}