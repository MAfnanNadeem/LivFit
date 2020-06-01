/*
 *  Created by Sumeet Kumar on 5/31/20 12:26 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/31/20 12:26 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.notification


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetTrainerNotifications(data: Data?, token: String?) :
    BasePost<GetTrainerNotifications.Data?>(data, "GetTrainerNotifications", token) {
    data class Data(
        @SerializedName("TrainerID")
        var trainerId: Int?
    )
}