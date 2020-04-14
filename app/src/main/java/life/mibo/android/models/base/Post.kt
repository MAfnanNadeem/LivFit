/*
 *  Created by Sumeet Kumar on 1/12/20 11:51 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 11:50 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base


import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("UserID", alternate = ["MemberID"])
    var userid: String?
)