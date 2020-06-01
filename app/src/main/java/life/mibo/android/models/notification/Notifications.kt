/*
 *  Created by Sumeet Kumar on 5/31/20 1:57 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/31/20 1:57 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.notification


import com.google.gson.annotations.SerializedName

data class Notifications(
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("mark_as_read")
    var markAsRead: Int?,
    @SerializedName("notification")
    var notification: String?,
    @SerializedName("sent_by")
    var sentBy: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("trainer_id")
    var trainerId: Int?,
    @SerializedName("member_id")
    var memberId: Int?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("updated_at")
    var updatedAt: String?
) {
    var isMember: Boolean = false
}