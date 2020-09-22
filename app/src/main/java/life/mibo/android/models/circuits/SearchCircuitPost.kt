/*
 *  Created by Sumeet Kumar on 9/20/20 5:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/20/20 5:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.circuits


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SearchCircuitPost(data: Data?, token: String) :
    BasePost<SearchCircuitPost.Data?>(data, "SearchCircuit", token) {

    data class Data(
        @SerializedName("Element")
        var element: String?,
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("PageNo")
        var pageNo: String?,
        @SerializedName("PageSize")
        var pageSize: String?,
        @SerializedName("UserType")
        var userType: String?,
        @SerializedName("LocationID")
        var locationID: String?,
        @SerializedName("IslandID")
        var islandID: String?,
        @SerializedName("Search")
        var search: String?
    )
//    data class Data(
//        @SerializedName("Element")
//        var element: String?,
//        @SerializedName("LocationID")
//        var locationID: String?,
//        @SerializedName("MemberID")
//        var memberID: String?,
//        @SerializedName("PageNo")
//        var pageNo: Int?,
//        @SerializedName("PageSize")
//        var pageSize: Int?,
//        @SerializedName("Search")
//        var search: String?,
//        @SerializedName("UserType")
//        var userType: String?
//    )
}