package life.mibo.android.models.workout


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BaseError
import life.mibo.android.models.base.BaseModel

data class SearchWorkout(
        @SerializedName("data")
        var `data`: Data?,
        @SerializedName("errors")
        var errors: List<BaseError?>?,
        @SerializedName("status")
        var status: String?) : BaseModel {

    data class Data(@SerializedName("Workout") var workout: List<Workout?>?)
}