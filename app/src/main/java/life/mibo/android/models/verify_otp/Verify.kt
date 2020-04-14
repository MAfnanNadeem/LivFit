/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.verify_otp


import com.google.gson.annotations.SerializedName

data class Verify(
    @SerializedName("message")
    var message: String?
)