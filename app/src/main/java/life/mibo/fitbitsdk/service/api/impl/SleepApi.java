/*
 *  Created by Sumeet Kumar on 7/6/20 3:22 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/6/20 3:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.fitbitsdk.service.api.impl;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SleepApi {
  /**
   * Log Sleep
   * Creates a log entry for a sleep event and returns a response in the format requested.
   * @param startTime Start time includes hours and minutes in the format HH:mm. (required)
   * @param duration Duration in milliseconds. (required)
   * @param date Log entry in the format yyyy-MM-dd. (required)
   * @return Call&lt;Void&gt;
   */
  @POST("1.2/user/-/sleep.json")
  Call<Void> sleep(
          @Query("startTime") String startTime, @Query("duration") Integer duration, @Query("date") String date
  );

  /**
   * Get Sleep Logs by Date Range
   * The Get Sleep Logs by Date Range endpoint returns a list of a user&#39;s sleep log entries (including naps) as well as detailed sleep entry data for a given date range (inclusive of start and end dates).
   * @param baseDate The date of records to be returned. In the format yyyy-MM-dd. (required)
   * @param endDate The date of records to be returned. In the format yyyy-MM-dd. (required)
   * @return Call&lt;Void&gt;
   */
  @GET("1.2/user/-/sleep/date/{base-date}/{end-date}.json")
  Call<Void> sleep_0(
          @Path("base-date") String baseDate, @Path("end-date") String endDate
  );

  /**
   * Get Sleep Log
   * The Get Sleep Logs by Date endpoint returns a summary and list of a user&#39;s sleep log entries (including naps) as well as detailed sleep entry data for a given day.
   * @param date The date of records to be returned. In the format yyyy-MM-dd. (required)
   * @return Call&lt;Void&gt;
   */
  @GET("1.2/user/-/sleep/date/{date}.json")
  Call<Void> sleep_1(
          @Path("date") String date
  );

  /**
   * Get Sleep Goal
   * Returns the user&#39;s sleep goal.
   * @return Call&lt;Void&gt;
   */
  @GET("1.2/user/-/sleep/goal.json")
  Call<Void> sleep_2();


  /**
   * Update Sleep Goal
   * Create or update the user&#39;s sleep goal and get a response in the JSON format.
   * @param minDuration Duration of sleep goal. (required)
   * @return Call&lt;Void&gt;
   */
  @POST("1.2/user/-/sleep/goal.json")
  Call<Void> sleep_3(
          @Query("minDuration") String minDuration
  );

  /**
   * Get Sleep Logs List
   * The Get Sleep Logs List endpoint returns a list of a user&#39;s sleep logs (including naps) before or after a given day with offset, limit, and sort order.
   * @param sort The sort order of entries by date asc (ascending) or desc (descending). (required)
   * @param offset The offset number of entries. (required)
   * @param limit The maximum number of entries returned (maximum;100). (required)
   * @param beforeDate The date in the format yyyy-MM-ddTHH:mm:ss. Only yyyy-MM-dd is required. Either beforeDate or afterDate should be specified. (optional)
   * @param afterDate The date in the format yyyy-MM-ddTHH:mm:ss. (optional)
   * @return Call&lt;Void&gt;
   */
  @GET("1.2/user/-/sleep/list.json")
  Call<Void> sleep_4(
          @Query("sort") String sort, @Query("offset") Integer offset, @Query("limit") Integer limit, @Query("beforeDate") String beforeDate, @Query("afterDate") String afterDate
  );

  /**
   * Delete Sleep Log
   * Deletes a user&#39;s sleep log entry with the given ID.
   * @param logId The ID of the sleep log to be deleted. (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("1.2/user/-/sleep/{log-id}.json")
  Call<Void> sleep_5(
          @Path("log-id") String logId
  );

}
