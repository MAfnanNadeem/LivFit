package life.mibo.fitbitsdk.service.models.auth

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import life.mibo.fitbitsdk.authentication.FitbitManager
import life.mibo.fitbitsdk.authentication.Scope
import java.lang.reflect.Type
import java.util.*


class OAuthAccessTokenTypeAdapter: JsonDeserializer<OAuthAccessToken> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): OAuthAccessToken {
        //Ridiculous custom serialization because GSON doesn't support custom setters
        FitbitManager.log("deserialize "+json)
        var access_token: String? = null
        var expires_in: Int? = null
        var refresh_token: String? = null
        var scope: List<Scope>? = null
        var token_type: String? = null
        var user_id: String? = null

        val jsonObject: JsonObject = json.asJsonObject
        jsonObject.entrySet().forEach {
            when(it.key) {
                "expires_in" -> {
                    expires_in = context.deserialize(it.value, Int::class.java)
                }
                else -> {
                    val value: String = context.deserialize(it.value, String::class.java)
                    when(it.key) {
                        "access_token" -> access_token = value
                        "refresh_token" -> refresh_token = value
                        "scope" -> scope = parseScopes(value)
                        "token_type" -> token_type = value
                        "user_id" -> user_id = value
                    }
                }
            }
        }
        return OAuthAccessToken(access_token, expires_in, refresh_token, scope, token_type, user_id)
    }

    private fun parseScopes(scopes: String): List<Scope> {
        val scopesArray = scopes.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val scopesList = ArrayList<Scope>()
        for (scopeStr in scopesArray) {
            val scope = Scope.fromString(scopeStr)
            if (scope != null) {
                scopesList.add(scope)
            }
        }

        return scopesList
    }
}