/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.models.session


import com.google.gson.annotations.SerializedName

data class SessionReports(
    @SerializedName("breaks")
    var breaks: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("duration")
    var duration: Int?,
    @SerializedName("end_datetime")
    var endDatetime: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("location_id")
    var locationId: Int?,
    @SerializedName("program_circuit_name")
    var programCircuitName: String?,
    @SerializedName("schedule_id")
    var scheduleId: Int?,
    @SerializedName("start_datetime")
    var startDatetime: String?,
    @SerializedName("trainer_id")
    var trainerId: Int?,
    @SerializedName("trainer_issues_log")
    var trainerIssuesLog: String?,
    @SerializedName("trainer_name")
    var trainerName: String?,
    @SerializedName("updated_at")
    var updatedAt: String?
)