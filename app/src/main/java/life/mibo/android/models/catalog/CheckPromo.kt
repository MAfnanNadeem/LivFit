/*
 *  Created by Sumeet Kumar on 7/11/20 3:52 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/11/20 3:52 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class CheckPromo(data: Data?, token: String?) :
    BasePost<CheckPromo.Data?>(data, "CheckPromocode", token) {
    data class Data(
        @SerializedName("ContractStartDate")
        var contractStartDate: String?,
        @SerializedName("MemberID")
        var memberID: Int?,
        @SerializedName("PlanType")
        var planType: String?,
        @SerializedName("Promocode")
        var promocode: String?,
        @SerializedName("ServiceID")
        var serviceID: Int?
    )

    companion object {
        fun createService(date: String?, member: Int?, service: Int?, promo: String?): Data {
            return CheckPromo.Data(date, member, "service", promo, service)
        }

        fun createPackage(date: String?, member: Int?, service: Int?, promo: String?): Data {
            return CheckPromo.Data(date, member, "package", promo, service)
        }
    }
}