/*
 *  Created by Sumeet Kumar on 1/15/20 3:39 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.program


import com.google.gson.annotations.SerializedName

data class Block(
    @SerializedName("Id")
    var id: Int?,
    @SerializedName("Parameter")
    var parameter: Parameter?,
    @SerializedName("ShortName")
    var shortName: String?
)