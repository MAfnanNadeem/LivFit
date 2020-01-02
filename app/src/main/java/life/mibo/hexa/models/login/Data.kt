/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.login


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("email")
    var email: String?,
    @SerializedName("password")
    var password: String?
)