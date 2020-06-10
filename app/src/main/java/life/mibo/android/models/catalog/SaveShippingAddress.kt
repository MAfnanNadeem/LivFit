/*
 *  Created by Sumeet Kumar on 6/3/20 11:37 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/3/20 11:37 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SaveShippingAddress(data: Data?, token: String?) :
    BasePost<SaveShippingAddress.Data?>(data, "SaveMemberShippingAddress", token) {
    data class Data(
        @SerializedName("Address")
        var address: String?,
        @SerializedName("City")
        var city: String?,
        @SerializedName("Country")
        var country: String?,
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("Name")
        var name: String?,
        @SerializedName("Phone")
        var phone: String?
    )
}