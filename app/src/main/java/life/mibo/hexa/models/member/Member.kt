package life.mibo.hexa.models.member


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseModel

data class Member(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("errors")
    var errors: List<Any?>?,
    @SerializedName("status")
    var status: String?
): BaseModel()