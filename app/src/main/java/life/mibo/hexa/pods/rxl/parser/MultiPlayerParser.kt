/*
 *  Created by Sumeet Kumar on 3/4/20 3:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 9:03 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hexa.pods.rxl.program.RxlPlayer
import life.mibo.hexa.pods.rxl.program.RxlProgram

class MultiPlayerParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "MultiPlayerParser") {
    override fun onCycleStart(player: RxlPlayer) {

    }

    override fun onCycleStart() {

    }

    override fun onCycleTapStart(playerId: Int) {

    }

    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {

    }

    override fun completeCycle() {

    }

}