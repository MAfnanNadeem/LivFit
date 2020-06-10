/*
 *  Created by Sumeet Kumar on 6/1/20 9:35 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/1/20 9:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse
import life.mibo.android.models.biometric.Biometric
import java.io.Serializable

class Services(data: List<Data?>?) : BaseResponse<List<Services.Data?>?>(data) {

    data class Data(
        @SerializedName("archive")
        var archive: Int?,
        @SerializedName("category")
        var category: String?,
        @SerializedName("created_at")
        var createdAt: Biometric.CreatedAt?,
        @SerializedName("created_by")
        var createdBy: String?,
        @SerializedName("currency")
        var currency: Double?,
        @SerializedName("currency_type")
        var currencyType: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("duration")
        var duration: String?,
        @SerializedName("end_date")
        var endDate: String?,
        @SerializedName("gym_id")
        var gymId: Int?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("location_id")
        var locationId: Int?,
        @SerializedName("location_type")
        var locationType: String?,
        @SerializedName("max_no")
        var maxNo: Any?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("no_of_session")
        var noOfSession: Int?,
        @SerializedName("package_id")
        var packageId: Any?,
        @SerializedName("program_id")
        var programId: Any?,
        @SerializedName("service_promo")
        var servicePromo: Int?,
        @SerializedName("session_gap")
        var sessionGap: Int?,
        @SerializedName("start_date")
        var startDate: String?,
        @SerializedName("status")
        var status: Int?,
        @SerializedName("type")
        var type: String?,
        @SerializedName("unique_id")
        var uniqueId: String?,
        @SerializedName("updated_at")
        var updatedAt: String?,
        @SerializedName("validity")
        var validity: Int?,
        @SerializedName("vat_charge")
        var vatCharge: String?,
        @SerializedName("location")
        var location: String?,
        @SerializedName("vat")
        var vat: Double?
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