/*
 *  Created by Sumeet Kumar on 6/4/20 10:58 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/4/20 10:58 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.catalog


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetInvoiceDetail(data: Data?, token: String?) :
    BasePost<GetInvoiceDetail.Data?>(data, "GetSingleInvoice", token) {
    data class Data(
        @SerializedName("BookingAdviceNO")
        var bookingAdviceNO: String?,
        @SerializedName("MemberID")
        var memberID: Int?
    )
}