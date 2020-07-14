/*
 *  Created by Sumeet Kumar on 7/11/20 5:08 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/11/20 5:08 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class PromoResponse(data: Promo?) : BaseResponse<PromoResponse.Promo?>(data) {
    data class Promo(
        @SerializedName("CurrencyType")
        var currencyType: String?,
        @SerializedName("DiscountType")
        var discountType: String?,
        @SerializedName("PromoValue")
        var promoValue: String?,
        @SerializedName("PromocodeID")
        var promocodeID: String?
    ) {

        fun isFLat(): Boolean {
            return discountType?.toLowerCase() == "amount"
        }

        fun isPercent(): Boolean {
            return discountType?.toLowerCase() == "percent"
        }

        fun getValue(): Double {
            try {
                return promoValue?.toDouble() ?: 0.0
            } catch (e: Exception) {

            }
            return 0.0
        }
    }

//    fun isFLat(): Boolean {
//        return data?.discountType?.toLowerCase() == "amount"
//    }
//
//    fun isPercent(): Boolean {
//        return data?.discountType?.toLowerCase() == "percent"
//    }
//
//    fun getValue(): Double {
//        try {
//            return data?.promoValue?.toDouble() ?: 0.0
//        } catch (e: Exception) {
//
//        }
//        return 0.0
//    }
}