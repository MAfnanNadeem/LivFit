/*
 *  Created by Sumeet Kumar on 2/29/20 11:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/29/20 11:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.rxl


import com.google.gson.annotations.SerializedName
import life.mibo.hexa.models.base.BasePost

class DeleteRxlExercise(data: Data, token: String?) :
    BasePost<DeleteRxlExercise.Data>(data, "DeleteRXLExerciseProgram", token) {

    data class Data(
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("RXLProgramID")
        var rXLProgramID: String?
    )
}