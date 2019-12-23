package life.mibo.hexa.core

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.JsonObject
import life.mibo.hexa.MiboApplication
import life.mibo.hexa.core.gson.GsonConverterFactory
import life.mibo.hexa.models.register.RegisterGuestMember
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*


class API {

    private constructor() {
//        okhttp = OkHttpClient.Builder()
//            .addInterceptor(ChuckerInterceptor(MiboApplication.context!!)).addInterceptor(
//            LoggingInterceptor.Builder()
//                .loggable(BuildConfig.DEBUG)
//                .setLevel(Level.BASIC)
//                .log(Platform.INFO)
//                .request("MIBO-Request")
//                .response("MIBO-Response")
//                .addHeader("MIBO-Version", BuildConfig.VERSION_NAME)
//                .addQueryParam("MIBO-Query", "0").build()
//        ).build()

        okhttp =
            OkHttpClient.Builder().addInterceptor(ChuckerInterceptor(MiboApplication.context!!))
                .build()
    }


    var okhttp: OkHttpClient? = null


    companion object {
        val request: API by lazy { API() }
        //val baseUrl = "https://os.mibo.world/api/v1/"
        //http://test.mibo.world/api/v1/
        val baseUrl = "http://test.mibo.world/api/v1/"
    }

    fun getApi(): ApiService {
//        okhttp?.networkInterceptors.add(object : Interceptor {
//            @Throws(IOException::class)
//            override fun intercept(chain: Interceptor.Chain): Response {
//                val requestBuilder = chain.request().newBuilder()
//                requestBuilder.header("Content-Type", "application/json")
//                return chain.proceed(requestBuilder.build())
//            }
//        });
        return Retrofit.Builder().baseUrl(baseUrl).client(okhttp!!)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }


    interface ApiService {

        @Headers("Accept: application/json" , "Content-Type: application/json")
        @POST("registerGuestMember")
        fun register(@Body data: RegisterGuestMember): Call<JsonObject>

        @Headers("Accept: application/json" , "Content-Type: application/json")
        @POST("login")
        fun login(@Field("username") name : String, @Field("password") pwd : String): Call<JsonObject>
    }
}