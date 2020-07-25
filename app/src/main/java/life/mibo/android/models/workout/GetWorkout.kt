/*
 *  Created by Sumeet Kumar on 7/15/20 1:48 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/15/20 1:48 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.workout


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetWorkout(data: Data?, token: String?) :
    BasePost<GetWorkout.Data?>(data, "SearchWorkoutVideo", token) {

    data class Data(
        @SerializedName("MemberID")
        var memberID: Int?,
        @SerializedName("PageNo")
        var pageNo: Int?,
        @SerializedName("PageSize")
        var pageSize: Int?,
        @SerializedName("Search")
        var search: String?
    )
}