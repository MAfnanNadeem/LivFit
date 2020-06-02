/*
 *  Created by Sumeet Kumar on 5/31/20 12:31 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/31/20 12:31 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.notification


import com.google.gson.annotations.SerializedName

data class TrainerNotifications(
    @SerializedName("data")
    var `data`: List<Data?>?,
    @SerializedName("error")
    var error: List<Any?>?,
    @SerializedName("status")
    var status: String?
) {
    data class Data(
        @SerializedName("avatar")
        var avatar: String?,
        @SerializedName("created_at")
        var createdAt: DateAt?,
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
        @SerializedName("type")
        var type: String?,
        @SerializedName("updated_at")
        var updatedAt: DateAt?
    )
}