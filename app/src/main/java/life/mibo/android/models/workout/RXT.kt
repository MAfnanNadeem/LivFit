package life.mibo.android.models.workout

import com.google.gson.annotations.SerializedName
import life.mibo.android.ui.rxt.parser.RxtTile
import java.io.Serializable

data class RXT(
    @SerializedName("Blocks")
        var blocks: List<RXTBlock?>?,
    @SerializedName("Category")
        var category: Any?,
    @SerializedName("RXTIsland")
        var rXTIsland: RXTIsland?,
    @SerializedName("WorkStationHeight")
        var workStationHeight: Int?,
    @SerializedName("WorkStationWidth")
        var workStationWidth: Int?,
    @SerializedName("VoicePrompt")
        var voicePrompt: String?
) : Serializable {
    data class RXTBlock(
            @SerializedName("RXTAction")
            var rXTAction: Int?,
            @SerializedName("RXTDelay")
            var rXTDelay: Int?,
            @SerializedName("RXTPause")
            var rXTPause: Int?,
            @SerializedName("RXTRound")
            var rXTRound: Int?,
            @SerializedName("RXTTotalDuration")
            var rXTTotalDuration: Int?,
            @SerializedName("RXTType")
            var rXTType: String?,
            @SerializedName("VideoLink")
            var videoLink: Any?,
            @SerializedName("RXTPattern")
            var pattern: String?
    ) : Serializable {
        fun isRandom() = rXTType?.toLowerCase()?.contains("random")
        fun isSequence() = rXTType?.toLowerCase()?.contains("sequence")

        fun getDelay(): Int {
            return rXTDelay ?: 0
        }

        fun getAction(): Int {
            return rXTAction?.times(1000) ?: 1000
        }

        fun getDuration(): Int {
            return rXTTotalDuration ?: 60
        }

        fun getRounds(): Int {
            return rXTRound ?: 1
        }

        fun getLogicType(): Int {
            when (rXTType?.toLowerCase()) {
                "sequence" -> return 1
                "random" -> return 2
                "all at once" -> return 3
                "single sequence" -> return 4
                "double sequence" -> return 5
                "hopscotch" -> return 6
            }
            return 1
        }
    }

    data class RXTIsland(
            @SerializedName("Id")
            var id: String?,
            @SerializedName("IslandHeight")
            var islandHeight: String?,
            @SerializedName("IslandImage")
            var islandImage: String?,
            @SerializedName("IslandWidth")
            var islandWidth: String?,
            @SerializedName("Name")
            var name: String?,
            @SerializedName("TotalTiles")
            var totalTiles: String?
    ) : Serializable {
        var tiles = ArrayList<RxtTile>()

        fun addTiles(list: List<RxtTile>) {
            tiles.clear()
            tiles.addAll(list)
        }

        fun getID(): Int {
            return id?.toIntOrNull() ?: 0
        }

        fun getX(): Int {
            return islandWidth?.toIntOrNull() ?: 0
        }

        fun getY(): Int {
            return islandHeight?.toIntOrNull() ?: 0
        }

        fun getTotal(): Int {
            return totalTiles?.toIntOrNull() ?: 0
        }
    }
}