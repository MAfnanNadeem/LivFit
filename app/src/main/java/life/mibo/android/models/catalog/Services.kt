/*
 *  Created by Sumeet Kumar on 6/1/20 9:35 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/1/20 9:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse
import life.mibo.android.models.biometric.CreatedAt
import java.io.Serializable

class Services(data: List<Data?>?) : BaseResponse<List<Services.Data?>?>(data) {

    data class Data(
        @SerializedName("created_at")
        var createdAt: CreatedAt?,
        @SerializedName("created_by")
        var createdBy: String?,
        @SerializedName("currency")
        var currency: Double?,
        @SerializedName("currency_type")
        var currencyType: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("location")
        var location: String?,
        @SerializedName("locationID")
        var locationID: String?,
        @SerializedName("location_type")
        var locationType: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("no_of_session")
        var noOfSession: Int?,
        @SerializedName("validity")
        var validity: Int?,
        @SerializedName("vat")
        var vat: Double?,
        @SerializedName("service_promo")
        var promoService: Int?,
        @SerializedName("start_date")
        var startDate: String?,
        @SerializedName("end_date")
        var endDate: String?

    ) : Serializable {

        fun match(query: String): Boolean {
            if (name?.toLowerCase()?.contains(query) == true)
                return true
            if (createdBy?.toLowerCase()?.contains(query) == true)
                return true
            return false
        }

    }


}