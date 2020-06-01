/*
 *  Created by Sumeet Kumar on 5/31/20 12:24 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/31/20 12:24 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.notification


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetMemberNotifications(data: Data?, token: String?) :
    BasePost<GetMemberNotifications.Data?>(data, "GetMemberNotifications", token) {
    data class Data(
        @SerializedName("MemberID")
        var memberID: Int?
    )
}