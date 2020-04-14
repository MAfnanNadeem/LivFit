/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.verify_number


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseModel

data class VerifyResponse(
    @SerializedName("data")
    var `data`: Any??,
    @SerializedName("errors")
    var errors: Any??,
    @SerializedName("status")
    var status: String?
): BaseModel