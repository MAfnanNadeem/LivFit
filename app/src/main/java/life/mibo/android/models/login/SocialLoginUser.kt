/*
 *  Created by Sumeet Kumar on 5/10/20 11:31 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/10/20 11:31 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.login


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SocialLoginUser(data: SocialLoginUser.Data?) : BasePost<SocialLoginUser.Data?>(data, "SocialLoginUser", "") {

    constructor(username: String, password: String, social: String) : this(
        SocialLoginUser.Data(
            username,
            password,
            social
        )
    )

    data class Data(
        @SerializedName("email")
        var email: String?,
        @SerializedName("key")
        var key: String?,
        @SerializedName("type")
        var type: String?
    )
}