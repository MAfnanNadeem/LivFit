/*
 *  Created by Sumeet Kumar on 9/2/20 3:24 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/2/20 12:19 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.rxt


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetMemberScores(
    data: Data?,
    token: String?
) : BasePost<GetMemberScores.Data?>(data, "GetMemberScores", token) {
    data class Data(
        @SerializedName("ExerciseType")
        var exerciseType: String?,
        @SerializedName("MemberID")
        var memberID: String?
    )
}