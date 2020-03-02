/*
 *  Created by Sumeet Kumar on 2/16/20 8:20 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/16/20 8:20 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.member


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BasePost

class MemberAvatar(
    avatarData: AvatarData,
    requestType: String = "MemberAvatar",
    token: String
) : BasePost<MemberAvatar.AvatarData>(avatarData, requestType, token) {
    data class AvatarData(
        @SerializedName("Avatar")
        var avatar: String?,
        @SerializedName("MemberID")
        var memberID: String?
    )

    constructor(memberID: String, base64: String, token: String) : this(
        AvatarData(base64, memberID), "MemberAvatar", token
    )
}