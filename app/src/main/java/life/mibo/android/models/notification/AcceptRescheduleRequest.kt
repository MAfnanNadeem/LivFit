/*
 *  Created by Sumeet Kumar on 5/31/20 2:48 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/31/20 2:48 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.notification


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class AcceptRescheduleRequest(data: Data?, token: String?) :
    BasePost<AcceptRescheduleRequest.Data?>(data, "AcceptRescheduleRequest", token) {
    data class Data(
        @SerializedName("NotificationID")
        var notificationID: Int?,
        @SerializedName("Status")
        var status: String?
    )
}