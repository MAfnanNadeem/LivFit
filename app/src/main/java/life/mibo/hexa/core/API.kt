package life.mibo.hexa.core

import com.chuckerteam.chucker.api.ChuckerInterceptor
import life.mibo.hexa.MiboApplication
import life.mibo.hexa.core.gson.GsonConverterFactory
import life.mibo.hexa.models.login.LoginData
import life.mibo.hexa.models.login.LoginResponse
import life.mibo.hexa.models.login.LoginUser
import life.mibo.hexa.models.member.Member
import life.mibo.hexa.models.member.MemberDetailsPOST
import life.mibo.hexa.models.register.RegisterGuestMember
import life.mibo.hexa.models.register.RegisterResponse
import life.mibo.hexa.models.send_otp.SendOTP
import life.mibo.hexa.models.send_otp.SendOtpResponse
import life.mibo.hexa.models.session.SessionDetails
import life.mibo.hexa.models.session.SessionReport
import life.mibo.hexa.models.user_details.UserDetails
import life.mibo.hexa.models.user_details.UserDetailsPost
import life.mibo.hexa.models.verify_number.VerifyNumber
import life.mibo.hexa.models.verify_otp.VerifyOtpResponse
import life.mibo.hexa.models.verify_number.VerifyResponse
import life.mibo.hexa.models.verify_otp.VerifyOTP
import life.mibo.hexa.models.weight.WeightAll
import life.mibo.hexa.models.weight.WeightAllResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


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


    private var okhttp: OkHttpClient? = null


    companion object {
        val request: API by lazy { API() }
        //val baseUrl = "https://os.mibo.world/api/v1/"
        //http://test.mibo.world/api/v1/
        const val baseUrl = "http://test.mibo.world/api/v1/"
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
        @POST("registerUser")
        fun register(@Body data: RegisterGuestMember): Call<RegisterResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("loginUser")
        fun login(@Body login: LoginUser): Call<LoginResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("loginMember")
        fun loginMember(@Body data: LoginData): Call<Member>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("verifyNumber")
        fun verifyNumber(@Body data: VerifyNumber): Call<VerifyResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("memberDetails")
        fun memberDetails(@Body data: MemberDetailsPOST): Call<Member>


        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("sentOTP")
        fun sendOtp(@Body data: SendOTP): Call<SendOtpResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("verifyOTP")
        fun verifyOtp(@Body data: VerifyOTP): Call<VerifyOtpResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getLatestSessionDetails")
        fun getSessionDetails(@Body data: SessionDetails): Call<SessionReport>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getLatestSessionDetails")
        fun getProducts(@Body data: SessionDetails): Call<SessionReport>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getAllWeight")
        fun getAllWeight(@Body data: WeightAll): Call<WeightAllResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("userDetails")
        fun userDetails(@Body data: UserDetailsPost): Call<UserDetails>
    }
}