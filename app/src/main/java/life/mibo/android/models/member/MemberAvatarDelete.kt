/*
 *  Created by Sumeet Kumar on 10/17/20 12:00 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 10/17/20 12:00 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.member

import life.mibo.android.models.base.BaseResponse

class MemberAvatarDelete(data: List<DataX?>?) :
    BaseResponse<List<MemberAvatarDelete.DataX?>?>(data) {

    data class DataX(
        val code: Int?,
        val message: String?
    )
}