package life.mibo.android.models.rxt


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class GetIslandPost(data: Data, token: String?) : BasePost<GetIslandPost.Data?>(data, "GetIslandTiles", token) {

    data class Data(
            @SerializedName("IslandId")
            var islandId: Int?,
            @SerializedName("MemberID")
            var memberID: Int?,
            @SerializedName("LocationId")
            var locationId: String?
    )
}