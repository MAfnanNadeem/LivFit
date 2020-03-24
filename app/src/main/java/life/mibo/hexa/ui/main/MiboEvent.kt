/*
 *  Created by Sumeet Kumar on 1/15/20 10:10 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 10:10 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.main

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object MiboEvent {

    private lateinit var firebase: FirebaseAnalytics

    fun init(context: Context) {
        firebase = FirebaseAnalytics.getInstance(context)
    }

    fun pageEvent(userName: String, userId: String, pageName: String) {
        val bundle = Bundle()
        bundle.putString("user_id", userId)
        bundle.putString("user_name", userName)
        bundle.putString("page_name", pageName)
        post(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun loginSuccess(userName: String, email: String) {
        try {
            val bundle = Bundle()
            bundle.putString("user_email", email)
            bundle.putString("user_name", userName)
            post(FirebaseAnalytics.Event.LOGIN, bundle)
        } catch (e: java.lang.Exception) {

        }
    }

    fun registerEvent(userName: String, email: String) {
        val bundle = Bundle()
        bundle.putString("user_email", email)
        bundle.putString("user_name", userName)
        post(FirebaseAnalytics.Event.SIGN_UP, bundle)
    }

    fun registerSuccess(userId: String) {
        val bundle = Bundle()
        bundle.putString("user_id", userId)
        bundle.putString("registered", "success")
        post(FirebaseAnalytics.Event.SIGN_UP + "_success", bundle)
    }

    fun registerError(userId: String, response: String) {
        val bundle = Bundle()
        bundle.putString("user_id", userId)
        bundle.putString("response", response)
        post(FirebaseAnalytics.Event.SIGN_UP, bundle)
    }

    fun otpSuccess(userId: String, otp: String) {
        val bundle = Bundle()
        bundle.putString("user_id", userId)
        bundle.putString("otp", otp)
        post(FirebaseAnalytics.Event.SIGN_UP + "otp", bundle)
    }

    fun event(tag: String, value: String?) {
        val bundle = Bundle()
        bundle.putString(tag, value)
        post("mibo_event", bundle)
    }

    private fun post(type: String, bundle: Bundle?) {
        try {
            firebase.logEvent(type, bundle)
        } catch (e: Exception) {

        }
    }

    fun log(throwable: Throwable) {
        try {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        } catch (e: Exception) {

        }
    }

    fun log(msg: String) {
        try {
            FirebaseCrashlytics.getInstance().log(msg)
        } catch (e: Exception) {
            log(e)
        }
    }

    fun getInstance() : MiboEvent {
        return MiboEvent
    }

}