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

public interface FoodLoggingApi {
  /**
   * Log Water
   * Creates a log entry for water using units in the unit systems that corresponds to the Accept-Language header provided.
   * @param date The date of records to be returned in the format yyyy-MM-dd. (required)
   * @param amount The amount consumption in the format X.XX and in the specified waterUnit or in the unit system that corresponds to the Accept-Language header provided. (required)
   * @param unit Water measurement unit; &#x60;ml&#x60;, &#x60;fl oz&#x60;, or &#x60;cup&#x60;. (optional)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/foods/log/water.json")
  Call<Void> UserFoodsLogWaterJsonPost(
          @retrofit2.http.Query("date") String date, @retrofit2.http.Query("amount") Integer amount, @retrofit2.http.Query("unit") String unit
  );

  /**
   * Get Food Locales
   * Returns the food locales that the user may choose to search, log, and create food in.
   * @return Call&lt;Void&gt;
   */
  @GET("1/foods/locales.json")
  Call<Void> food();


  /**
   * Search Foods
   * Returns a list of public foods from the Fitbit food database and private food the user created in the format requested.
   * @param query The URL-encoded search query. (required)
   * @return Call&lt;Void&gt;
   */
  @GET("1/foods/search.json")
  Call<Void> food_0(
          @retrofit2.http.Query("query") String query
  );

  /**
   * Get Food Units
   * Returns a list of all valid Fitbit food units in the format requested.
   * @return Call&lt;Void&gt;
   */
  @GET("1/foods/units.json")
  Call<Void> food_1();


  /**
   * Get Food Goals
   * Returns a user&#39;s current daily calorie consumption goal and/or foodPlan value in the format requested.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/foods/log/goal.json")
  Call<Void> food_10();


  /**
   * Update Food Goal
   * Updates a user&#39;s daily calories consumption goal or food plan and returns a response in the format requested.
   * @param calories Manual calorie consumption goal in either calories or intensity must be provided. (required)
   * @param intensity Food plan intensity (MAINTENANCE, EASIER, MEDIUM, KINDAHARD, or HARDER). Either calories or intensity must be provided. (optional)
   * @param personalized Food plan type; true or false. (optional)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/foods/log/goal.json")
  Call<Void> food_11(
          @retrofit2.http.Query("calories") Integer calories, @retrofit2.http.Query("intensity") String intensity, @retrofit2.http.Query("personalized") String personalized
  );

  /**
   * Get Recent Foods
   * Returns a list of a user&#39;s frequent foods in the format requested. A frequent food in the list provides a quick way to log the food via the Log Food endpoint.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/foods/log/recent.json")
  Call<Void> food_12();


  /**
   * Delete Food Log
   * Deletes a user&#39;s food log entry with the given ID.
   * @param foodLogId The ID of the food log entry to be deleted. (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("1/user/-/foods/log/{food-log-id}.json")
  Call<Void> food_13(
          @retrofit2.http.Path("food-log-id") String foodLogId
  );

  /**
   * Delete Custom Food
   * Deletes custom food for a user and returns a response in the format requested.
   * @param foodId The ID of the food to be deleted. (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("1/user/-/foods/{food-id}.json")
  Call<Void> food_14(
          @retrofit2.http.Path("food-id") String foodId
  );

  /**
   * Get Meals
   * Returns a list of meals created by user in the user&#39;s food log in the format requested. User creates and manages meals on the Food Log tab on the website.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/meals.json")
  Call<Void> food_15();


  /**
   * Create Meal
   * Creates a meal with the given food contained in the post body.
   * @param name Name of the meal. (required)
   * @param description Short description of the meal. (required)
   * @param foodId ID of the food to be included in the meal. (required)
   * @param unitId ID of units used. Typically retrieved via a previous call to Get Food Logs, Search Foods, or Get Food Units. (required)
   * @param amount Amount consumed; in the format X.XX, in the specified unitId. (required)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/meals.json")
  Call<Void> food_16(
          @retrofit2.http.Query("name") String name, @retrofit2.http.Query("Description") String description, @retrofit2.http.Query("foodId") String foodId, @retrofit2.http.Query("unitId") String unitId, @retrofit2.http.Query("amount") String amount
  );

  /**
   * Edit Meal
   * Replaces an existing meal with the contents of the request. The response contains the updated meal.
   * @param mealId Id of the meal to edit. (required)
   * @param name Name of the meal. (required)
   * @param description Short description of the meal. (required)
   * @param foodId ID of the food to be included in the meal. (required)
   * @param unitId ID of units used. Typically retrieved via a previous call to Get Food Logs, Search Foods, or Get Food Units. (required)
   * @param amount Amount consumed; in the format X.XX, in the specified unitId. (required)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/meals/{meal-id}.json")
  Call<Void> food_17(
          @retrofit2.http.Path("meal-id") String mealId, @retrofit2.http.Query("name") String name, @retrofit2.http.Query("Description") String description, @retrofit2.http.Query("foodId") String foodId, @retrofit2.http.Query("unitId") String unitId, @retrofit2.http.Query("amount") String amount
  );

  /**
   * Delete Meal
   * Deletes a user&#39;s meal with the given meal id.
   * @param mealId Id of the meal to delete. (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("1/user/-/meals/{meal-id}.json")
  Call<Void> food_18(
          @retrofit2.http.Path("meal-id") String mealId
  );

  /**
   * Get Food
   * Returns the details of a specific food in the Fitbit food databases or a private food that an authorized user has entered in the format requested.
   * @param foodId The ID of the food. (required)
   * @return Call&lt;Void&gt;
   */
  @GET("1/foods/{food-id}.json")
  Call<Void> food_2(
          @retrofit2.http.Path("food-id") String foodId
  );

