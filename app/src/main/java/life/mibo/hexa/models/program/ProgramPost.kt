/*
 *  Created by Sumeet Kumar on 1/15/20 3:33 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/15/20 3:33 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.models.program

import life.mibo.hexa.models.base.BasePost


class ProgramPost(
    data: DataPost,
    requestType: String,
    auth: String?
) : BasePost<DataPost>(data, requestType, auth)