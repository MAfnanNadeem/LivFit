/*
 *  Created by Sumeet Kumar on 6/1/20 9:37 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/1/20 9:37 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.product


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetMemberServices(data: Data?, token: String?) :
    BasePost<GetMemberServices.Data?>(data, "GetMemberServices", token) {
    data class Data(
        @SerializedName("MemberID")
        var memberID: Int?
    )
}