/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.send_otp


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("UserID")
    var userid: String?
)