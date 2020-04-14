/*
 * $Created by $Sumeet $Kumar 2020.
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.send_otp


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("Messageid")
    var Messageid: String?,
    @SerializedName("message")
    var message: String?
)