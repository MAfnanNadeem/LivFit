/*
 *  Created by Sumeet Kumar on 7/9/20 9:39 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/9/20 9:39 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class TrainerInvoices(data: List<Data?>?) : BaseResponse<List<TrainerInvoices.Data?>?>(data) {

    data class Data(
        @SerializedName("date")
        var date: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("member")
        var member: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("currency")
        var currency: String?,
        @SerializedName("price")
        var price: Int?,
        @SerializedName("quantity")
        var quantity: String?
    )
}