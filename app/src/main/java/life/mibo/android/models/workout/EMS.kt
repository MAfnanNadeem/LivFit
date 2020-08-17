package life.mibo.android.models.workout

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EMS(
        @SerializedName("AccessType")
        var accessType: String?,
        @SerializedName("BlockType")
        var blockType: String?,
        @SerializedName("Blocks")
        var blocks: List<EMSBlock?>?,
        @SerializedName("BorgRating")
        var borgRating: Int?,
        @SerializedName("BufferTime")
        var bufferTime: BufferTime?,
        @SerializedName("Category")
        var category: String?,
        @SerializedName("CircuitID")
        var circuitID: Int?,
        @SerializedName("CreatedBy")
        var createdBy: Int?,
        @SerializedName("Description")
        var description: String?,
        @SerializedName("Duration")
        var duration: Duration?,
        @SerializedName("Id")
        var id: Int?,
        @SerializedName("MemberID")
        var memberID: Any?,
        @SerializedName("Name")
        var name: String?,
        @SerializedName("ServiceID")
        var serviceID: Any?,
        @SerializedName("Type")
        var type: String?
) : Serializable {
    data class EMSBlock(
            @SerializedName("Id")
            var id: Int?,
            @SerializedName("Parameter")
            var parameter: Parameter?,
            @SerializedName("ShortName")
            var shortName: String?
    )  : Serializable

    data class Parameter(
            @SerializedName("ActionDuration")
            var actionDuration: ActionDuration?,
            @SerializedName("BlockDuration")
            var blockDuration: BlockDuration?,
            @SerializedName("DownRampDuration")
            var downRampDuration: DownRampDuration?,
            @SerializedName("EmsVideoLink")
            var emsVideoLink: EmsVideoLink?,
            @SerializedName("Frequency")
            var frequency: Frequency?,
            @SerializedName("PauseDuration")
            var pauseDuration: PauseDuration?,
            @SerializedName("PulseWidth")
            var pulseWidth: PulseWidth?,
            @SerializedName("UpRampDuration")
            var upRampDuration: UpRampDuration?,
            @SerializedName("Waveform")
            var waveform: Waveform?
    )  : Serializable {
        data class ActionDuration(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable

        data class BlockDuration(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable

        data class DownRampDuration(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable

        data class EmsVideoLink(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable

        data class Frequency(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable

        data class PauseDuration(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable

        data class PulseWidth(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable

        data class UpRampDuration(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable

        data class Waveform(
                @SerializedName("default")
                var default: String?,
                @SerializedName("desc")
                var desc: String?,
                @SerializedName("max")
                var max: String?,
                @SerializedName("min")
                var min: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("unit")
                var unit: String?,
                @SerializedName("value")
                var value: String?
        )  : Serializable
    }

    data class BufferTime(
            @SerializedName("unit")
            var unit: String?,
            @SerializedName("value")
            var value: Int?
    )  : Serializable

    data class Duration(
            @SerializedName("default")
            var default: String?,
            @SerializedName("format")
            var format: String?,
            @SerializedName("max")
            var max: String?,
            @SerializedName("min")
            var min: String?,
            @SerializedName("unit")
            var unit: String?,
            @SerializedName("value")
            var value: String?
    )  : Serializable
}