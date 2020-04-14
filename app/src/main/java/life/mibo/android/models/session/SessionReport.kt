/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.session


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.android.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.android.models.base.BaseModel
import life.mibo.android.models.base.Error

data class SessionReport(
    @SerializedName("data")
    var report: Report?,
    @SerializedName("error")
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    var error: List<Error?>?,
    @SerializedName("status")
    var status: String?
): BaseModel