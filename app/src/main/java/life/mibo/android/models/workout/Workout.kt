package life.mibo.android.models.workout

import com.google.gson.annotations.SerializedName
import java.io.Serializable

public data class Workout(
    @SerializedName("AccessType")
        var accessType: String?,
    @SerializedName("BorgRating")
        var borgRating: Int?,
    @SerializedName("CreatedBy")
        var createdBy: Int?,
    @SerializedName("Description")
        var description: String?,
    @SerializedName("DurationUnit")
        var durationUnit: String?,
    @SerializedName("DurationValue")
        var durationValue: String?,
    @SerializedName("EMSId")
        var eMSId: Int?,
    @SerializedName("Element")
        var element: List<String?>?,
    @SerializedName("FitflixId")
        var fitflixId: Any?,
    @SerializedName("Icon")
        var icon: String?,
    @SerializedName("Id")
        var id: Int?,
    @SerializedName("MemberID")
        var memberID: Any?,
    @SerializedName("Name")
        var name: String?,
    @SerializedName("Program")
        var program: EMS?,
    @SerializedName("RXL")
        var rxl: RXL?,
    @SerializedName("RXT")
        var rxt: RXT?,
    @SerializedName("RXLId")
        var rXLId: Any?,
    @SerializedName("RXTId")
        var rXTId: Int?,
    @SerializedName("TENSId")
        var tENSId: Any?,
    @SerializedName("Tags")
        var tags: List<String?>?,
    @SerializedName("VideoLink")
        var videoLink: String?
) : Serializable {

//        fun getProgram(): SearchWorkout.Program {
//            return Gson().fromJson(program, SearchWorkout.Program::class.java)
//        }
//
//        fun getRxt(): SearchWorkout.RXT {
//            return Gson().fromJson(rxt, SearchWorkout.RXT::class.java)
//        }
//
//        fun getRxl(): SearchWorkout.Program {
//            return Gson().fromJson(rxl, SearchWorkout.Program::class.java)
//        }

    fun getDuration(): String {
        try {
            if (durationUnit?.contains("seconds")!!) {
                val d = durationValue!!.toInt()
                return String.format("%02d:%02d", d.div(60), d % 60)
            }

        } catch (e: Exception) {

        }
        return durationValue ?: ""
    }

    fun getDurationSec(): Int {
        return durationValue?.toIntOrNull() ?: 0
    }

    fun updateCheck() {
        isSelected = !isSelected
    }

    var rxtProgram: Any? = null

    var color: Int = 0

    var isSelected = false


    // fun isSelected() = selected

}