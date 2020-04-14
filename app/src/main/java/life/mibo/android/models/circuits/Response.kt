/*
 *  Created by Sumeet Kumar on 2/4/20 12:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/4/20 12:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.circuits


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("Circuits")
    var circuits: List<Circuit?>?,
    @SerializedName("CurrentPage")
    var currentPage: Int?,
    @SerializedName("TotalPages")
    var totalPages: Int?
)