/*
 *  Created by Sumeet Kumar on 6/2/20 3:27 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/2/20 3:27 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.product


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse
import java.io.Serializable

class Packages(data: List<Data?>?) : BaseResponse<List<Packages.Data?>>(data) {

    data class Data(
        @SerializedName("archive")
        var archive: String?,
        @SerializedName("created_at")
        var createdAt: String?,
        @SerializedName("created_by")
        var createdBy: String?,
        @SerializedName("currency")
        var currency: String?,
        @SerializedName("currency_type")
        var currencyType: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("gym_id")
        var gymId: Int?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("location_id")
        var locationId: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("price")
        var price: Double?,
        @SerializedName("product_id")
        var productId: Int?,
        @SerializedName("service_id")
        var serviceId: Int?,
        @SerializedName("status")
        var status: Int?,
        @SerializedName("tax_percentage")
        var taxPercentage: Double?,
        @SerializedName("tax_type")
        var taxType: String?,
        @SerializedName("unique_id")
        var uniqueId: String?,
        @SerializedName("updated_at")
        var updatedAt: String?
    ) : Serializable {
        fun match(query: String): Boolean {
            if (name?.toLowerCase()?.contains(query) == true)
                return true
            return false
        }
    }
}