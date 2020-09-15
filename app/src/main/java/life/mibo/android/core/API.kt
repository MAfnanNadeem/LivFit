package life.mibo.android.core

import com.chuckerteam.chucker.api.ChuckerInterceptor
import life.mibo.android.core.gson.GsonConverterFactory
import life.mibo.android.models.base.*
import life.mibo.android.models.biometric.Biometric
import life.mibo.android.models.biometric.PostBiometric
import life.mibo.android.models.calories.Calories
import life.mibo.android.models.catalog.*
import life.mibo.android.models.circuits.CircuitResponse
import life.mibo.android.models.circuits.SearchCircuit
import life.mibo.android.models.create_session.BookSession
import life.mibo.android.models.create_session.BookSessionPost
import life.mibo.android.models.create_session.SaveSessionPost
import life.mibo.android.models.login.LoginResponse
import life.mibo.android.models.login.LoginUser
import life.mibo.android.models.login.SocialLoginUser
import life.mibo.android.models.member.*
import life.mibo.android.models.muscle.GetSuitPost
import life.mibo.android.models.muscle.GetSuits
import life.mibo.android.models.muscle.MuscleCollection
import life.mibo.android.models.notification.*
import life.mibo.android.models.password.CreatePassword
import life.mibo.android.models.password.ForgetPasswordPost
import life.mibo.android.models.password.ForgetPasswordVerifyOtp
import life.mibo.android.models.password.PasswordVerifyOTPResponse
import life.mibo.android.models.program.ProgramPost
import life.mibo.android.models.program.SearchPrograms
import life.mibo.android.models.register.RegisterMember
import life.mibo.android.models.register.RegisterResponse
import life.mibo.android.models.rxl.*
import life.mibo.android.models.rxt.*
import life.mibo.android.models.send_otp.SendOTP
import life.mibo.android.models.send_otp.SendOtpResponse
import life.mibo.android.models.session.RequestRescheduleMemberSession
import life.mibo.android.models.session.RescheduleMemberSession
import life.mibo.android.models.session.SessionDetails
import life.mibo.android.models.session.SessionReport
import life.mibo.android.models.trainer.*
import life.mibo.android.models.user_details.UpdateMemberDetails
import life.mibo.android.models.user_details.UserDetails
import life.mibo.android.models.user_details.UserDetailsPost
import life.mibo.android.models.verify_otp.VerifyOTP
import life.mibo.android.models.verify_otp.VerifyOtpResponse
import life.mibo.android.models.weight.CompareMemberWeight
import life.mibo.android.models.weight.CompareWeightResponse
import life.mibo.android.models.weight.WeightAll
import life.mibo.android.models.weight.WeightAllResponse
import life.mibo.android.models.workout.*
import life.mibo.android.ui.main.MiboApplication
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*
import retrofit2.http.Headers
import java.util.concurrent.TimeUnit


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
            OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).cache(getCache()).build()
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
        //const val baseUrl = "http://test.mibo.world/api/consumer/"
        //private val url = "http://test.mibo.world"
        private val url =
            if (MiboApplication.DEV_SERVER) "https://dev.mibolivfit.club" else "https://mibolivfit.club"
        //private const val productionUrl = "https://demo.mibolivfit.club"

        // const val url = "https://mibolivfit.club"
        val baseUrl = "$url/api/consumer/"
        val trainerUrl = "$url/api/v1/"
        val chainUrl = "http://chaintest.mibo.world/api/"
        // val fitbitApi = "http://chaintest.mibo.world/api/"
        // const val fitbitAuth = "http://chaintest.mibo.world/api/"
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


    fun getTrainerApi(): ApiService {
        return Retrofit.Builder().baseUrl(trainerUrl).client(okhttp!!)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }

    fun getChainApi(): ApiService {
        return Retrofit.Builder().baseUrl(chainUrl).client(okhttp!!)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }


    interface ApiService {

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("registerUser")
        fun register(@Body data: RegisterMember): Call<RegisterResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("updateNumber")
        fun updateNumber(@Body data: UpdateNumber): Call<ResponseStatus>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("registerInvitedMember")
        fun registerInvitedMember(@Body data: RegisterMember): Call<RegisterResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("loginUser")
        fun login(@Body login: LoginUser): Call<LoginResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("socialLoginUser")
        fun socialLoginUser(@Body login: SocialLoginUser): Call<LoginResponse>

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

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("forgotPasswordOTP")
        fun forgotPasswordOTP(@Body data: ForgetPasswordPost): Call<ResponseStatus>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("forgotPasswordVerifyOTP")
        fun forgotPasswordVerifyOTP(@Body data: ForgetPasswordVerifyOtp): Call<PasswordVerifyOTPResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("createPassword")
        fun createPassword(@Body data: CreatePassword): Call<ResponseStatus>

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

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("updateMemberDetails")
        fun updateMemberDetails(@Body data: UpdateMemberDetails): Call<ResponseData>

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
        @POST("saveMemberAvatar")
        fun uploadAvatar(@Body data: MemberAvatar): Call<ResponseData>

        @Multipart
        @Headers("Accept: application/json", "Content-Type: multipart/form-data")
        @POST("saveMemberAvatar")
        fun uploadAvatar(
            @Part file: MultipartBody.Part,
            @Part("token") token: RequestBody?,
            @Part("RequestType") request: RequestBody,
            @Part("MemberID") memberId: RequestBody?
        ): Call<ResponseData>

        @Multipart
        @POST("saveMemberAvatar")
        fun uploadAvatar(@PartMap params: HashMap<String, RequestBody?>): Call<SaveMemberAvatar>

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

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveMemberBiometrics")
        fun saveMemberBiometrics(@Body data: PostBiometric): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getMemberBiometrics")
        fun getMemberBiometrics(@Body data: MemberPost): Call<Biometric>


        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("compareMemberWeight")
        fun compareMemberWeight(@Body data: CompareMemberWeight): Call<CompareWeightResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveFirebaseToken")
        fun saveFirebaseToken(@Body data: FirebaseTokenPost): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("searchIndependentProfessionals")
        fun searchProfessionals(@Body data: SearchTrainers): Call<IndependentProfessionals>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("servicesByProfessionals")
        fun getProfessionalDetails(@Body data: GetServicesOfProfessionals): Call<ProfessionalDetails>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("sendInviteRequestToTrainer")
        fun inviteProfessional(@Body data: InviteProfessional): Call<TrainerInviteResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getMemberCalendar")
        fun getMemberCalendar(@Body data: MemberCalendarPost): Call<MemberCalendar>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("rescheduleMemberSession")
        fun rescheduleMemberSession(@Body data: RescheduleMemberSession): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("requestRescheduleMemberSession")
        fun requestRescheduleMemberSession(@Body data: RequestRescheduleMemberSession): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getMemberNotifications")
        fun getMemberNotifications(@Body data: GetMemberNotifications): Call<MemberNotifications>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getTrainerNotifications")
        fun getTrainerNotifications(@Body data: GetTrainerNotifications): Call<TrainerNotifications>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("acceptMemberInvite")
        fun acceptMemberInvite(@Body data: AcceptMemberInvite): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("acceptRescheduleRequest")
        fun acceptRescheduleRequest(@Body data: AcceptRescheduleRequest): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getMemberServices")
        fun getMemberServices(@Body data: GetMemberServices): Call<Services>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getMemberPackages")
        fun getMemberPackages(@Body data: GetMemberServices): Call<Packages>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getMemberShippingAddress")
        fun getMemberShippingAddress(@Body data: MemberPost): Call<ShipmentAddress>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveMemberShippingAddress")
        fun saveMemberShippingAddress(@Body data: SaveShippingAddress): Call<ResponseData>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("updateMemberShippingAddress")
        fun updateMemberShippingAddress(@Body data: SaveShippingAddress): Call<ResponseData>


        //GetAllInvoices
        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getAllInvoice")
        fun getAllInvoice(@Body data: MemberPost): Call<GetInvoices>

        //GetSingleInvoice
        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getInvoice")
        fun getInvoiceDetails(@Body data: GetInvoiceDetail): Call<InvoiceDetails>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveOrderDetails")
        fun saveOrderDetails(@Body data: SaveOrderDetails): Call<ResponseStatus>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("checkPromocode")
        fun checkPromoCode(@Body data: CheckPromo): Call<PromoResponse>


        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("searchWorkoutVideo")
        fun getWorkouts(@Body data: GetWorkout): Call<Workouts>


        // FOR TRAINER
        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getAllTrainerInvoices")
        fun getTrainerInvoices(@Body data: TrainerID): Call<TrainerInvoices>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getIndependentProfessionalServices")
        fun getTrainerServices(@Body data: TrainerID): Call<TrainerServices>

        //GetIndependentProfessionalCustomers
        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getIndependentProfessionalCustomers")
        fun getCustomers(@Body data: TrainerID): Call<TrainerClients>


//        @Headers("Accept: application/json", "Content-Type: application/json")
//        @POST("getAllCaloriesBurnt")
//        fun getAllCaloriesBurnt(@Body data: RequestRescheduleMemberSession): Call<ResponseData>


        // Chain APIs
        @Headers("Accept: application/json", "Content-Type: application/json")
        @GET("productsLivFit")
        fun getChainProducts(@Query("country") country: String): Call<Catalog>


        // TRAINER - INDEPENDENT PROFESSIONAL APIs
        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getTrainerCalendarSession")
        fun getTrainerCalendarSession(@Body data: TrainerCalendarSession): Call<TrainerCalendarResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("startSession")
        fun startTrainerSession(@Body data: StartTrainerSession): Call<ResponseStatus>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveSessionReport")
        fun saveTrainerSession(@Body data: SaveTrainerSessionReport): Call<ResponseStatus>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("memberAttendance")
        fun trainerMemberAttendance(@Body data: TrainerCalendarSession): Call<TrainerCalendarResponse>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("searchPrograms")
        fun trainerSearchPrograms(@Body data: ProgramPost): Call<SearchPrograms>

        // RXT
        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("searchWorkout")
        fun searchWorkout(@Body data: SearchWorkoutPost): Call<SearchWorkout>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveIslandTiles")
        fun saveIslandTiles(@Body data: SaveIslandPost): Call<ResponseStatus>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("updateIslandTiles")
        fun updateIslandTiles(@Body data: SaveIslandPost): Call<ResponseStatus>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getIslandTiles")
        fun getIslandTiles(@Body data: GetIslandPost): Call<GetIslandTiles>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getAllIsland")
        fun getAllIsland(@Body data: GetAllIslandPost): Call<GetAllIslands>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getIslandTilesByLocation")
        fun getIslandsByLocation(@Body data: GetAllIslandPost): Call<GetAllIslandsByLocation>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("saveMemberScores")
        fun saveScore(@Body data: SaveMemberScores): Call<ResponseStatus>

        @Headers("Accept: application/json", "Content-Type: application/json")
        @POST("getMemberScores")
        fun getScore(@Body data: GetMemberScores): Call<GetMemberScoresReport>


    }
}