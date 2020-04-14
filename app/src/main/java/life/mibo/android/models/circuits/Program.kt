/*
 *  Created by Sumeet Kumar on 2/4/20 12:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/4/20 12:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.circuits


import com.google.gson.annotations.SerializedName

data class Program(
    @SerializedName("access_type")
    var accessType: String?,
    @SerializedName("active")
    var active: Int?,
    @SerializedName("block_type")
    var blockType: String?,
    @SerializedName("borg_rating")
    var borgRating: Any?,
    @SerializedName("buffer_time")
    var bufferTime: Any?,
    @SerializedName("category")
    var category: Any?,
    @SerializedName("circuit_id")
    var circuitId: Int?,
    @SerializedName("copy_no")
    var copyNo: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("created_by")
    var createdBy: Int?,
    @SerializedName("default_val")
    var defaultVal: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("format")
    var format: String?,
    @SerializedName("gym_location")
    var gymLocation: Int?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("max")
    var max: Any?,
    @SerializedName("member_id")
    var memberId: Any?,
    @SerializedName("min")
    var min: Any?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("pause")
    var pause: Int?,
    @SerializedName("program_details")
    var programDetails: List<ProgramDetail?>?,
    @SerializedName("program_mode")
    var programMode: Int?,
    @SerializedName("program_order")
    var programOrder: Int?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("unit")
    var unit: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("value")
    var value: String?
)