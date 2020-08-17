package life.mibo.android.models.workout

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RXL(
    @SerializedName("Blocks")
            var blocks: List<RXT.RXTBlock?>?,
    @SerializedName("Category")
            var category: Any?,
    @SerializedName("RXTIsland")
            var rXTIsland: String?,
    @SerializedName("WorkStationHeight")
            var workStationHeight: Int?,
    @SerializedName("WorkStationWidth")
            var workStationWidth: Int?
    ) : Serializable