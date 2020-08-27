/*
 *  Created by Sumeet Kumar on 7/5/20 4:03 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/5/20 3:54 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit.fitbit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import life.mibo.android.core.Prefs
import life.mibo.android.core.security.EncryptedPrefs
import life.mibo.android.ui.base.WebViewActivity
import life.mibo.android.ui.main.MiboApplication
import life.mibo.hardware.core.Logger
import org.threeten.bp.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class Fitbit(var context: Context?) {

    private var prefs: EncryptedPrefs? = null

    constructor() : this(null) {

    }

    companion object {

        var ANY = 1
        var FITBIT = 2
        var GOOGLE = 3
        var SAMSUNG = 4
        var REQUEST_CODE = 4744


        //private const val client = "22BNJ6"
        private const val client = "22BWRJ"
        private const val secret = "36aa6e40d555b5035fd0026748926c9a"
        private const val callback = "https://dev.mibolivfit.club/androidappcallback"
        const val api = "https://api.fitbit.com/1/user/"
        const val oAuth = "https://www.fitbit.com/oauth2/authorize"
        const val oAuthRefreshToken = "https://api.fitbit.com/oauth2/token"
        private const val token_key = "fitbit_token"
        private const val refresh_token_key = "fitbit_refresh_token"
        private const val token_type_key = "fitbit_token_type"
        private const val scope_key = "fitbit_scope"
        private const val user_key = "fitbit_user"
        private const val expire_key = "fitbit_expires_in"
        private const val expire_time_key = "fitbit_expires_time"

        fun setup(context: Context) {
//            val config = FitbitConfigurationBuilder()
//                .addRequiredScopes(Scope.activity, Scope.heartrate, Scope.profile, Scope.sleep)
//                .addOptionalScopes(Scope.location, Scope.nutrition, Scope.settings, Scope.weight)
//                .setClientCredentials(ClientCredentials(client, secret, callback))
//                .setTokenExpiresIn(2592000L)
//                .build()
            //FitbitManager.configure(context, config)
        }

        fun isLogged(): Boolean {
            try {
                val token: String =
                    Prefs.getEncrypted(MiboApplication.context).get(token_key, "", true) ?: ""
                return token.length > 5
            } catch (e: Exception) {

            }
            return false
        }


    }

    fun getPrefs(): EncryptedPrefs {
        if (prefs == null)
            prefs = Prefs.getEncrypted(context)
        return prefs!!
    }

    fun loginToFitbit(fragment: Fragment) {
        log("loginToFitbit")
        val list = listOf(
            "activity",
            "heartrate",
            "nutrition",
            "profile",
            "settings",
            "sleep",
            "social",
            "weight"
        )

        val state = "mibolivfitapp"
        val redirect = ""
        val response = "token"
        val expire = 86400L // 604800  2592000
        var scopes = ""
        var first = false
        for (scop in list) {
            if (first)
                scopes += "%20"
            scopes += scop
            first = true;
        }

        val url =
            "https://www.fitbit.com/oauth2/authorize?response_type=$response&client_id=$client&redirect_uri=$callback&scope=$scopes&expires_in=604800"
        WebViewActivity.launch(fragment, url, FITBIT)


    }

    fun fitbitSuccess(result: String, activity: Activity) {

        log("fitbitSuccess result $result")

        if (result.startsWith(callback)) {
            val uri = Uri.parse(result.replace("#", "?"))
            val token = uri.getQueryParameter("access_token")
            val user = uri.getQueryParameter("user_id")
            val scope = uri.getQueryParameter("scope")
            val tokenType = uri.getQueryParameter("token_type")
            val expiresIn = uri.getQueryParameter("expires_in")
            log("fitbitSuccess uri $uri")
            log("fitbitSuccess token $token")
            log("fitbitSuccess user $user")
            log("fitbitSuccess scope $scope")
            log("fitbitSuccess tokenType $tokenType")
            log("fitbitSuccess expiresIn $expiresIn")
            val prefs = Prefs.getEncrypted(activity)
            prefs.set(token_key, token, true)
            prefs.set(user_key, user, true)
            prefs.set(scope_key, scope, true)
            prefs.set(token_type_key, tokenType, true)
            prefs.set(expire_key, expiresIn, true)
            try {
                val cal = Calendar.getInstance()
                // log("fitbitSuccess Calendar $cal")
                // log("fitbitSuccess Calendar ${cal.timeInMillis}")
                //val t = expiresIn?.toLong().div(3600)
                val l = TimeUnit.SECONDS.toDays(expiresIn?.toLong()!!)
                cal.add(Calendar.DAY_OF_MONTH, l.toInt())
                // log("fitbitSuccess Calendar l $l")
                // log("fitbitSuccess Calendar $cal")
                // log("fitbitSuccess Calendar ${cal.timeInMillis}")

                val time = cal.timeInMillis
                prefs.set(expire_time_key, "$time", true)
            } catch (eE: java.lang.Exception) {

            }

            val intent = Intent()
            intent.putExtra("fitbit_result", result)
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()

        }
        // onPageStarted https://test.mibolivfit.club/androidappcallback#access_token=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkJOSjYiLCJzdWIiOiI4TVZWRlkiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd251dCB3c2V0IHdhY3Qgd3NvYyIsImV4cCI6MTU5NDUyOTA0MCwiaWF0IjoxNTkzOTI0MjQwfQ.eZ7yB3DMHuV2M0s_5tYZ_EJ-UDpT_-hniStuabYytUc&user_id=8MVVFY&scope=nutrition+settings+activity+social+heartrate&token_type=Bearer&expires_in=604800
        // onPageFinished https://test.mibolivfit.club/androidappcallback#access_token=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkJOSjYiLCJzdWIiOiI4TVZWRlkiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd251dCB3c2V0IHdhY3Qgd3NvYyIsImV4cCI6MTU5NDUyOTA0MCwiaWF0IjoxNTkzOTI0MjQwfQ.eZ7yB3DMHuV2M0s_5tYZ_EJ-UDpT_-hniStuabYytUc&user_id=8MVVFY&scope=nutrition+settings+activity+social+heartrate&token_type=Bearer&expires_in=604800
        //var data = ""
        // if (result.startsWith(callback))
        //     data = result.substring(0, callback.length)

    }

    fun getSteps2(date: String, callback: okhttp3.Callback) {
        //FitbitService()
    }

    fun refreshToken(tokenOld: String, callback: okhttp3.Callback) {

        //FitbitApi().getSteps(date, getToken() + "n", callback)
    }

    fun getSteps(date: String, callback: okhttp3.Callback) {

        FitbitApi().getSteps(date, getToken(), callback)
    }

    fun getSteps(startDate: String, endDate: String, callback: okhttp3.Callback) {
        FitbitApi().getSteps(startDate, endDate, getToken(), callback)
    }

    fun needToRefresh() {
        try {
            val exp = getPrefs().get(expire_time_key, "", true) ?: ""
            if (exp.length > 1) {
                val time = exp.toLong()
                if (LocalDate.now().isBefore(LocalDate.ofEpochDay(time))) {
                    // return false
                    return
                }

                val list = listOf(
                    "activity",
                    "heartrate",
                    "nutrition",
                    "profile",
                    "settings",
                    "sleep",
                    "social",
                    "weight"
                )

                val response = "token"
                var scopes = ""
                var first = false
                for (scop in list) {
                    if (first)
                        scopes += "%20"
                    scopes += scop
                    first = true;
                }

                val url =
                    "https://www.fitbit.com/oauth2/authorize?response_type=$response&client_id=$client&redirect_uri=$callback&scope=$scopes&expires_in=604800"
                WebViewActivity.launch(context!!, url, FITBIT)


            }
        } catch (e: Exception) {

        }

    }

    fun isConnected(): Boolean {
        try {
            // return FitbitManager.isLoggedIn()
            return getToken().length > 5
        } catch (e: Exception) {

        }
        return false
    }

    private fun getToken(): String {
        return getPrefs().get(token_key, "", true) ?: ""
    }


    fun log(msg: String) {
        Logger.e("FITBIT", msg)
    }
}