/*
 *  Created by Sumeet Kumar on 5/23/20 2:47 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/23/20 2:47 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.weight


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class CompareMemberWeight(data: Data?, token: String?) :
    BasePost<CompareMemberWeight.Data?>(data, "CompareMemberWeight", token) {
    data class Data(
        @SerializedName("MemberID")
        var memberID: String?
    )
}