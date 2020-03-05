/*
 *  Created by Sumeet Kumar on 3/4/20 3:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/4/20 3:56 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hexa.pods.rxl.program.RxlPlayer
import life.mibo.hexa.pods.rxl.program.RxlProgram

class RandomParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "RandomParser") {
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