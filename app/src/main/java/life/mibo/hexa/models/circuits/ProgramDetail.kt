/*
 *  Created by Sumeet Kumar on 2/4/20 12:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/4/20 12:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.circuits


import com.google.gson.annotations.SerializedName

data class ProgramDetail(
    @SerializedName("block_name")
    var blockName: String?,
    @SerializedName("block_parameters")
    var blockParameters: List<BlockParameter?>?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("program_id")
    var programId: Int?,
    @SerializedName("updated_at")
    var updatedAt: String?
)