/*
 *  Created by Sumeet Kumar on 2/11/20 10:06 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 10:06 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.impl

import life.mibo.hexa.models.rxl.RXLPrograms
import life.mibo.hexa.models.rxl.RxlExercises

interface ReactionObserver {
    fun onDataReceived(programs: ArrayList<RxlExercises.Program>)
    fun onUpdateList(programs: ArrayList<RxlExercises.Program>)
}