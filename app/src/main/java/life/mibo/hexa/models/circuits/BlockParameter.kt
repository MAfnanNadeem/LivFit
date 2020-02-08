/*
 *  Created by Sumeet Kumar on 2/4/20 12:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/4/20 12:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.circuits


import com.google.gson.annotations.SerializedName

data class BlockParameter(
    @SerializedName("block_id")
    var blockId: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("default_val")
    var defaultVal: Any?,
    @SerializedName("desc_val")
    var descVal: Any?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("max")
    var max: Any?,
    @SerializedName("min")
    var min: Any?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("unit")
    var unit: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("value")
    var value: String?
)