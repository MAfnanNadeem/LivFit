/*
 *  Created by Sumeet Kumar on 5/13/20 10:36 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/13/20 10:36 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class ProfessionalDetails(data: List<Data?>?) :
    BaseResponse<List<ProfessionalDetails.Data?>?>(data) {
    
    data class Data(
        @SerializedName("archive")
        var archive: Int?,
        @SerializedName("category")
        var category: String?,
        @SerializedName("created_at")
        var createdAt: String?,
        @SerializedName("created_by")
        var createdBy: Any?,
        @SerializedName("Price")
        var currency: Int?,
        @SerializedName("Currency")
        var currencyType: Any?,
        @SerializedName("Description")
        var description: String?,
        @SerializedName("duration")
        var duration: String?,
        @SerializedName("end_date")
        var endDate: String?,
        @SerializedName("gym_id")
        var gymId: Int?,
        @SerializedName("ID")
        var id: Int?,
        @SerializedName("location_id")
        var locationId: Int?,
        @SerializedName("location_type")
        var locationType: Any?,
        @SerializedName("max_no")
        var maxNo: Any?,
        @SerializedName("Name")
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
        var vatCharge: String?
    )


}