/*
 *  Created by Sumeet Kumar on 1/15/20 3:39 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.program


import com.google.gson.annotations.SerializedName

data class Program(
    @SerializedName("AccessType")
    var accessType: String?,
    @SerializedName("Blocks")
    var blocks: List<Block?>?,
    @SerializedName("BorgRating")
    var borgRating: Int?,
    @SerializedName("Category")
    var category: String?,
    @SerializedName("CircuitID")
    var circuitID: Int?,
    @SerializedName("CreatedBy")
    var createdBy: Int?,
    @SerializedName("Description")
    var description: String?,
    @SerializedName("Duration")
    var duration: Duration?,
    @SerializedName("Id")
    var id: Int?,
    @SerializedName("MemberID")
    var memberID: Int?,
    @SerializedName("Name")
    var name: String?,
    @SerializedName("Type")
    var type: String?
)