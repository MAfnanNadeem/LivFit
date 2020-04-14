/*
 *  Created by Sumeet Kumar on 1/15/20 3:39 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.program


import com.google.gson.annotations.SerializedName

data class ProgramData(
    @SerializedName("CurrentPage")
    var currentPage: String,
    @SerializedName("Programs")
    var programs: ArrayList<Program?>?,
    @SerializedName("TotalPages")
    var totalPages: Int?,
    @SerializedName("TrainerId")
    var trainerId: Any?
)