package life.mibo.android.models.rxt


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SaveIslandPost(data: Data, token: String?) : BasePost<SaveIslandPost.Data?>(data, "SaveIslandTiles", token) {

    data class Data(
            @SerializedName("IslandId")
            var islandId: Int?,
            @SerializedName("MemberID")
            var memberID: Int?,
            @SerializedName("LocationId")
            var locationId: String?,
            @SerializedName("Tiles")
            var tiles: String?
    )
}