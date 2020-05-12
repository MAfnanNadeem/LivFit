/*
 *  Created by Sumeet Kumar on 5/11/20 2:24 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/8/20 12:08 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base

import com.google.gson.annotations.SerializedName


class FirebaseTokenPost(
    memberId: String,
    token: String,
    firebase: String,
    request: String = "SaveFirebaseToken"
) :
    BasePost<FirebaseTokenPost.UserID>(UserID(memberId, firebase), request, token) {
    data class UserID(
        @SerializedName("UserID")
        var memberID: String?,
        @SerializedName("Token")
        var token: String?
    )
}