/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.models.session


import com.google.gson.annotations.SerializedName

data class SessionMemberReports(
    @SerializedName("calories_burnt")
    var caloriesBurnt: Int?,
    @SerializedName("channel_values")
    var channelValues: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("member_id")
    var memberId: Int?,
    @SerializedName("peak_hr")
    var peakHr: Int?,
    @SerializedName("resting_hr")
    var restingHr: Int?,
    @SerializedName("session_count")
    var sessionCount: Int?,
    @SerializedName("session_report_id")
    var sessionReportId: Int?,
    @SerializedName("trainer_feedback")
    var trainerFeedback: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("user_rating")
    var userRating: Int?,
    @SerializedName("weight")
    var weight: String?,
    @SerializedName("variable_hr")
    var variableHr: String?
)