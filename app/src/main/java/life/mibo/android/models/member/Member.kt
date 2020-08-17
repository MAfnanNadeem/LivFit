package life.mibo.android.models.member


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import life.mibo.android.core.gson.AlwaysListTypeAdapterFactory
import life.mibo.android.models.base.BaseModel
import life.mibo.android.models.base.BaseError

data class Member(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("errors")
    @JsonAdapter(AlwaysListTypeAdapterFactory::class)
    var errors: List<BaseError?>?,
    @SerializedName("status")
    var status: String?
): BaseModel