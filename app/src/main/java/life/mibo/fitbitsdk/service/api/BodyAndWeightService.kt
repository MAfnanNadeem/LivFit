package life.mibo.fitbitsdk.service.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface BodyAndWeightService {

    /**
     * Log Body Fat
     * Creates a log entry for body fat and returns a response in the format requested.
     * @param fat Body fat in the format of X.XX in the unit system that corresponds to the Accept-Language header provided. (required)
     * @param date Log entry date in the format yyyy-MM-dd. (required)
     * @param time Time of the measurement in hours and minutes in the format HH:mm:ss that is set to the last second of the day if not provided. (required)
     * @return Call&lt;Void&gt;
     */
    @POST("1/user/-/body/log/fat.json")
    fun weight(
            @retrofit2.http.Query("fat") fat: Int?, @retrofit2.http.Query("date") date: String, @retrofit2.http.Query("time") time: String
    ): Call<Void>

    /**
     * Get Body Fat Logs
     * Retreives a list of all user&#39;s body fat log entries for a given day in the format requested.
     * @param baseDate The range start date in the format yyyy-MM-dd or today. (required)
     * @param endDate The end date of the range. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/log/fat/date/{base-date}/{end-date}.json")
    fun weight_0(
            @retrofit2.http.Path("base-date") baseDate: String, @retrofit2.http.Path("end-date") endDate: String
    ): Call<String>

    /**
     * Get Body Fat Logs
     * Retreives a list of all user&#39;s body fat log entries for a given day in the format requested.
     * @param date The date in the format yyyy-MM-dd. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/log/fat/date/{date}.json")
    fun weight_1(
            @retrofit2.http.Path("date") date: String
    ): Call<Void>

    /**
     * Delete Weight Log
     * Deletes a user&#39;s body weight log entrywith the given ID.
     * @param bodyWeightLogId The ID of the body weight log entry. (required)
     * @return Call&lt;Void&gt;
     */
    @DELETE("1/user/-/body/log/weight/{body-weight-log-id}.json")
    fun weight_10(
            @retrofit2.http.Path("body-weight-log-id") bodyWeightLogId: Int?
    ): Call<Void>

    /**
     * Get Body Goals
     * Retreives a user&#39;s current body fat percentage or weight goal using units in the unit systems that corresponds to the Accept-Language header providedin the format requested.
     * @param goalType weight or fat. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/log/{goal-type}/goal.json")
    fun weight_11(
            @retrofit2.http.Path("goal-type") goalType: String
    ): Call<Void>

    /**
     * Get Body Fat Logs
     * Retreives a list of all user&#39;s body fat log entries for a given day in the format requested.
     * @param date The date in the format yyyy-MM-dd. (required)
     * @param period The range for which data will be returned. Options are 1d, 7d, 30d, 1w, 1m, 3m, 6m, 1y, or max. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/log/fat/date/{date}/{period}.json")
    fun weight_2(
            @retrofit2.http.Path("date") date: String, @retrofit2.http.Path("period") period: String
    ): Call<Void>

    /**
     * Update Body Fat Goal
     * Updates user&#39;s fat percentage goal.
     * @param fat Target body fat percentage; in the format X.XX. (required)
     * @return Call&lt;Void&gt;
     */
    @POST("1/user/-/body/log/fat/goal.json")
    fun weight_3(
            @retrofit2.http.Query("fat") fat: String
    ): Call<Void>

    /**
     * Delete Body Fat Log
     * Deletes a user&#39;s body fat log entry with the given ID.
     * @param bodyFatLogId The ID of the body fat log entry. (required)
     * @return Call&lt;Void&gt;
     */
    @DELETE("1/user/-/body/log/fat/{body-fat-log-id}.json")
    fun weight_4(
            @retrofit2.http.Path("body-fat-log-id") bodyFatLogId: Int?
    ): Call<Void>

    /**
     * Log Weight
     * Creates log entry for a body weight using units in the unit systems that corresponds to the Accept-Language header provided and gets a response in the format requested.
     * @param weight Weight in the format of X.XX. (required)
     * @param date Log entry date in the format yyyy-MM-dd. (required)
     * @param time Time of the measurement; hours and minutes in the format of HH:mm:ss, which is set to the last second of the day if not provided. (optional)
     * @return Call&lt;Void&gt;
     */
    @POST("1/user/-/body/log/weight.json")
    fun weight_5(
            @retrofit2.http.Query("weight") weight: Int?, @retrofit2.http.Query("date") date: String, @retrofit2.http.Query("time") time: String
    ): Call<Void>

    /**
     * Get Body Fat Logs
     * Retreives a list of all user&#39;s body fat log entries for a given day in the format requested.
     * @param baseDate The range start date in the format yyyy-MM-dd or today. (required)
     * @param endDate The end date of the range. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/log/weight/date/{base-date}/{end-date}.json")
    fun weight_6(
            @retrofit2.http.Path("base-date") baseDate: String, @retrofit2.http.Path("end-date") endDate: String
    ): Call<Void>

    /**
     * Get Weight Logs
     * Retreives a list of all user&#39;s body weight log entries for a given day using units in the unit systems which corresponds to the Accept-Language header provided.
     * @param date The date in the format yyyy-MM-dd. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/log/weight/date/{date}.json")
    fun weight_7(
            @retrofit2.http.Path("date") date: String
    ): Call<Void>

    /**
     * Get Body Fat Logs
     * Retreives a list of all user&#39;s body weight log entries for a given day in the format requested.
     * @param date The date in the format yyyy-MM-dd. (required)
     * @param period The range for which data will be returned. Options are 1d, 7d, 30d, 1w, 1m, 3m, 6m, 1y, or max. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/log/weight/date/{date}/{period}.json")
    fun weight_8(
            @retrofit2.http.Path("date") date: String, @retrofit2.http.Path("period") period: String
    ): Call<Void>

    /**
     * Update Weight Goal
     * Updates user&#39;s fat percentage goal.
     * @param startDate Weight goal start date; in the format yyyy-MM-dd. (required)
     * @param startWeight Weight goal start weight; in the format X.XX, in the unit systems that corresponds to the Accept-Language header provided. (required)
     * @param weight Weight goal target weight; in the format X.XX, in the unit systems that corresponds to the Accept-Language header provided; required if user doesn&#39;t have an existing weight goal. (optional)
     * @return Call&lt;Void&gt;
     */
    @POST("1/user/-/body/log/weight/goal.json")
    fun weight_9(
            @retrofit2.http.Query("startDate") startDate: String, @retrofit2.http.Query("startWeight") startWeight: String, @retrofit2.http.Query("weight") weight: String
    ): Call<Void>

}