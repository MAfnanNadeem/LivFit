/*
 * $Created by $Sumeet $Kumar 2020.
 */

/*
 * $Created by $Sumeet $Kumar 2019.
 */

package life.mibo.hexa.models.base


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.hexa.core.gson.AlwaysListTypeAdapterFactory

//@JsonAdapter(AlwaysListTypeAdapterFactory::class)
public data class Error(
    @SerializedName("code")
    var code: Int?,
    @SerializedName("message")
    var message: String?
) : BaseModel