/*
 *  Created by Sumeet Kumar on 2/4/20 12:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/4/20 12:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.circuits


import com.google.gson.annotations.SerializedName

data class Circuit(
    @SerializedName("AccessType")
    var accessType: String?,
    @SerializedName("BufferTime")
    var bufferTime: BufferTime?,
    @SerializedName("CreatedBy")
    var createdBy: Int?,
    @SerializedName("Description")
    var description: String?,
    @SerializedName("Duration")
    var duration: Duration?,
    @SerializedName("Id")
    var id: Int?,
    @SerializedName("MemberID")
    var memberID: Any?,
    @SerializedName("Name")
    var name: String?,
    @SerializedName("programs")
    var programs: List<Program?>?,
    @SerializedName("Type")
    var type: String?
)