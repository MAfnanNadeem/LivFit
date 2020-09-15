/*
 *  Created by Sumeet Kumar on 8/25/20 12:35 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 8/25/20 12:35 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.rxt


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class GetAllIslandsByLocation(data: List<Island?>?) :
    BaseResponse<List<GetAllIslandsByLocation.Island?>?>(data) {

    data class Island(
        @SerializedName("ID")
        var id: Int?,
        @SerializedName("IslandHeight")
        var islandHeight: Int?,
        @SerializedName("IslandImage")
        var islandImage: String?,
        @SerializedName("IslandWidth")
        var islandWidth: Int?,
        @SerializedName("LocationId")
        var locationId: Any?,
        @SerializedName("Name")
        var name: String?,
        @SerializedName("Tiles")
        var tiles: String?,
        @SerializedName("TotalTiles")
        var totalTiles: Int?
    ) {

        fun getTileCount(): Int {
            val s = tiles?.split(",")
            return s?.size ?: 0
        }
    }
}