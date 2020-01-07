/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.session


import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("heart_rate")
    var heartRate: List<String?>?,
    @SerializedName("muscle_channels")
    var muscleChannels: MuscleChannels?,
    @SerializedName("session_member_reports")
    var sessionMemberReports: SessionMemberReports?,
    @SerializedName("session_reports")
    var sessionReports: SessionReports?,
    @SerializedName("weight")
    var weight: String?
)