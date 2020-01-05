/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.login


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.hexa.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.hexa.models.base.Error

data class LoginResponse(
    @SerializedName("data")
    var `data`: Member?,
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    @SerializedName("error")
    var error: List<Error?>?,
    @SerializedName("status")
    var status: String?
)