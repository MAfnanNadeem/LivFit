/*
 *  Created by Sumeet Kumar on 4/5/20 11:34 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/5/20 11:34 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.muscle


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BaseResponse

class MuscleCollection(muscle: List<Muscle?>?) :
    BaseResponse<List<Muscle?>?>(muscle) {


}