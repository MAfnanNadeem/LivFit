/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.session


import com.google.gson.annotations.SerializedName

data class MuscleChannels(
    @SerializedName("Abs")
    var abs: String?,
    @SerializedName("Arms")
    var arms: String?,
    @SerializedName("Back")
    var back: String?,
    @SerializedName("Calfs")
    var calfs: String?,
    @SerializedName("Chest")
    var chest: String?,
    @SerializedName("Glutes")
    var glutes: String?,
    @SerializedName("Hamstrings")
    var hamstrings: String?,
    @SerializedName("Quads")
    var quads: String?,
    @SerializedName("Traps")
    var traps: String?,
    @SerializedName("Upperback")
    var upperback: String?
)