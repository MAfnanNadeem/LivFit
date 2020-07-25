/*
 *  Created by Sumeet Kumar on 5/10/20 11:31 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/10/20 11:31 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.login


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SocialLoginUser(data: Data?) : BasePost<SocialLoginUser.Data?>(data, "SocialLoginUser", "") {

    constructor(
        email: String,
        key: String,
        firstName: String?,
        lastName: String,
        avatar: String,
        type: String
    ) : this(
        Data(
            email,
            key,
            firstName,
            lastName,
            avatar,
            type
        )
    )

    data class Data(
        @SerializedName("email")
        var email: String?,
        @SerializedName("key")
        var key: String?,
        @SerializedName("firstName")
        var fName: String?,
        @SerializedName("lastName")
        var lName: String?,
        @SerializedName("avatar")
        var photo: String?,
        @SerializedName("type")
        var type: String?
    )
}