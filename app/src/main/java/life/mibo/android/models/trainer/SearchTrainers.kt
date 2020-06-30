/*
 *  Created by Sumeet Kumar on 5/13/20 9:35 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/13/20 9:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer

import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SearchTrainers(item: Page = Page(), token: String?) :
    BasePost<SearchTrainers.Page>(item, "SearchIndependentProfessionals", token) {


    data class Page(
        @SerializedName("PageSize") var pageSize: String = "50",
        @SerializedName("PageNo") var pageNo: String = "1",
        @SerializedName("Search") var search: String = ""
    )
}