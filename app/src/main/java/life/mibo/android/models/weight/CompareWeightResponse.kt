/*
 *  Created by Sumeet Kumar on 5/23/20 2:49 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/23/20 2:49 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.weight


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class CompareWeightResponse(data: List<Data?>?) :
    BaseResponse<List<CompareWeightResponse.Data?>?>(data) {

    data class Data(
        @SerializedName("CreatedAt")
        var createdAt: CreatedAt?,
        @SerializedName("MemberID")
        var memberID: Int?,
        @SerializedName("Weight")
        var weight: String?,
        @SerializedName("Weight Unit")
        var weightUnit: String?
    )

    data class CreatedAt(
        @SerializedName("date")
        var date: String?,
        @SerializedName("timezone")
        var timezone: String?,
        @SerializedName("timezone_type")
        var timezoneType: Int?
    )
}