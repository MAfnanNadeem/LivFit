/*
 *  Created by Sumeet Kumar on 7/15/20 1:50 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/15/20 1:50 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.workout


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse
import java.io.Serializable

class Workouts(data: Data) : BaseResponse<Workouts.Data?>(data) {

    data class Data(
        @SerializedName("CurrentPage")
        var currentPage: Int?,
        @SerializedName("Programs")
        var programs: List<Program?>?,
        @SerializedName("TotalPages")
        var totalPages: Int?
    )

    data class Duration(
        @SerializedName("unit")
        var unit: String?,
        @SerializedName("value")
        var value: String?
    ) : Serializable

    data class Program(
        @SerializedName("BlockType")
        var blockType: String?,
        @SerializedName("BorgRating")
        var borgRating: Int?,
        @SerializedName("Description")
        var description: String?,
        @SerializedName("Duration")
        var duration: Duration?,
        @SerializedName("Id")
        var id: Int?,
        @SerializedName("Name")
        var name: String?,
        @SerializedName("Tags")
        var tags: List<String>?,
        @SerializedName("Video")
        var video: String?,
        @SerializedName("Thumbnail")
        var thumbnail: String?
    ) : Serializable
}