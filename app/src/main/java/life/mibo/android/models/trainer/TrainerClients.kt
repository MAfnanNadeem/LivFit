/*
 *  Created by Sumeet Kumar on 7/12/20 5:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/12/20 5:16 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class TrainerClients(data: List<Client?>?) : BaseResponse<List<TrainerClients.Client?>>(data) {

    data class Client(
        @SerializedName("avatar")
        var avatar: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("services")
        var services: List<Service?>?
    )

    data class Service(
        @SerializedName("completedSessions")
        var completedSessions: Int?,
        @SerializedName("endDate")
        var endDate: String?,
        @SerializedName("serviceName")
        var serviceName: String?,
        @SerializedName("startDate")
        var startDate: String?,
        @SerializedName("totalSessions")
        var totalSessions: Int?
    )
}