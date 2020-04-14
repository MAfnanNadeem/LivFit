/*
 *  Created by Sumeet Kumar on 1/23/20 8:45 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 8:45 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("message")
    var message: String?
)