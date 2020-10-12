package life.mibo.fitbitsdk.service.storage

import android.content.SharedPreferences
import life.mibo.fitbitsdk.gsonhelper.SafeGson
import life.mibo.fitbitsdk.service.models.auth.OAuthAccessToken

class SharedPreferenceTokenStorage @JvmOverloads constructor(private val preferences: SharedPreferences, private val key: String = OAUTH_TOKEN_KEY) : OAuthAccessTokenStorage {
    companion object {
        private const val OAUTH_TOKEN_KEY = "oauth_token"
    }

    override var token: OAuthAccessToken?
        get() {
            return if (preferences.contains(key)) {
                SafeGson.fromJson(preferences.getString(key, ""), OAuthAccessToken::class.java)
            } else {
                null
            }
        }
        set(value) {
            if (value == null) {
                preferences.edit().remove(key).apply()
            } else {
                preferences.edit().putString(key, SafeGson.toJson(value)).apply()
            }
        }

}