/*
 *  Created by Sumeet Kumar on 6/3/20 11:35 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/3/20 11:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class ShipmentAddress(address: List<Address?>?) :
    BaseResponse<List<ShipmentAddress.Address?>?>(address) {

    data class Address(
        @SerializedName("address")
        var address: String?,
        @SerializedName("city")
        var city: String?,
        @SerializedName("country")
        var country: String?,
        @SerializedName("created_at")
        var createdAt: String?,
        @SerializedName("created_by")
        var createdBy: Int?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("phone")
        var phone: String?,
        @SerializedName("updated_at")
        var updatedAt: String?
    )
}