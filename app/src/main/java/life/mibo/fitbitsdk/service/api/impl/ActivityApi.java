/*
 *  Created by Sumeet Kumar on 7/6/20 3:21 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/6/20 3:18 PM
 *  Mibo Hexa - app
 */

package life.mibo.fitbitsdk.service.api.impl;

import life.mibo.fitbitsdk.service.models.ActivityCategory;
import life.mibo.fitbitsdk.service.models.ActivityLog;
import life.mibo.fitbitsdk.service.models.ActivitySteps;
import life.mibo.fitbitsdk.service.models.ActivityType;
import life.mibo.fitbitsdk.service.models.DailyActivitySummary;
import life.mibo.fitbitsdk.service.models.LifetimeActivities;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ActivityApi {

    /**
     * Get Daily Activity Summary
     * Retrieves a summary and list of a user&#39;s activities and activity log entries for a given day.
     *
     * @param date The date in the format yyyy-MM-dd (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/date/{date}.json")
    Call<DailyActivitySummary> getDailyActivitySummary(@retrofit2.http.Path("date") String date);

    /**
     * Browse Activity Types
     * Retreives a tree of all valid Fitbit public activities from the activities catelog as well as private custom activities the user created in the format requested.
     *
     * @return Call&lt;Void&gt;
     */
    @GET("1/activities.json")
    Call<ActivityType> browseActivityTypes();


    /**
     * get Steps for date range
     * Retrieves the steps for each date in the the date range
     *
     * @param startDate in the format yyyy-MM-dd (required)
     * @param endDate   in the format yyyy-MM-dd (required)
     */
    @GET("/1/user/-/activities/steps/date/{start_date}/{end_date}.json")
    Call<ActivitySteps> getActivityStepsForDateRange(
            @retrofit2.http.Path("start_date") String startDate,
            @retrofit2.http.Path("end_date") String endDate);


    @GET("/1/user/-/activities/steps/date/{start_date}/{end_date}.json")
    Call<ActivitySteps> getActivityStepsDaily(
            @retrofit2.http.Path("start_date") String startDate,
            @retrofit2.http.Path("end_date") String endDate);

    /**
     * Get Activity Type
     * Returns the detail of a specific activity in the Fitbit activities database in the format requested. If activity has levels, it also returns a list of activity level details.
     *
     * @param activityId The activity ID. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/activities/{activity-id}.json")
    Call<ActivityCategory> getActivityType(
            @retrofit2.http.Path("activity-id") String activityId
    );

    /**
     * Get Lifetime Stats
     * Updates a user&#39;s daily activity goals and returns a response using units in the unit system which corresponds to the Accept-Language header provided.
     *
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities.json")
    Call<LifetimeActivities> getLifetimeStats();


    /**
     * Get Recent Activity Types
     * Retreives a list of a user&#39;s recent activities types logged with some details of the last activity log of that type using units in the unit system which corresponds to the Accept-Language header provided.
     *
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/recent.json")
    Call<Void> activity_10();


    /**
     * Delete Activity Log
     * Deletes a user&#39;s activity log entry with the given ID.
     *
     * @param activityLogId The id of the activity log entry. (required)
     * @return Call&lt;Void&gt;
     */
    @DELETE("1/user/-/activities/{activity-log-id}.json")
    Call<Void> activity_11(
            @retrofit2.http.Path("activity-log-id") Integer activityLogId
    );

    /**
     * Get Activity TCX
     * Retreives the details of a user&#39;s location and heart rate data during a logged exercise activity.
     *
     * @param logId             The activity&#39;s log ID. (required)
     * @param includePartialTCX Include TCX points regardless of GPS data being present (optional)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/{log-id}.tcx")
    Call<Void> activity_12(
            @retrofit2.http.Path("log-id") String logId, @retrofit2.http.Query("includePartialTCX") Boolean includePartialTCX
    );

    /**
     * Log Activity
     * The Log Activity endpoint creates log entry for an activity or user&#39;s private custom activity using units in the unit system which corresponds to the Accept-Language header provided (or using optional custom distanceUnit) and get a response in the format requested.
     *
     * @param activityId     The ID of the activity, directory activity or intensity level activity. (required)
     * @param manualCalories Calories burned that are manaully specified. Required with activityName must be provided. (required)
     * @param startTime      Activity start time. Hours and minutes in the format HH:mm:ss. (required)
     * @param durationMillis Duration in milliseconds. (required)
     * @param date           Log entry date in the format yyyy-MM-dd. (required)
     * @param distance       Distance is required for logging directory activity in the format X.XX and in the selected distanceUnit. (required)
     * @param activityName   Custom activity name. Either activityId or activityName must be provided. (optional)
     * @param distanceUnit   Distance measurement unit. Steps units are available only for Walking (activityId&#x3D;90013) and Running (activityId&#x3D;90009) directory activities and their intensity levels. (optional)
     * @return Call&lt;Void&gt;
     */
    @POST("1/user/-/activities.json")
    Call<Void> activity_2(
            @retrofit2.http.Query("activityId") Integer activityId, @retrofit2.http.Query("manualCalories") Integer manualCalories, @retrofit2.http.Query("startTime") String startTime, @retrofit2.http.Query("durationMillis") Integer durationMillis, @retrofit2.http.Query("date") String date, @retrofit2.http.Query("distance") Integer distance, @retrofit2.http.Query("activityName") String activityName, @retrofit2.http.Query("distanceUnit") Integer distanceUnit
    );

    /**
     * Get Favorite Activities
     * Returns a list of a user&#39;s favorite activities.
     *
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/favorite.json")
    Call<Void> activity_3();


    /**
     * Add Favorite Activity
     * Adds the activity with the given ID to user&#39;s list of favorite activities.
     *
     * @param activityId The encoded ID of the activity. (required)
     * @return Call&lt;Void&gt;
     */
    @POST("1/user/-/activities/favorite/{activity-id}.json")
    Call<Void> activity_4(
            @retrofit2.http.Path("activity-id") String activityId
    );

    /**
     * Delete Favorite Activity
     * Removes the activity with the given ID from a user&#39;s list of favorite activities.
     *
     * @param activityId The ID of the activity to be removed. (required)
     * @return Call&lt;Void&gt;
     */
    @DELETE("1/user/-/activities/favorite/{activity-id}.json")
    Call<Void> activity_5(
            @retrofit2.http.Path("activity-id") String activityId
    );

    /**
     * Get Frequent Activities
     * Retreives a list of a user&#39;s frequent activities in the format requested using units in the unit system which corresponds to the Accept-Language header provided.
     *
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/frequent.json")
    Call<Void> activity_6();


    /**
     * Get Activity Goals
     * Retreives a user&#39;s current daily or weekly activity goals using measurement units as defined in the unit system, which corresponds to the Accept-Language header provided.
     *
     * @param period daily or weekly. (required)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/goals/{period}.json")
    Call<Void> activity_7(
            @retrofit2.http.Path("period") String period
    );

    /**
     * Update Activity Goals
     * Updates a user&#39;s daily or weekly activity goals and returns a response using units in the unit system which corresponds to the Accept-Language header provided.
     *
     * @param period daily or weekly. (required)
     * @param type   goal type (required)
     * @param value  goal value (required)
     * @return Call&lt;Void&gt;
     */
    @POST("1/user/-/activities/goals/{period}.json")
    Call<Void> activity_8(
            @retrofit2.http.Path("period") String period, @retrofit2.http.Query("type") String type, @retrofit2.http.Query("value") String value
    );

    /**
     * Get Activity Log List
     * Retreives a list of user&#39;s activity log entries before or after a given day with offset and limit using units in the unit system which corresponds to the Accept-Language header provided.
     *
     * @param sort       The sort order of entries by date asc (ascending) or desc (descending). (required)
     * @param offset     The offset number of entries. (required)
     * @param limit      The maximum number of entries returned (maximum;100). (required)
     * @param beforeDate The date in the format yyyy-MM-ddTHH:mm:ss. Only yyyy-MM-dd is required. Either beforeDate or afterDate should be specified. (optional)
     * @param afterDate  The date in the format yyyy-MM-ddTHH:mm:ss. (optional)
     * @return Call&lt;Void&gt;
     */
    @GET("1/user/-/activities/list.json")
    Call<ActivityLog> getActivityLogList(
            @retrofit2.http.Query("sort") String sort, @retrofit2.http.Query("offset") Integer offset, @retrofit2.http.Query("limit") Integer limit, @retrofit2.http.Query("beforeDate") String beforeDate, @retrofit2.http.Query("afterDate") String afterDate
    );

}
