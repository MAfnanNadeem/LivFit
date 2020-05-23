/*
 *  Created by Sumeet Kumar on 5/17/20 11:53 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/17/20 11:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.member


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class MemberCalendarPost(data: Data?, token: String?) :
    BasePost<MemberCalendarPost.Data?>(data, "GetMemberCalendar", token) {
    data class Data(
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("FromDateTime")
        var fromDateTime: String?,
        @SerializedName("ToDateTime")
        var toDateTime: String?
    )
}