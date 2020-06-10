/*
 *  Created by Sumeet Kumar on 6/9/20 10:41 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/9/20 10:41 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.trainer


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetServicesOfProfessionals(
    data: Data?, token: String?
) : BasePost<GetServicesOfProfessionals.Data?>(data, "ServicesIndependentProfessionals", token) {
    data class Data(
        @SerializedName("MemberID")
        var memberID: Int?,
        @SerializedName("TrainerID")
        var trainerID: Int?
    )
}