/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.login


import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("email")
    var email: String?,
    @SerializedName("password")
    var password: String?
)