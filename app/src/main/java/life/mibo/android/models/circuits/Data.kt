/*
 *  Created by Sumeet Kumar on 2/4/20 12:10 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/4/20 12:10 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.circuits


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("PageNo")
    var pageNo: Int? = 1,
    @SerializedName("PageSize")
    var pageSize: Int? = 50,
    @SerializedName("Search")
    var search: String? = ""
)