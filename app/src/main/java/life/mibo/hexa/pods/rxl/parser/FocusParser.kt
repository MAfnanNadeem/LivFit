/*
 *  Created by Sumeet Kumar on 2/1/20 8:37 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 8:37 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.parser

import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hexa.pods.rxl.program.RxlPlayer
import life.mibo.hexa.pods.rxl.program.RxlProgram

class FocusParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "FocusParser") {
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