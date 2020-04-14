/*
 *  Created by Sumeet Kumar on 3/19/20 3:43 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/19/20 3:43 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.product


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class Products(data: List<Data?>??) : BaseResponse<List<Products.Data?>?>(data) {
    data class Data(
        @SerializedName("description")
        var description: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("price")
        var price: Int?,
        @SerializedName("priceCurrency")
        var priceCurrency: String?,
        @SerializedName("status")
        var status: Int?
    )
}