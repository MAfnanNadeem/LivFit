/*
 *  Created by Sumeet Kumar on 4/5/20 4:13 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/5/20 4:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.muscle

import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BasePost

class GetSuitPost(suitType: String, token: String, request: String = "GetSuits") :
    BasePost<GetSuitPost.SuitType>(GetSuitPost.SuitType(suitType), request, token) {

    data class SuitType(
        @SerializedName("Category")
        var suitType: String?
    )

    companion object {
        const val CHANEL_10 = "10 Channel"
        const val CHANEL_6 = "6 Channel"
        const val CHANEL_4 = "4 Channel"
        const val CHANEL_2 = "2 Channel"
        const val CHANEL_ALL = ""
    }
}