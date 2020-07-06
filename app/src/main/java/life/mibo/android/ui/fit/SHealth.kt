/*
 *  Created by Sumeet Kumar on 7/4/20 11:04 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/4/20 11:04 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import life.mibo.android.core.Prefs
import life.mibo.android.core.security.EncryptedPrefs
import life.mibo.android.ui.base.WebViewActivity
import life.mibo.android.ui.fit.fitbit.Fitbit
import life.mibo.hardware.core.Logger

class SHealth(var context: Context?) {

    private var prefs: EncryptedPrefs? = null

    constructor() : this(null) {

    }

    companion object {
        private const val client = "22BNJ6"
        private const val secret = "71d2a02224ed51d39aef3a478186aa70"
        private const val callback = "https://test.mibolivfit.club/androidappcallback"
        const val api = "api.fitbit.com"
        const val oAuth = "https://www.fitbit.com/oauth2/authorize"
        const val oAuthRefreshToken = "https://api.fitbit.com/oauth2/token"
    }

    fun getPrefs(): EncryptedPrefs {
        if (prefs == null)
            prefs = Prefs.getEncrypted(context)
        return prefs!!
    }
    fun isConnected(): Boolean {
        //return getPrefs().get("fitbit_token", "", true)?.length ?: 0 > 5
        return false
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
            "https://www.fitbit.com/oauth2/authorize?response_type=$response&client_id=${client}&redirect_uri=${callback}&scope=$scopes&expires_in=604800"
        WebViewActivity.launch(fragment, url, Fitbit.FITBIT)


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
            prefs.set("fitbit_token", token, true)
            prefs.set("fitbit_user", user, true)
            prefs.set("fitbit_scope", scope, true)
            prefs.set("fitbit_token_type", tokenType, true)
            prefs.set("fitbit_expires_in", expiresIn, true)
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

    private fun isFitbitConnected(): Boolean {
        return getPrefs().get("fitbit_token", "", true)?.length ?: 0 > 5
    }


    fun log(msg: String) {
        Logger.e("FITBIT", msg)
    }
}