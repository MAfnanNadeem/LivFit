/*
 *  Created by Sumeet Kumar on 2/11/20 9:33 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 9:33 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.rxl

import life.mibo.hexa.models.base.BasePost
import life.mibo.hexa.models.base.MemberID

class GetRXLProgram(member: MemberID, token: String?, requestType: String = "GetRXLProgram") :
    BasePost<MemberID>(member, requestType, token) {
}