/*
 *  Created by Sumeet Kumar on 2/27/20 12:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 9:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base

import com.google.gson.annotations.SerializedName


class TrainerID(trainerId: Int?, token: String?, request: String) :
    BasePost<TrainerID.ID>(ID(trainerId), request, token) {

    data class ID(@SerializedName("TrainerID") var id: Int?)
}