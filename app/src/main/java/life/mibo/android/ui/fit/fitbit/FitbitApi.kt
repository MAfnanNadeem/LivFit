/*
 *  Created by Sumeet Kumar on 7/5/20 4:04 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/5/20 3:58 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit.fitbit

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.JsonArray
import kotlinx.coroutines.Deferred
import life.mibo.android.core.gson.GsonConverterFactory
import life.mibo.android.ui.main.MiboApplication
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

class FitbitApi {

    var okhttp: OkHttpClient? = null

    companion object {
        val request: FitbitApi by lazy { FitbitApi() }
        fun get() = lazy { FitbitApi() }
        const val baseUrl = Fitbit.api


    }

    init {

    }

    fun getClient(): OkHttpClient {
        if (okhttp == null) {
            okhttp = if (MiboApplication.DEBUG) {
                OkHttpClient.Builder().addInterceptor(ChuckerInterceptor(MiboApplication.context!!))
                    .build()
            } else {
                OkHttpClient.Builder().build()
            }
        }

        return okhttp!!

    }


    fun getApi(): FitbitService {
        return Retrofit.Builder().baseUrl(baseUrl).client(okhttp!!)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(FitbitService::class.java)
    }

    fun refresh(refresh: String, client: String, secret: String, runnable: Runnable?) {
        val url = "https://api.fitbit.com/oauth2/token"
        val body = FormBody.Builder()
            .add("grant_type", "refresh_token").add("refresh_token", refresh)
            .add("expires_in", "2592000")
            .build()
        val request = Request.Builder()
            .url(url)
            .method("POST", body)
            .header("Authorization", "Basic $client:$secret")
            .addHeader("Accept-Language", "en_GB")
            .build()

        val call = getClient().newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {

            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        runnable?.run()
                    }
                } catch (e: Exception) {

                }
            }
        })

        //getApi().getSteps(date).enqueue(callback)
    }

    fun getSteps(date: String, token: String, callback: okhttp3.Callback) {
        val url = Fitbit.api + "-/activities/steps/date/$date/1d/15min.json"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .addHeader("Accept-Language", "en_GB")
            .build()

        val call = getClient().newCall(request)
        call.enqueue(callback)

        //getApi().getSteps(date).enqueue(callback)
    }

    fun getSteps(startDate: String, endDate: String, token: String, callback: okhttp3.Callback) {
        val url = Fitbit.api + "-/activities/steps/date/$startDate/$endDate/15min.json"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .addHeader("Accept-Language", "en_GB")
            .build()

        val call = getClient().newCall(request)
        call.enqueue(callback)

    }

    fun getHeartPoints(
        startDate: String,
        endDate: String,
        token: String,
        callback: okhttp3.Callback
    ) {
        val url = Fitbit.api + "-/activities/steps/date/$startDate/$endDate/15min.json"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .addHeader("Accept-Language", "en_GB")
            .build()

        val call = getClient().newCall(request)
        call.enqueue(callback)

    }


    interface FitbitService {

        @GET("user/-/profile.json")
        fun getProfile(): Deferred<JsonArray>

        @GET("user/-/devices.json")
        fun listDevices(): Deferred<List<JsonArray>>

        @GET(
            "user/-/activities/heart/" +
                    "date/{date}/{end-date}/" +
                    "{detail-level}/" +
                    "time/{start-time}/{end-time}.json"
        )

        fun getHrData(
            @Path("date") startDate: String,
            @Path("start-time") startTime: String,
            @Path("end-date") endDate: String,
            @Path("end-time") endTime: String,
            @Path("detail-level") detailLevel: String
        ): Deferred<JsonArray>

        @GET(
            "user/-/activities/calories/" +
                    "date/{date}/{end-date}/" +
                    "{detail-level}/" +
                    "time/{start-time}/{end-time}.json"
        )
        fun getCaloriesData(
            @Path("date") startDate: String,
            @Path("start-time") startTime: String,
            @Path("end-date") endDate: String,
            @Path("end-time") endTime: String,
            @Path("detail-level") detailLevel: String
        ): Deferred<JsonArray>

        @GET(
            "user/-/activities/steps/" +
                    "date/{date}/{end-date}/" +
                    "{detail-level}/" +
                    "time/{start-time}/{end-time}.json"
        )
        fun getStepsData(
            @Path("date") startDate: String,
            @Path("start-time") startTime: String,
            @Path("end-date") endDate: String,
            @Path("end-time") endTime: String,
            @Path("detail-level") detailLevel: String
        ): Deferred<JsonArray>


        @GET("-/activities/steps/date/{date}/1d/15min.json")
        fun getSteps(@Path("date") startDate: String): Call<DailySteps>

        @GET("-/activities/steps/date/{start}/{end}/15min.json")
        fun getSteps(@Path("start") start: String, @Path("end") end: String): Call<StepsData>
    }

}