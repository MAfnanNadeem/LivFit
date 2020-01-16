/*
 *  Created by Sumeet Kumar on 1/15/20 3:33 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:33 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.program


import com.google.gson.annotations.SerializedName

data class DataPost(
    @SerializedName("PageNo")
    var pageNo: Int?,
    @SerializedName("PageSize")
    var pageSize: Int?,
    @SerializedName("Search")
    var search: String?,
    @SerializedName("TrainerId")
    var trainerId: String?
)