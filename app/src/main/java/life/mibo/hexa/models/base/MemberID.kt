/*
 *  Created by Sumeet Kumar on 2/11/20 9:36 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 9:36 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.base


import com.google.gson.annotations.SerializedName

data class MemberID(
    @SerializedName("MemberID")
    var memberID: String?
)