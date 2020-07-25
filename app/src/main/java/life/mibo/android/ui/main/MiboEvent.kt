/*
 *  Created by Sumeet Kumar on 1/15/20 10:10 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 10:10 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.main

import android.content.Context
import android.os.Bundle
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import life.mibo.android.core.Prefs
import life.mibo.hardware.core.Logger

object MiboEvent {

    private lateinit var firebase: FirebaseAnalytics

    // val INSTANCE = MiboEvent.getInstance()

    fun init(context: Context) {
        firebase = FirebaseAnalytics.getInstance(context)
        try {
            firebase.setUserId(Prefs.get(context).memberId)
            // firebase.setUserProperty(FirebaseAnalytics.UserProperty.SIGN_UP_METHOD)
        } catch (e: java.lang.Exception) {
        }
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
        post(FirebaseAnalytics.Event.SIGN_UP+"_error", bundle)
    }

    fun registerFailed(s: String) {
        val bundle = Bundle()
        bundle.putString("err_msg", s)
        post(FirebaseAnalytics.Event.SIGN_UP+"_failed", bundle)
    }

    fun otpSuccess(userId: String, otp: String) {
        val bundle = Bundle()
        bundle.putString("user_id", userId)
        bundle.putString("otp", otp)
        post(FirebaseAnalytics.Event.SIGN_UP + "_otp", bundle)
    }

    fun event(tag: String, value: String?) {
        val bundle = Bundle()
        bundle.putString("tag", tag)
        bundle.putString("value", value)
        post("mibo_event", bundle)
    }

    fun event(type: String, tag: String, value: String?) {
        val bundle = Bundle()
        bundle.putString("tag", tag)
        bundle.putString("value", value)
        post(type, bundle)
    }

    private fun post(type: String, bundle: Bundle?) {
        try {
            firebase.logEvent(type, bundle)
        } catch (e: Exception) {
            log(e)
        }
    }

    private fun event(type: String, bundle: Bundle?) {
        try {
            firebase.logEvent(type, bundle)
        } catch (e: Exception) {
            log(e)
        }
    }

    fun log(throwable: Throwable) {
        try {
            Logger.e("MiboEvent LOG $throwable")
            FirebaseCrashlytics.getInstance().recordException(throwable)
        } catch (e: Exception) {
            Logger.e("MiboEvent LOG-ERROR $e")
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

    fun fbLog(context: Context, event: String) {
        try {
            AppEventsLogger.newLogger(context).logEvent(event)
        } catch (e: Exception) {
            log(e)
        }
    }

    fun fbPurchase(context: Context, price: Double, currency: String?) {
        try {
            val bundle = Bundle()
            bundle.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
            AppEventsLogger.newLogger(context).logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, price, bundle)
        } catch (e: Exception) {
            log(e)
        }
    }


}