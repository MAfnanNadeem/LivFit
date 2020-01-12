/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.verify_number


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel

data class VerifyResponse(
    @SerializedName("data")
    var `data`: Any??,
    @SerializedName("errors")
    var errors: Any??,
    @SerializedName("status")
    var status: String?
): BaseModel()