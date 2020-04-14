/*
 *  Created by Sumeet Kumar on 2/27/20 12:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 9:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base


class MemberPost(memberId: String, token: String, request: String = "GetRXLExerciseProgram") :
    BasePost<MemberID>(MemberID(memberId), request, token)