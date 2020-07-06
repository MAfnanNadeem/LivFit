/*
 *  Created by Sumeet Kumar on 7/5/20 4:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/5/20 4:40 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit.fitbit


import com.google.gson.annotations.SerializedName

//https://api.fitbit.com/1/user/-/activities/steps/date/2020-06-01/2020-07-05/15min.json
data class StepsData(
    @SerializedName("activities-steps")
    var list: List<Step?>?
)