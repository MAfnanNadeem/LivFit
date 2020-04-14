/*
 *  Created by Sumeet Kumar on 1/23/20 5:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 5:15 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.program

import life.mibo.android.models.base.BasePost

data class ProgramPost(
    var item: ProgramPostData = ProgramPostData(),
    var auth: String,
    var type: String? = "SearchPrograms"
) : BasePost<ProgramPostData>(item, type, auth)