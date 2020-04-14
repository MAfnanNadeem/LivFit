/*
 *  Created by Sumeet Kumar on 1/22/20 4:47 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 4:47 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.create_session


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("ConsumerCurrentDate")
    var consumerCurrentDate: String?,
    @SerializedName("ConsumerCurrentTime")
    var consumerCurrentTime: String?,
    @SerializedName("MemberID")
    var memberID: Int?,
    @SerializedName("ProgramID")
    var programID: Int?
)