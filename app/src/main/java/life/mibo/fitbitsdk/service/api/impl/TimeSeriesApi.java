/*
 *  Created by Sumeet Kumar on 7/6/20 3:22 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/6/20 3:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.fitbitsdk.service.api.impl;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TimeSeriesApi {
    /**
     * Get Heart Rate Time Series
     * Returns the time series data in the specified range for a given resource in the format requested using units in the unit systems that corresponds to the Accept-Language header provided.
     *
     * @param baseDate The range start date in  the format yyyy-MM-dd or today. (required)
     * @param endDate  The end date of the range. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/heart/date/{base-date}/{end-date}.json")
    Call<Void> UserActivitiesHeartDateBaseDateEndDateJsonGet(
            @retrofit2.http.Path("base-date") String baseDate, @retrofit2.http.Path("end-date") String endDate
    );

    /**
     * Get Body Time Series
     * Returns time series data in the specified range for a given resource in the format requested using units in the unit system that corresponds to the Accept-Language header provided.
     *
     * @param resourcePath The resource path, which incudes the bmi, fat, or weight options. (required)
     * @param baseDate     The range start date in the format yyyy-MM-dd or today. (required)
     * @param endDate      The end date of the range. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/{resource-path}/date/{base-date}/{end-date}.json")
    Call<Void> UserBodyResourcePathDateBaseDateEndDateJsonGet(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("base-date") String baseDate, @retrofit2.http.Path("end-date") String endDate
    );

    /**
     * Get Food or Water Time Series
     * Updates a user&#39;s daily activity goals and returns a response using units in the unit system which corresponds to the Accept-Language header provided.
     *
     * @param resourcePath The resouce path. See options in the Resouce Path Options section in the full documentation. (required)
     * @param date         The end date of the period specified in the format yyyy-MM-dd or today. (required)
     * @param period       The range for which data will be returned. Options are 1d, 7d, 30d, 1w, 3m, 6m, 1y, or max. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/foods/log/{resource-path}/date/{date}/{period}.json")
    Call<Void> UserFoodsLogResourcePathDateDatePeriodJsonGet(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("date") String date, @retrofit2.http.Path("period") String period
    );

    /**
     * Get Heart Rate Intraday Time Series
     * Returns the intraday time series for a given resource in the format requested. If your application has the appropriate access, your calls to a time series endpoint for a specific day (by using start and end dates on the same day or a period of 1d), the response will include extended intraday values with a one-minute detail level for that day. Unlike other time series calls that allow fetching data of other users, intraday data is available only for and to the authorized user.
     *
     * @param date        The date in the format of yyyy-MM-dd or today. (required)
     * @param detailLevel The number of data points to include either 1sec or 1min. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/heart/date/{date}/1d/{detail-level}.json")
    Call<Void> intraday(
            @retrofit2.http.Path("date") String date, @retrofit2.http.Path("detail-level") String detailLevel
    );

    /**
     * Get Heart Rate Intraday Time Series
     * Returns the intraday time series for a given resource in the format requested. If your application has the appropriate access, your calls to a time series endpoint for a specific day (by using start and end dates on the same day or a period of 1d), the response will include extended intraday values with a one-minute detail level for that day. Unlike other time series calls that allow fetching data of other users, intraday data is available only for and to the authorized user.
     *
     * @param date        The date in the format of yyyy-MM-dd or today. (required)
     * @param detailLevel The number of data points to include either 1sec or 1min. (required)
     * @param startTime   The start of the period in the format of HH:mm. (required)
     * @param endTime     The end time of the period in the format of HH:mm. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/heart/date/{date}/1d/{detail-level}/time/{start-time}/{end-time}.json")
    Call<Void> intraday_0(
            @retrofit2.http.Path("date") String date, @retrofit2.http.Path("detail-level") String detailLevel, @retrofit2.http.Path("start-time") String startTime, @retrofit2.http.Path("end-time") String endTime
    );

    /**
     * Get Heart Rate Intraday Time Series
     * Returns the intraday time series for a given resource in the format requested. If your application has the appropriate access, your calls to a time series endpoint for a specific day (by using start and end dates on the same day or a period of 1d), the response will include extended intraday values with a one-minute detail level for that day. Unlike other time series calls that allow fetching data of other users, intraday data is available only for and to the authorized user.
     *
     * @param date        The date in the format of yyyy-MM-dd or today. (required)
     * @param endDate     The end date in the format of yyyy-MM-dd or today. (required)
     * @param detailLevel The number of data points to include either 1sec or 1min. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/heart/date/{date}/{end-date}/{detail-level}.json")
    Call<Void> intraday_1(
            @retrofit2.http.Path("date") String date, @retrofit2.http.Path("end-date") String endDate, @retrofit2.http.Path("detail-level") String detailLevel
    );

    /**
     * Get Intraday Time Series
     * Returns the Intraday Time Series for a given resource in the format requested.
     *
     * @param resourcePath The resource-path; see options in the Resource Path Options section in the full documentation. (required)
     * @param date         The date in the format yyyy-MM-dd or today. (required)
     * @param endDate      The date in the format yyyy-MM-dd or today. (required)
     * @param detailLevel  Number of data points to include. Either 1min or 15min. (required)
     * @param startTime    The start of the period in the format HH:mm. (required)
     * @param endTime      The end of the period in the format HH:mm. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/{resource-path}/date/{date}/{end-date}/{detail-level}/time/{start-time}/{end-time}.json")
    Call<Void> intraday_10(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("date") String date, @retrofit2.http.Path("end-date") String endDate, @retrofit2.http.Path("detail-level") String detailLevel, @retrofit2.http.Path("start-time") String startTime, @retrofit2.http.Path("end-time") String endTime
    );

    /**
     * Get Activity Time Series
     * Returns time series data in the specified range for a given resource in the format requested using units in the unit system that corresponds to the Accept-Language header provided.
     *
     * @param resourcePath The resource-path; see options in the Resource Path Options section in the full documentation. (required)
     * @param date         The end date of the period specified in the format yyyy-MM-dd or today. (required)
     * @param period       The range for which data will be returned. Options are 1d, 7d, 30d, 1w, 1m, 3m, 6m, 1y, or max. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/{resource-path}/date/{date}/{period}.json")
    Call<Void> intraday_11(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("date") String date, @retrofit2.http.Path("period") String period
    );

    /**
     * Get Body Time Series
     * Returns time series data in the specified range for a given resource in the format requested using units in the unit system that corresponds to the Accept-Language header provided.
     *
     * @param resourcePath The resource path, which incudes the bmi, fat, or weight options. (required)
     * @param date         The range start date in the format yyyy-MM-dd or today. (required)
     * @param period       The range for which data will be returned. Options are 1d, 7d, 30d, 1w, 1m, 3m, 6m, 1y, or max. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/body/{resource-path}/date/{date}/{period}.json")
    Call<Void> intraday_12(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("date") String date, @retrofit2.http.Path("period") String period
    );

    /**
     * Get Food or Water Time Series
     * Updates a user&#39;s daily activity goals and returns a response using units in the unit system which corresponds to the Accept-Language header provided.
     *
     * @param resourcePath The resouce path. See options in the Resouce Path Options section in the full documentation. (required)
     * @param baseDate     The range start date in the format yyyy-MM-dd or today. (required)
     * @param endDate      The end date of the range. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/foods/log/{resource-path}/date/{base-date}/{end-date}.json")
    Call<Void> intraday_13(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("base-date") String baseDate, @retrofit2.http.Path("end-date") String endDate
    );

    /**
     * Get Sleep Time Series
     * Returns time series data in the specified range for a given resource in the format requested using units in the unit system that corresponds to the Accept-Language header provided.
     *
     * @param resourcePath The resource path; see the Resource Path Options in the full documentation. (required)
     * @param baseDate     The range start date in the format yyyy-MM-dd or today. (required)
     * @param endDate      The end date of the range. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/sleep/{resource-path}/date/{base-date}/{end-date}.json")
    Call<Void> intraday_14(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("base-date") String baseDate, @retrofit2.http.Path("end-date") String endDate
    );

    /**
     * Get Sleep Time Series
     * Returns time series data in the specified range for a given resource in the format requested using units in the unit system that corresponds to the Accept-Language header provided.
     *
     * @param resourcePath The resource path; see the Resource Path Options in the full documentation. (required)
     * @param date         The end date of the period specified in the format yyyy-MM-dd or today. (required)
     * @param period       The range for which data will be returned. Options are 1d, 7d, 30d, 1w, 1m, 3m, 6m, 1y, or max. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/sleep/{resource-path}/date/{date}/{period}.json")
    Call<Void> intraday_15(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("date") String date, @retrofit2.http.Path("period") String period
    );

    /**
     * Get Heart Rate Intraday Time Series
     * Returns the intraday time series for a given resource in the format requested. If your application has the appropriate access, your calls to a time series endpoint for a specific day (by using start and end dates on the same day or a period of 1d), the response will include extended intraday values with a one-minute detail level for that day. Unlike other time series calls that allow fetching data of other users, intraday data is available only for and to the authorized user.
     *
     * @param date        The date in the format of yyyy-MM-dd or today. (required)
     * @param endDate     The end date in the format of yyyy-MM-dd or today. (required)
     * @param detailLevel The number of data points to include either 1sec or 1min. (required)
     * @param startTime   The start of the period in the format of HH:mm. (required)
     * @param endTime     The end time of the period in the format of HH:mm. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/heart/date/{date}/{end-date}/{detail-level}/time/{start-time}/{end-time}.json")
    Call<Void> intraday_2(
            @retrofit2.http.Path("date") String date, @retrofit2.http.Path("end-date") String endDate, @retrofit2.http.Path("detail-level") String detailLevel, @retrofit2.http.Path("start-time") String startTime, @retrofit2.http.Path("end-time") String endTime
    );

    /**
     * Get Heart Rate Time Series
     * Returns the time series data in the specified range for a given resource in the format requested using units in the unit systems that corresponds to the Accept-Language header provided.
     *
     * @param date   The end date of the period specified in the format yyyy-MM-dd or today. (required)
     * @param period The range of which data will be returned. Options are 1d, 7d, 30d, 1w, and 1m. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/heart/date/{date}/{period}.json")
    Call<Void> intraday_3(
            @retrofit2.http.Path("date") String date, @retrofit2.http.Path("period") String period
    );


    @GET("1/user/-/activities/heart/date/{date}/1d.json")
    Call<Void> getHeartRateDay(@retrofit2.http.Path("date") String date);

    /**
     * Get Activity Time Series
     * Returns time series data in the specified range for a given resource.
     *
     * @param resourcePath The resource-path; see options in the Resource Path Options section in the full documentation. (required)
     * @param baseDate     The range start date in the format yyyy-MM-dd or today. (required)
     * @param endDate      The end date of the range. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/tracker/{resource-path}/date/{base-date}/{end-date}.json")
    Call<Void> intraday_4(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("base-date") String baseDate, @retrofit2.http.Path("end-date") String endDate
    );

    /**
     * Get Activity Time Series
     * Returns time series data in the specified range for a given resource in the format requested using units in the unit system that corresponds to the Accept-Language header provided.
     *
     * @param resourcePath The resource-path; see options in the Resource Path Options section in the full documentation. (required)
     * @param date         The end date of the period specified in the format yyyy-MM-dd or today. (required)
     * @param period       The range for which data will be returned. Options are 1d, 7d, 30d, 1w, 1m, 3m, 6m, 1y, or max. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/tracker/{resource-path}/date/{date}/{period}.json")
    Call<Void> intraday_5(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("date") String date, @retrofit2.http.Path("period") String period
    );

    /**
     * Get Activity Time Series
     * Returns time series data in the specified range for a given resource.
     *
     * @param resourcePath The resource-path; see options in the Resource Path Options section in the full documentation. (required)
     * @param baseDate     The range start date in the format yyyy-MM-dd or today. (required)
     * @param endDate      The end date of the range. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/{resource-path}/date/{base-date}/{end-date}.json")
    Call<Void> intraday_6(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("base-date") String baseDate, @retrofit2.http.Path("end-date") String endDate
    );

    /**
     * Get Intraday Time Series
     * Returns the Intraday Time Series for a given resource in the format requested.
     *
     * @param resourcePath The resource-path; see options in the Resource Path Options section in the full documentation. (required)
     * @param baseDate     The date in the format yyyy-MM-dd or today. (required)
     * @param endDate      The date in the format yyyy-MM-dd or today. (required)
     * @param detailLevel  Number of data points to include. Either 1min or 15min. Optional. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/{resource-path}/date/{base-date}/{end-date}/{detail-level}.json")
    Call<Void> intraday_7(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("base-date") String baseDate, @retrofit2.http.Path("end-date") String endDate, @retrofit2.http.Path("detail-level") String detailLevel
    );

    /**
     * Get Intraday Time Series
     * Returns the Intraday Time Series for a given resource in the format requested.
     *
     * @param resourcePath The resource-path; see options in the Resource Path Options section in the full documentation. (required)
     * @param date         The date in the format yyyy-MM-dd or today. (required)
     * @param detailLevel  Number of data points to include. Either 1min or 15min. Optional. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/{resource-path}/date/{date}/1d/{detail-level}.json")
    Call<Void> intraday_8(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("date") String date, @retrofit2.http.Path("detail-level") String detailLevel
    );

    /**
     * Get Intraday Time Series
     * Returns the Intraday Time Series for a given resource in the format requested.
     *
     * @param resourcePath The resource-path; see options in the Resource Path Options section in the full documentation. (required)
     * @param date         The date in the format yyyy-MM-dd or today. (required)
     * @param detailLevel  Number of data points to include. Either 1min or 15min. (required)
     * @param startTime    The start of the period in the format HH:mm. (required)
     * @param endTime      The end of the period in the format HH:mm. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/{resource-path}/date/{date}/1d/{detail-level}/time/{start-time}/{end-time}.json")
    Call<Void> intraday_9(
            @retrofit2.http.Path("resource-path") String resourcePath, @retrofit2.http.Path("date") String date, @retrofit2.http.Path("detail-level") String detailLevel, @retrofit2.http.Path("start-time") String startTime, @retrofit2.http.Path("end-time") String endTime
    );

}
