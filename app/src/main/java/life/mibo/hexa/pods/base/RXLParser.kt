/*
 *  Created by Sumeet Kumar on 2/1/20 8:50 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 8:40 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.base

import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.rxl.program.PlayerType
import life.mibo.hexa.pods.rxl.program.RxlLight

interface RXLParser {
    fun type(): RxlLight
    fun name(): String
    fun player(): PlayerType
    //fun program(): Program
    fun onEvent(event: RxlStatusEvent)

    fun onStart()
    fun onStop()
    fun onReset()

    fun startPublish(){


    }
}