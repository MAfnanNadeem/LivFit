/*
 *  Created by Sumeet Kumar on 1/23/20 5:21 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 5:21 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.program

import com.google.gson.annotations.SerializedName

data class ProgramPostData(
    @SerializedName("PageSize") var pageSize: String = "50",
    @SerializedName("PageNo") var pageNo: String = "1",
    @SerializedName("Search") var search: String = "",
    @SerializedName("TrainerId") var trainerId: String = ""
)