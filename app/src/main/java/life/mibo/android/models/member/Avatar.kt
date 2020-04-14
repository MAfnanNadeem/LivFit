/*
 *  Created by Sumeet Kumar on 4/12/20 9:08 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/12/20 9:08 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.member


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class Avatar(data: Data?, token: String?, requestType: String = "MemberAvatar") :
    BasePost<Avatar.Data?>(data, requestType, token) {

    class Data(
        @SerializedName("Avatar")
        var avatar: String?,
        @SerializedName("MemberID")
        var memberID: String?
    )
}