/*
 * $Created by $Sumeet $Kumar 2020.
 */

/*
 * $Created by $Sumeet $Kumar 2019.
 */

package life.mibo.android.models.base


import com.google.gson.annotations.SerializedName

//@JsonAdapter(AlwaysListTypeAdapterFactory::class)
public data class BaseError(
    @SerializedName("code")
    var code: Int?,
    @SerializedName("message")
    var message: String?
) : BaseModel