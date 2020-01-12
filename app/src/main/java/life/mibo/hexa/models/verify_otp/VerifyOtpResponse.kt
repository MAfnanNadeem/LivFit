/*
 * $Created by $Sumeet $Kumar 2020.
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.verify_otp


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.hexa.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.hexa.models.base.BaseModel
import life.mibo.hexa.models.base.Error

data class VerifyOtpResponse(
    @SerializedName("data")
    var `data`: Verify?,
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    @SerializedName("error")
    var errors: List<Error>?,
    @SerializedName("status")
    var status: String?
): BaseModel()