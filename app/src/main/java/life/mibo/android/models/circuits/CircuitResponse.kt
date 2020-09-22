/*
 *  Created by Sumeet Kumar on 9/20/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/20/20 5:16 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.circuits


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class CircuitResponse(data: Data) : BaseResponse<CircuitResponse.Data?>(data) {
    data class Data(
        @SerializedName("Circuits")
        var circuits: List<Circuit?>?,
        @SerializedName("CurrentPage")
        var currentPage: Int?,
        @SerializedName("TotalPages")
        var totalPages: Int?
    )
}