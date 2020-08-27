package life.mibo.android.models.workout


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SearchWorkoutPost(data: Data?, token: String?) : BasePost<SearchWorkoutPost.Data?>(data, "SearchWorkout", token) {

    data class Data(
            @SerializedName("Element")
            var element: String?,
            @SerializedName("MemberID")
            var memberID: String?,
            @SerializedName("PageNo")
            var pageNo: String?,
            @SerializedName("PageSize")
            var pageSize: String?,
            @SerializedName("UserType")
            var userType: String?,
            @SerializedName("LocationID")
            var locationID: String?,
            @SerializedName("IslandID")
            var islandID: String?,
            @SerializedName("Search")
            var search: String?
    )
}