package life.mibo.hexa.models.member


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.hexa.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.hexa.models.base.BaseModel
import life.mibo.hexa.models.base.Error

data class Member(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("errors")
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    var errors: List<Error?>?,
    @SerializedName("status")
    var status: String?
): BaseModel