package life.mibo.android.core

import com.chuckerteam.chucker.api.ChuckerInterceptor
import life.mibo.android.core.gson.GsonConverterFactory
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.base.PostData
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.calories.Calories
import life.mibo.android.models.circuits.CircuitResponse
import life.mibo.android.models.circuits.SearchCircuit
import life.mibo.android.models.create_session.BookSession
import life.mibo.android.models.create_session.BookSessionPost
import life.mibo.android.models.create_session.SaveSessionPost
import life.mibo.android.models.login.LoginResponse
import life.mibo.android.models.login.LoginUser
import life.mibo.android.models.member.Avatar
import life.mibo.android.models.member.ChangePassword
import life.mibo.android.models.member.ChangePasswordResponse
import life.mibo.android.models.member.MemberAvatar
import life.mibo.android.models.muscle.GetSuitPost
import life.mibo.android.models.muscle.GetSuits
import life.mibo.android.models.muscle.MuscleCollection
import life.mibo.android.models.product.Products
import life.mibo.android.models.program.ProgramPost
import life.mibo.android.models.program.SearchPrograms
import life.mibo.android.models.register.RegisterMember
import life.mibo.android.models.register.RegisterResponse
import life.mibo.android.models.rxl.*
import life.mibo.android.models.send_otp.SendOTP
import life.mibo.android.models.send_otp.SendOtpResponse
import life.mibo.android.models.session.SessionDetails
import life.mibo.android.models.session.SessionReport
import life.mibo.android.models.user_details.UserDetails
import life.mibo.android.models.user_details.UserDetailsPost
import life.mibo.android.models.verify_otp.VerifyOTP
import life.mibo.android.models.verify_otp.VerifyOtpResponse
import life.mibo.android.models.weight.WeightAll
import life.mibo.android.models.weight.WeightAllResponse
import life.mibo.android.ui.main.MiboApplication
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
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

        okhttp = if (MiboApplication.DEBUG) {
            OkHttpClient.Builder().addInterceptor(ChuckerInterceptor(MiboApplication.context!!))
                .addInterceptor(getLogger())
                .cache(getCache())
                .build()
        } else {
            OkHttpClient.Builder().cache(getCache()).build()
        }

    }


    private var okhttp: OkHttpClient? = null

    private val cacheSize: Long = 50 * 1024 * 1024 // 50MB
    private fun getCache(): Cache? {
        MiboApplication.context?.cacheDir?.let {
            Logger.e("MiboApplication Cache not null")
            return Cache(it, cacheSize)
        }
        return null
    }

    class ForceCacheInterceptor : Interceptor {
        @Throws(Exception::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val builder: Request.Builder = chain.request().newBuilder()
            if (!Utils.isConnected(MiboApplication.context)) {
                builder.cacheControl(CacheControl.FORCE_CACHE)
            }
            return chain.proceed(builder.build())
        }
    }

    fun getLogger(): Interceptor {
        val i = HttpLoggingInterceptor()
        i.level = HttpLoggingInterceptor.Level.BODY
        return i
    }
    companion object {
        val request: API by lazy { API() }
        fun get() = lazy { API() }

        //val baseUrl = "https://os.mibo.world/api/v1/"
        //http://test.mibo.world/api/v1/
        const val baseUrl = "http://test.mibo.world/api/consumer/"
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

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("registerUser")
        fun register(@Body data: RegisterMember): Call<RegisterResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("loginUser")
        fun login(@Body login: LoginUser): Call<LoginResponse>

//        @Headers("Accept: application/json", "Content-Type: application/json")
//        @POST("loginMember")
//        fun loginMember(@Body data: LoginData): Call<Member>

//        @Headers("Accept: application/json", "Content-Type: application/json")
//        @POST("verifyNumber")
//        fun verifyNumber(@Body data: VerifyNumber): Call<VerifyResponse>


        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("sentOTP")
        fun sendOtp(@Body data: SendOTP): Call<SendOtpResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("verifyOTP")
        fun verifyOtp(@Body data: VerifyOTP): Call<VerifyOtpResponse>

        //@Headers("Accept: application/json", "Content-Type: application/json")
        //@POST("verifyOTP")
        //fun changePassword(@Body data: ChangePassword): Call<VerifyOtpResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getLatestSessionReports")
        fun getSessionDetails(@Body data: SessionDetails): Call<SessionReport>

//        @Headers("Accept: application/json", "Content-Type: application/json")
//        @POST("getLatestSessionDetails")
//        fun getProducts(@Body data: SessionDetails): Call<SessionReport>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getAllWeight")
        fun getAllWeight(@Body data: WeightAll): Call<WeightAllResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("memberDetails")
        fun userDetails(@Body data: UserDetailsPost): Call<UserDetails>

//        @Headers("Accept: application/json", "Content-Type: application/json")
//        @POST("userDetails")
//        fun memberDetails(@Body data: UserDetailsPost): Call<UserDetails>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getAllCaloriesBurnt")
        fun getAllCaloriesBurnt(@Body data: PostData): Call<Calories>

//        @Headers("Accept: application/json", "Content-Type: application/json")
//        @POST("getProgram")
//        fun getProgram(@Body data: PostData): Call<Calories>

//        @Headers("Accept: application/json", "Content-Type: application/json")
//        @POST("searchPrograms")
//        fun searchPrograms(@Body data: ProgramPost2): Call<SearchPrograms2>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("searchPrograms")
        fun searchPrograms2(@Body data: ProgramPost): Call<SearchPrograms>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("bookAndStartConsumerSession")
        fun bookSession(@Body data: BookSessionPost): Call<BookSession>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveSessionReport")
        fun saveSessionReport(@Body data: SaveSessionPost): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("searchCircuit")
        fun getCircuits(@Body data: SearchCircuit): Call<CircuitResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveRXLProgram")
        fun saveRXLProgram(@Body data: SaveRXLProgram): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getRXLProgram")
        fun getRXLProgram(@Body data: GetRXLProgram): Call<RXLPrograms>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("deleteRXLProgram")
        fun deleteRXLProgram(@Body data: DeleteRXLProgram): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("memberAvatar")
        fun uploadAvatar(@Body data: MemberAvatar): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getRXLExerciseProgram")
        fun getRXLExerciseProgram(@Body data: MemberPost): Call<RxlExercises>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveRXLExerciseProgram")
        fun saveRXLExerciseProgram(@Body data: SaveRxlExercise): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("deleteRXLExerciseProgram")
        fun deleteRXLExerciseProgram(@Body data: DeleteRxlExercise): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getProductList")
        fun getProductList(@Body data: MemberPost): Call<Products>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getMuscleCollection")
        fun getMuscleCollection(@Body data: MemberPost): Call<MuscleCollection>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getSuits")
        fun getSuits(@Body data: GetSuitPost): Call<GetSuits>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("memberAvatar")
        fun memberAvatar(@Body data: Avatar): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("changePassword")
        fun changePassword(@Body data: ChangePassword): Call<ChangePasswordResponse>
    }
}