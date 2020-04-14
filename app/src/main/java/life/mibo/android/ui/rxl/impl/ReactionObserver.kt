/*
 *  Created by Sumeet Kumar on 2/11/20 10:06 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 10:06 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl.impl

import life.mibo.android.models.rxl.RxlProgram

interface ReactionObserver {
    fun onDataReceived(programs: ArrayList<RxlProgram>)
    fun onUpdateList(programs: ArrayList<RxlProgram>)
}