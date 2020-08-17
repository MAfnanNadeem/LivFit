/*
 * $Created by $Sumeet $Kumar 2020.
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.send_otp


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.android.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.android.models.base.BaseModel
import life.mibo.android.models.base.BaseError

data class SendOtpResponse(
    @SerializedName("data")
    var `data`: Response?,
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    @SerializedName("error")
    var errors: List<BaseError>?,
    @SerializedName("status")
    var status: String?
): BaseModel