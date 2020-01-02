/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.login


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.Error

data class LoginResponse(
    @SerializedName("data")
    var `data`: Response?,
    @SerializedName("error")
    var error: List<Error?>?,
    @SerializedName("status")
    var status: String?
)