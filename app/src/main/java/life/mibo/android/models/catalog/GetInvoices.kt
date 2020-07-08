/*
 *  Created by Sumeet Kumar on 6/4/20 10:26 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/4/20 10:26 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class GetInvoices(data: Data?) : BaseResponse<GetInvoices.Data?>(data) {
    data class Data(
        @SerializedName("Invoice")
        var invoice: List<Invoice?>?
    )

    data class Invoice(
        @SerializedName("name")
        var name: String?,
        @SerializedName("invoice_date")
        var invoiceDate: String?,
        @SerializedName("invoice_number")
        var invoiceNumber: String?,
        @SerializedName("package_type")
        var packageType: String?,
        @SerializedName("paid_status")
        var paidStatus: String?,
        @SerializedName("currency_type")
        var currency: String?,
        @SerializedName("price")
        var price: Double?,
        @SerializedName("vat")
        var vat: Double?,
        @SerializedName("locationID")
        var locationID: String?,
        @SerializedName("quantity")
        var quantity: Int?,
        @SerializedName("totalPrice")
        var totalPrice: Double?
    )
}