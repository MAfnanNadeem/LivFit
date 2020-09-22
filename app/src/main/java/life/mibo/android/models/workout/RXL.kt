package life.mibo.android.models.workout

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import life.mibo.android.ui.rxl.adapter.PlayersAdapter
import java.io.Serializable

data class RXL(
    @SerializedName("Accessories")
    var accessories:  List<String?>?,
    @SerializedName("Blocks")
    var blocks: List<RXLBlock?>?,
    @SerializedName("Category")
    var category: List<String?>?,
    @SerializedName("ProximityValue")
    var proximityValue: String?,
    @SerializedName("RXLPlayers")
    var rXLPlayers: Int?,
    @SerializedName("RXLPods")
    var rXLPods: Int?,
    @SerializedName("TapProximity")
    var tapProximity: String?,
    @SerializedName("VoicePrompt")
    var voicePrompt: String?,
    @SerializedName("WorkStation")
    var workStation: Int?
) : Serializable {

    var id: Int = 0
    var borg: Int = 7
    var name: String = ""
    var desc: String = ""
    var total: String = "0"
    var unit: String = "seconds"
    var icon: String = ""
    var videoLink: String = ""

    fun duration(): Int {
        try {
            return total.toIntOrNull() ?: 0
        } catch (e: Exception) {

        }
        return 0
    }

    fun pods() = rXLPods ?: 4
    fun players() = rXLPlayers ?: 1
    fun stations() = workStation ?: 1
    fun isTap() = tapProximity?.toLowerCase()?.equals("tap")
    fun proximity() = tapProximity?.toLowerCase()?.equals("proximity")

    fun getLinks(): List<String> {
        val list = ArrayList<String>()
        try {
            if (videoLink.length > 1)
                list.add(videoLink)
            blocks?.let {
                for (b in it) {
                    if (b?.videoLink?.length ?: 0 > 1)
                        list.add(b!!.videoLink!!)
                }
            }
        } catch (e: java.lang.Exception) {
        }
        return list
    }


    var isFavourite: Boolean = false

    @Transient
    var selectedPlayers: ArrayList<PlayersAdapter.PlayerItem>? = null

    data class RXLBlock(
        @SerializedName("ActivityOn")
        var activityOn: String?,
        @SerializedName("AssignedColors")
        @Transient var assignedColors: String?,
        @SerializedName("DistractingColors")
        @Transient var distractingColors: String?,
        @SerializedName("Pattern")
        var pattern: String?,
        @SerializedName("RXLAction")
        var rXLAction: Int?,
        @SerializedName("RXLDelay")
        var rXLDelay: Int?,
        @SerializedName("RXLPause")
        var rXLPause: Int?,
        @SerializedName("RXLRound")
        var rXLRound: Int?,
        @SerializedName("RXLTotalDuration")
        var rXLTotalDuration: Int?,
        @SerializedName("RXLType")
        var rXLType: String?,
        @SerializedName("VideoLink")
        var videoLink: String?
    ) : Serializable {
        fun isRandom() = rXLType?.toLowerCase()?.contains("random")
        fun isSequence() = rXLType?.toLowerCase()?.contains("sequence")

        fun getDelay(): Int {
            return rXLDelay ?: 0
        }

        fun getAction(): Int {
            return rXLAction?.times(1000) ?: 1000
        }
        fun getActionSec(): Int {
            return rXLAction ?: 1
        }

        fun getDuration(): Int {
            return rXLTotalDuration ?: 60
        }

        fun getRounds(): Int {
            return rXLRound ?: 1
        }

        fun getLogicType(): Int {
            when (rXLType?.toLowerCase()) {
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
}