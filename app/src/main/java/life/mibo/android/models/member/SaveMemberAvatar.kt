/*
 *  Created by Sumeet Kumar on 5/10/20 6:46 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/10/20 6:46 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.member


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class SaveMemberAvatar(data: SaveMemberAvatar.Data?) : BaseResponse<SaveMemberAvatar.Data?>(data) {
    data class Data(
        @SerializedName("code")
        var code: Int?,
        @SerializedName("message")
        var message: String?,
        @SerializedName("profile")
        var profile: String?
    )
}