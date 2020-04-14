/*
 *  Created by Sumeet Kumar on 2/15/20 11:26 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/15/20 11:26 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.rxl


import com.google.gson.annotations.SerializedName
import life.mibo.android.models.base.BasePost

class DeleteRXLProgram(data: DeleteProgramData, token: String?) :
    BasePost<DeleteRXLProgram.DeleteProgramData>(data, "DeleteRXLProgram", token) {
    data class DeleteProgramData(
        @SerializedName("MemberID")
        var memberID: String?,
        @SerializedName("RXLProgramID")
        var rXLProgramID: String?
    )

    constructor(memberID: String?, programID: String?, token: String?) : this(
        DeleteProgramData(memberID, programID), token
    )
}