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

public interface SubscriptionsApi {
  /**
   * Get a List of Subscriptions
   * Retreives a list of a user&#39;s subscriptions for your application in the format requested. You can either fetch subscriptions for a specific collection or the entire list of subscriptions for the user. For best practice, make sure that your application maintains this list on your side and use this endpoint only to periodically ensure data consistency.
   * @param collectionPath This is the resource of the collection to receive notifications from (foods, activities, sleep, or body). If not present, subscription will be created for all collections. If you have both all and specific collection subscriptions, you will get duplicate notifications on that collections&#39; updates. Each subscriber can have only one subscription for a specific user&#39;s collection. (required)
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/{collection-path}/apiSubscriptions.json")
  Call<Void> subscription(
          @Path("collection-path") String collectionPath
  );

  /**
   * Add a Subscription
   * Adds a subscription in your application so that users can get notifications and return a response in the format requested. The subscription-id value provides a way to associate an update with a particular user stream in your application.
   * @param collectionPath This is the resource of the collection to receive notifications from (foods, activities, sleep, or body). If not present, subscription will be created for all collections. If you have both all and specific collection subscriptions, you will get duplicate notifications on that collections&#39; updates. Each subscriber can have only one subscription for a specific user&#39;s collection. (required)
   * @param subscriptionId This is the unique ID of the subscription created by the API client application. Each ID must be unique across the entire set of subscribers and collections. The Fitbit servers will pass this ID back along with any notifications about the user indicated by the user parameter in the URL path. (required)
   * @return Call&lt;Void&gt;
   */
  @POST("1/user/-/{collection-path}/apiSubscriptions/{subscription-id}.json")
  Call<Void> subscription_0(
          @Path("collection-path") String collectionPath, @Path("subscription-id") String subscriptionId
  );

  /**
   * Delete a Subscription
   * Deletes a subscription for a user..
   * @param collectionPath This is the resource of the collection to receive notifications from (foods, activities, sleep, or body). If not present, subscription will be created for all collections. If you have both all and specific collection subscriptions, you will get duplicate notifications on that collections&#39; updates. Each subscriber can have only one subscription for a specific user&#39;s collection. (required)
   * @param subscriptionId This is the resource of the collection to receive notifications from (foods, activities, sleep, or body). If not present, subscription will be created for all collections. If you have both all and specific collection subscriptions, you will get duplicate notifications on that collections&#39; updates. Each subscriber can have only one subscription for a specific user&#39;s collection. (required)
   * @return Call&lt;Void&gt;
   */
  @DELETE("1/user/-/{collection-path}/apiSubscriptions/{subscription-id}.json")
  Call<Void> subscription_1(
          @Path("collection-path") String collectionPath, @Path("subscription-id") String subscriptionId
  );

}
