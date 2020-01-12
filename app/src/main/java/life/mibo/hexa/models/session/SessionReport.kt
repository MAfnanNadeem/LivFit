/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.session


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel
import life.mibo.hexa.models.base.Error

data class SessionReport(
    @SerializedName("data")
    var report: Report?,
    @SerializedName("error")
    var error: List<Error?>?,
    @SerializedName("status")
    var status: String?
): BaseModel()