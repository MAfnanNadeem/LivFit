package life.mibo.fitbitsdk.service.models.auth

import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
import android.util.Log
import life.mibo.fitbitsdk.authentication.Scope
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import life.mibo.fitbitsdk.authentication.FitbitManager
import java.util.*

@Parcelize
class OAuthAccessToken(
        var access_token: String?,
        var expires_in: Int?,
        var refresh_token: String?,
        var scopes: List<Scope>?,
        var token_type: String?,
        var user_id: String?) : Parcelable {

    var expiration: Calendar

    private companion object : Parceler<OAuthAccessToken> {
        val TAG = OAuthAccessToken::class.java.simpleName

        override fun OAuthAccessToken.write(parcel: Parcel, flags: Int) {
            parcel.writeString(access_token)
            parcel.writeInt(expires_in ?: 0)
            parcel.writeString(refresh_token)
            parcel.writeString(scopes?.joinToString(",") { it.name } ?: "")
            parcel.writeString(token_type)
            parcel.writeString(user_id)
            parcel.writeLong(expiration.timeInMillis)
        }

        override fun create(parcel: Parcel): OAuthAccessToken {
            val token = OAuthAccessToken(parcel.readString(), parcel.readInt(), parcel.readString(), parseScopes(parcel.readString() ?: ""), parcel.readString(), parcel.readString())
            val expirationDate = Calendar.getInstance()
            expirationDate.timeInMillis = parcel.readLong()
            token.expiration = expirationDate

            FitbitManager.log(TAG+ "create/parcel - Token next expires at:" + getExpirationDateFormatted(token))

            return token
        }

        private fun parseScopes(scopes: String): List<Scope> {
            val scopesArray = scopes.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val scopesList = ArrayList<Scope>()
            for (scopeStr in scopesArray) {
                val scope = Scope.fromString(scopeStr)
                if (scope != null) {
                    scopesList.add(scope)
                }
            }

            return scopesList
        }

        fun getExpirationDateFormatted(token:OAuthAccessToken?): String {
            FitbitManager.log("getExpirationDateFormatted ${token?.expires_in} ")

            token?.let {
                val dateString = DateFormat.format("dd-MM-yyyy hh:mm:ss", it.expiration)
                return it.expiration?.timeInMillis?.toString() + " : " + dateString
            }
            return "No token"
        }

    }

    fun updateExpiration() {
        FitbitManager.log("updateExpiration $expires_in ")
        this.expiration = expires_in?.let {
            Calendar.getInstance().apply {
                this.add(Calendar.SECOND, it)
            }
        } ?: Calendar.getInstance()
        FitbitManager.log(TAG+ "updateExpiration - token next expires at: "  + getExpirationDateFormatted(this))
    }

    init {
        this.expiration = expires_in?.let {
            Calendar.getInstance().apply {
                this.add(Calendar.SECOND, it)
            }
        } ?: Calendar.getInstance()

        FitbitManager.log(TAG+ " Init - Token next expires at: "  + getExpirationDateFormatted(this))

    }

    fun needsRefresh(): Boolean {
        //Force a refresh up to 5 minutes before the expiration time
        val windowPeriod = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }

        var refresh = false
        expiration?.let {
            refresh = it.before(windowPeriod)
        }
        FitbitManager.log(TAG+ "needsRefresh() $refresh - Token currently expires at: "  + getExpirationDateFormatted(this))

        return refresh
    }

}