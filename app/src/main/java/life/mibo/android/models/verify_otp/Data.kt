/*
 * $Created by $Sumeet $Kumar 2020.
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.verify_otp


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("UserID")
    var userid: String?,
    @SerializedName("otp")
    var otp: String?
)