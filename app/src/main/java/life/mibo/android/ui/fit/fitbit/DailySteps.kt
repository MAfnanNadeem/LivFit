/*
 *  Created by Sumeet Kumar on 7/5/20 4:04 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/5/20 4:04 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit.fitbit

import com.google.gson.annotations.SerializedName

class DailySteps(
    @SerializedName("activities-steps")
    var activitiesSteps: List<Step?>?,
    @SerializedName("activities-steps-intraday")
    var activitiesStepsIntraday: StepsIntraday?
) {
    data class StepsIntraday(
        @SerializedName("dataset")
        var dataset: List<Dataset?>?,
        @SerializedName("datasetInterval")
        var datasetInterval: Int?,
        @SerializedName("datasetType")
        var datasetType: String?
    )

    data class Dataset(
        @SerializedName("time")
        var time: String?,
        @SerializedName("value")
        var value: Int?
    )
}