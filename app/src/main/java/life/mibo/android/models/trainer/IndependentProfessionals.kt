/*
 *  Created by Sumeet Kumar on 5/13/20 9:48 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/13/20 9:48 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class IndependentProfessionals(data: Data?) : BaseResponse<IndependentProfessionals.Data?>(data) {

    data class Data(
        @SerializedName("CurrentPage")
        var currentPage: Int?,
        @SerializedName("IndependentProfessionals")
        var professionals: List<Professional?>?,
        @SerializedName("TotalPages")
        var totalPages: Int?
    )
}