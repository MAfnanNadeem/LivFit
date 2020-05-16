/*
 *  Created by Sumeet Kumar on 5/13/20 12:29 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/13/20 12:29 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class InviteProfessional(data: Data?, token: String?) :
    BasePost<InviteProfessional.Data?>(data, "SendInviteRequestToTrainer", token) {
    data class Data(
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("TrainerID")
        var trainerID: String?
    )
}