package life.mibo.android.models.rxl


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class SaveMyWorkout(data: Data?, token: String?) :
    BasePost<SaveMyWorkout.Data?>(data, "SaveMyWorkout", token) {

    data class Data(
        @SerializedName("Accessories")
        var accessories: List<String?>?,
        @SerializedName("Avatar")
        var avatar: String?,
        @SerializedName("Category")
        var category: List<String?>?,
        @SerializedName("Description")
        var description: String?,
        @SerializedName("MemberId")
        var memberId: String?,
        @SerializedName("Minute")
        var minute: String?,
        @SerializedName("Name")
        var name: String?,
        @SerializedName("Pods")
        var pods: String?,
        @SerializedName("RxlBlock")
        var rxlBlock: List<RxlBlock?>?,
        @SerializedName("RxlPlayers")
        var rxlPlayers: String?,
        @SerializedName("Second")
        var second: String?,
        @SerializedName("VideoLink")
        var videoLink: String?,
        @SerializedName("WorkStation")
        var workStation: String?,
        @SerializedName("WorkoutTags")
        var workoutTags: List<String?>?
    )

    data class RxlBlock(
        @SerializedName("Action")
        var action: String?,
        @SerializedName("ActivityOn")
        var activityOn: String?,
        @SerializedName("AssignedColors")
        var assignedColors: String?,
        @SerializedName("Delay")
        var delay: String?,
        @SerializedName("DistractingColors")
        var distractingColors: String?,
        @SerializedName("Pattern")
        var pattern: String?,
        @SerializedName("Pause")
        var pause: String?,
        @SerializedName("Round")
        var round: String?,
        @SerializedName("RxlType")
        var rxlType: String?,
        @SerializedName("RxlVideoLink")
        var rxlVideoLink: String?,
        @SerializedName("TotalDuration")
        var totalDuration: String?
    )
}