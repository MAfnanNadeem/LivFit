/*
 *  Created by Sumeet Kumar on 9/2/20 12:22 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/2/20 12:22 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.rxt


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetAllIslandPost(data: Data?, token: String?) :
    BasePost<GetAllIslandPost.Data?>(data, "GetAllIsland", token) {

    data class Data(
        @SerializedName("LocationId")
        var locationId: String?
    )
}