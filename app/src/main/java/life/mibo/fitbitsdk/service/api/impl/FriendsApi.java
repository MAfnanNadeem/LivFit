/*
 *  Created by Sumeet Kumar on 7/6/20 3:22 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/6/20 3:17 PM
 *  Mibo Hexa - app
 */

package life.mibo.fitbitsdk.service.api.impl;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FriendsApi {
  /**
   * Get Friends
   * Returns data of a user&#39;s friends in the format requested using units in the unit system which corresponds to the Accept-Language header provided.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/friends.json")
  Call<Void> friends();
    

  /**
   * Invite Friend
   * Creates an invitation to become friends with the authorized user.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/friends/invitations.json")
  Call<Void> friends_0();
    

  /**
   * Respond to Friend Invitation
   * Accepts or rejects an invitation to become friends wit inviting user.
   * @param fromUserId The encoded ID of a user from which to accept or reject invitation. (required)
   * @param accept Accept or reject invitation; true or false. (required)
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/friends/invitations/{from-user-id}.json")
  Call<Void> friends_1(
          @Path("from-user-id") String fromUserId, @Query("accept") String accept
  );

  /**
   * Get Friends Leaderboard
   * Returns data of a user&#39;s friends in the format requested using units in the unit system which corresponds to the Accept-Language header provided.
   * @return Call&lt;Void&gt;
   */
  @GET("1/user/-/friends/leaderboard.json")
  Call<Void> friends_2();
    

}
