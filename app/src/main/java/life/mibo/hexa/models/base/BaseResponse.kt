/*
 *  Created by Sumeet Kumar on 1/23/20 9:11 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 9:10 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.base

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.hexa.core.gson.AlwaysListTypeAdapterFactory

abstract class BaseResponse<T>(@SerializedName(value = "data", alternate = ["Data"]) var data: T?) : BaseModel {
    @SerializedName("error")
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    var errors: List<Error?>? = null
    @SerializedName("status")
    var status: String? = null

    fun status() : String =  status?.toLowerCase() ?: ""
}