  /**
   * Create Food
   * Creates a new private food for a user and returns a response in the format requested. The created food is found via the Search Foods call.
   * @param name The food name. (required)
   * @param defaultFoodMeasurementUnitId The ID of the default measurement unit. Full list of units can be retrieved via the Get Food Units endpoint. (required)
   * @param defaultServingSize The size of the default serving. Nutrition values should be provided for this serving size. (required)
   * @param calories The calories in the default serving size. (required)
   * @param formType Form type; LIQUID or DRY. (optional)
   * @param description The description of the food. (optional)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/foods.json")
  Call<Void> food_3(
          @retrofit2.http.Query("name") String name, @retrofit2.http.Query("defaultFoodMeasurementUnitId") String defaultFoodMeasurementUnitId, @retrofit2.http.Query("defaultServingSize") String defaultServingSize, @retrofit2.http.Query("calories") String calories, @retrofit2.http.Query("formType") String formType, @retrofit2.http.Query("description") String description
  );

  /**
   * Log Food
   * Creates food log entries for users with or without foodId value.
   * @param foodId The ID of the food to be logged. Either foodId or foodName must be provided. (required)
   * @param mealTypeId Meal types. 1&#x3D;Breakfast; 2&#x3D;Morning Snack; 3&#x3D;Lunch; 4&#x3D;Afternoon Snack; 5&#x3D;Dinner; 7&#x3D;Anytime. (required)
   * @param unitId The ID of units used. Typically retrieved via a previous call to Get Food Logs, Search Foods, or Get Food Units. (required)
   * @param amount The amount consumed in the format X.XX in the specified unitId. (required)
   * @param date Log entry date in the format yyyy-MM-dd. (required)
   * @param foodName Food entry name. Either foodId or foodName must be provided. (optional)
   * @param favorite The &#x60;true&#x60; value will add the food to the user&#39;s favorites after creating the log entry; while the &#x60;false&#x60; value will not. Valid only with foodId value. (optional)
   * @param brandName Brand name of food. Valid only with foodName parameters. (optional)
   * @param calories Calories for this serving size. This is allowed with foodName parameter (default to zero); otherwise it is ignored. (optional)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/foods/log.json")
  Call<Void> food_4(
          @retrofit2.http.Query("foodId") String foodId, @retrofit2.http.Query("mealTypeId") String mealTypeId, @retrofit2.http.Query("unitId") String unitId, @retrofit2.http.Query("amount") String amount, @retrofit2.http.Query("date") String date, @retrofit2.http.Query("foodName") String foodName, @retrofit2.http.Query("favorite") Boolean favorite, @retrofit2.http.Query("brandName") String brandName, @retrofit2.http.Query("calories") Integer calories
  );

  /**
   * Get Food Logs
   * Retreives a summary and list of a user&#39;s food log entries for a given day in the format requested.
   * @param date The date of records to be returned. In the format yyyy-MM-dd. (required)
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/foods/log/date/{date}.json")
  Call<Void> food_5(
          @retrofit2.http.Path("date") String date
  );

  /**
   * Get Favorite Foods
   * Returns a list of a user&#39;s favorite foods in the format requested. A favorite food in the list provides a quick way to log the food via the Log Food endpoint.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/foods/log/favorite.json")
  Call<Void> food_6();


  /**
   * Add Favorite Food
   * Updates a user&#39;s daily activity goals and returns a response using units in the unit system which corresponds to the Accept-Language header provided.
   * @param foodId The ID of the food to be added to user&#39;s favorites. (required)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/foods/log/favorite/{food-id}.json")
  Call<Void> food_7(
          @retrofit2.http.Path("food-id") String foodId
  );

  /**
   * Delete Favorite Food
   * Deletes a food with the given ID to the user&#39;s list of favorite foods.
   * @param foodId The ID of the food to be deleted from user&#39;s favorites. (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("1/user/-/foods/log/favorite/{food-id}.json")
  Call<Void> food_8(
          @retrofit2.http.Path("food-id") String foodId
  );

  /**
   * Get Frequent Foods
   * Returns a list of a user&#39;s frequent foods in the format requested. A frequent food in the list provides a quick way to log the food via the Log Food endpoint.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/foods/log/frequent.json")
  Call<Void> food_9();


  /**
   * Get Water Logs
   * Retreives a summary and list of a user&#39;s water log entries for a given day in the requested using units in the unit system that corresponds to the Accept-Language header provided.
   * @param date The date of records to be returned. In the format yyyy-MM-dd. (required)
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/foods/log/water/date/{date}.json")
  Call<Void> water(
          @retrofit2.http.Path("date") String date
  );

  /**
   * Get Water Goal
   * Retreives a summary and list of a user&#39;s water goal entries for a given day in the requested using units in the unit system that corresponds to the Accept-Language header provided.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/foods/log/water/goal.json")
  Call<Void> water_0();


  /**
   * Update Water Goal
   * Updates a user&#39;s daily calories consumption goal or food plan and returns a response in the format requested.
   * @param target The target water goal in the format X.X is set in unit based on locale. (required)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/foods/log/water/goal.json")
  Call<Void> water_1(
          @retrofit2.http.Query("target") Integer target
  );

  /**
   * Update Water Log
   * Updates a user&#39;s water log entry with the given ID.
   * @param waterLogId The ID of the waterUnit log entry to be deleted. (required)
   * @param amount Amount consumed; in the format X.X and in the specified waterUnit or in the unit system that corresponds to the Accept-Language header provided. (required)
   * @param unit Water measurement unit. &#39;ml&#39;, &#39;fl oz&#39;, or &#39;cup&#39;. (optional)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/foods/log/water/{water-log-id}.json")
  Call<Void> water_2(
          @retrofit2.http.Path("water-log-id") String waterLogId, @retrofit2.http.Query("amount") String amount, @retrofit2.http.Query("unit") String unit
  );

  /**
   * Delete Water Log
   * Deletes a user&#39;s water log entry with the given ID.
   * @param waterLogId The ID of the waterUnit log entry to be deleted. (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("1/user/-/foods/log/water/{water-log-id}.json")
  Call<Void> water_3(
          @retrofit2.http.Path("water-log-id") String waterLogId
  );

}
