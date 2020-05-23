/*
 *  Created by Sumeet Kumar on 5/20/20 9:31 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/20/20 9:31 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class StartTrainerSession(data: Data?, token: String?) :
    BasePost<StartTrainerSession.Data?>(data, "StartSession", token) {
    data class Data(
        @SerializedName("LocationID")
        var locationID: String?,
        @SerializedName("SessionID")
        var sessionID: Int?,
        @SerializedName("started")
        var started: Int?,
        @SerializedName("TrainerID")
        var trainerID: Int?
    )
}