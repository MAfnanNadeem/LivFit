/*
 *  Created by Sumeet Kumar on 7/9/20 9:51 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/9/20 9:51 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class TrainerServices(data: List<Data?>?) : BaseResponse<List<TrainerServices.Data?>>(data) {
    data class Data(
        @SerializedName("currency")
        var currency: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("numberOfSessions")
        var numberOfSessions: Int?,
        @SerializedName("price")
        var price: Int?
    )
}