package life.mibo.fitbitsdk.service.storage

import life.mibo.fitbitsdk.service.models.auth.OAuthAccessToken


interface OAuthAccessTokenStorage {
    var token : OAuthAccessToken?
}