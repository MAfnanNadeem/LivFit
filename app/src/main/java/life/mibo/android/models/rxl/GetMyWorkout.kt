package life.mibo.android.models.rxl


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetMyWorkout(data: Data?, token: String?) :
    BasePost<GetMyWorkout.Data?>(data, "GetMyWorkout", token) {
    data class Data(
        @SerializedName("MemberId")
        var memberId: String?,
        @SerializedName("PageNo")
        var pageNo: Int?,
        @SerializedName("PageSize")
        var pageSize: Int?,
        @SerializedName("Search")
        var search: String?
    )
}