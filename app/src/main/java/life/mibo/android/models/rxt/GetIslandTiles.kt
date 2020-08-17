package life.mibo.android.models.rxt


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseResponse

class GetIslandTiles(data: Data?) : BaseResponse<GetIslandTiles.Data?>(data) {
    data class Data(
            @SerializedName("ID")
            var iD: Int?,
            @SerializedName("IslandId")
            var islandId: Int?,
            @SerializedName("MemberID")
            var memberID: Int?,
            @SerializedName("Tiles")
            var tiles: String?
    )
}