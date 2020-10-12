package life.mibo.fitbitsdk.service.api

import life.mibo.fitbitsdk.service.models.UserProfile
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    /**
     * Get Badges
     * Retrieves the user&#39;s badges in the format requested. Response includes all badges for the user as seen on the Fitbit website badge locker (both activity and weight related.) The endpoint returns weight and distance badges based on the user&#39;s unit profile preference as on the website.
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/badges.json")
    fun badges(): Call<Void>


    /**
     * Get Profile
     * Returns a user&#39;s profile. The authenticated owner receives all values. However, the authenticated user&#39;s access to other users&#39; data is subject to those users&#39; privacy settings. Numerical values are returned in the unit system specified in the Accept-Language header.
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/profile.json")
    fun profile(): Call<UserProfile>


    /**
     * Update Profile
     * Updates a user&#39;s profile using a form.
     * @param gender The sex of the user; (MALE/FEMALE/NA). (optional)
     * @param birthday The date of birth in the format of yyyy-MM-dd. (optional)
     * @param height The height in the format X.XX in the unit system that corresponds to the Accept-Language header provided. (optional)
     * @param aboutMe This is a short description of user. (optional)
     * @param fullname The fullname of the user. (optional)
     * @param country The country of the user&#39;s residence. This is a two-character code. (optional)
     * @param state The US state of the user&#39;s residence. This is a two-character code if the country was or is set to US. (optional)
     * @param city The US city of the user&#39;s residence. (optional)
     * @param strideLengthWalking Walking stride length in the format X.XX, where it is in the unit system that corresponds to the Accept-Language header provided. (optional)
     * @param strideLengthRunning Running stride length in the format X.XX, where it is in the unit system that corresponds to the Accept-Language header provided. (optional)
     * @param weightUnit Default weight unit on website (which doesn&#39;t affect API); one of (en_US, en_GB, &#39;any&#39; for METRIC). (optional)
     * @param heightUnit Default height/distance unit on website (which doesn&#39;t affect API); one of (en_US, en_GB, &#39;any&#39; for METRIC). (optional)
     * @param waterUnit Default water unit on website (which doesn&#39;t affect API); one of (en_US, en_GB, &#39;any&#39; for METRIC). (optional)
     * @param glucoseUnit Default glucose unit on website (which doesn&#39;t affect API); one of (en_US, en_GB, &#39;any&#39; for METRIC). (optional)
     * @param timezone The timezone in the format &#39;America/Los_Angeles&#39;. (optional)
     * @param foodsLocale The food database locale in the format of xx.XX. (optional)
     * @param locale The locale of the website (country/language); one of the locales, currently – (en_US, fr_FR, de_DE, es_ES, en_GB, en_AU, en_NZ, ja_JP). (optional)
     * @param localeLang The Language in the format &#39;xx&#39;. You should specify either locale or both - localeLang and localeCountry (locale is higher priority). (optional)
     * @param localeCountry The Country in the format &#39;xx&#39;. You should specify either locale or both - localeLang and localeCountry (locale is higher priority). (optional)
     * @param startDayOfWeek The Start day of the week, meaning what day the week should start on. Either Sunday or Monday. (optional)
     * @return Call&lt;Void&gt;
     */
    @retrofit2.http.Multipart
    @POST("1/user/-/profile.json")
    fun updateProfile(
            @retrofit2.http.Part("gender") gender: String, @retrofit2.http.Part("birthday") birthday: String, @retrofit2.http.Part("height") height: String, @retrofit2.http.Part("aboutMe") aboutMe: String, @retrofit2.http.Part("fullname") fullname: String, @retrofit2.http.Part("country") country: String, @retrofit2.http.Part("state") state: String, @retrofit2.http.Part("city") city: String, @retrofit2.http.Part("strideLengthWalking") strideLengthWalking: String, @retrofit2.http.Part("strideLengthRunning") strideLengthRunning: String, @retrofit2.http.Part("weightUnit") weightUnit: String, @retrofit2.http.Part("heightUnit") heightUnit: String, @retrofit2.http.Part("waterUnit") waterUnit: String, @retrofit2.http.Part("glucoseUnit") glucoseUnit: String, @retrofit2.http.Part("timezone") timezone: String, @retrofit2.http.Part("foodsLocale") foodsLocale: String, @retrofit2.http.Part("locale") locale: String, @retrofit2.http.Part("localeLang") localeLang: String, @retrofit2.http.Part("localeCountry") localeCountry: String, @retrofit2.http.Part("startDayOfWeek") startDayOfWeek: String
    ): Call<UserProfile>

}
