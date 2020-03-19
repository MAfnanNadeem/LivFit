/*
 *  Created by Sumeet Kumar on 3/4/20 4:00 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 9:03 AM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.parser

import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.rxl.program.RxlPlayer
import life.mibo.hardware.rxl.program.RxlProgram

class SinglePlayerParser(program: RxlProgram, listener: Listener) :
    RxlParser(program, listener, "SinglePlayerParser") {
    override fun onCycleStart(player: RxlPlayer) {
        TODO("Not yet implemented")
    }

    override fun onCycleStart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCycleTapStart(playerId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNext(player: RxlPlayer, event: RxlStatusEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeCycle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}