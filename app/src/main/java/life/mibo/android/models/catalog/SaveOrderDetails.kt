/*
 *  Created by Sumeet Kumar on 6/4/20 3:24 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/4/20 3:24 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SaveOrderDetails(
    data: Data?,
    token: String?
) : BasePost<SaveOrderDetails.Data?>(data, "SaveOrderDetails", token) {
    data class Data(
        @SerializedName("Device")
        var device: String?,
        @SerializedName("LocationID")
        var locationID: String?,
        @SerializedName("MemberID")
        var memberID: Int?,
        @SerializedName("PackageID")
        var packageID: Int?,
        @SerializedName("Price")
        var price: Double?,
        @SerializedName("Quantity")
        var quantity: Int?,
        @SerializedName("ShippingAddressID")
        var shippingAddressID: Int?,
        @SerializedName("TransactionID")
        var transactionID: String?,
        @SerializedName("Type")
        var type: String?,
        @SerializedName("VAT")
        var vAT: Double?,
        @SerializedName("CurrencyType")
        var currency: String?,
        @SerializedName("PaidStatus")
        var paidStatus: String?,
        @SerializedName("BookingAdvice")
        var bookingAdvice: String?
    )
}