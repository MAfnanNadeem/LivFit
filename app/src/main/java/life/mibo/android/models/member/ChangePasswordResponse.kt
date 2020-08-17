/*
 *  Created by Sumeet Kumar on 4/13/20 3:18 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/13/20 3:18 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.member


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.android.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.android.models.base.BaseError
import life.mibo.android.models.base.Response
import life.mibo.hardware.models.BaseModel

class ChangePasswordResponse(
    @SerializedName("data")
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    var `data`: List<Response?>?,
    @SerializedName("error", alternate = ["errors"])
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    var errors: List<BaseError?>?,
    @SerializedName("status")
    var status: String?
) : BaseModel {

}