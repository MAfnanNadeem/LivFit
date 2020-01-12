/*
 *  Created by Sumeet Kumar on 1/12/20 11:50 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/12/20 11:50 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.calories


import com.google.gson.annotations.SerializedName

data class CaloriesData(
    @SerializedName("calories_burnt")
    var caloriesBurnt: Int?,
    @SerializedName("end_datetime")
    var endDatetime: String?,
    @SerializedName("program_circuit_name")
    var programCircuitName: String?,
    @SerializedName("schedule_id")
    var scheduleId: Int?,
    @SerializedName("service_name")
    var serviceName: String?,
    @SerializedName("session_member_reports_id")
    var sessionMemberReportsId: Int?,
    @SerializedName("session_report_id")
    var sessionReportId: Int?,
    @SerializedName("start_datetime")
    var startDatetime: String?,
    @SerializedName("trainer_id")
    var trainerId: Int?,
    @SerializedName("trainer_name")
    var trainerName: String?
) {


}