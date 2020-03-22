/*
 *  Created by Sumeet Kumar on 2/1/20 8:40 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 8:39 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.parser

import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hexa.pods.base.RXLParser
import life.mibo.hardware.rxl.program.PlayerType
import life.mibo.hexa.pods.rxl.program.RxlLight

class SequenceParser : RXLParser {
    //private var player: life.mibo.hexa.pods.base.Players? = null

    override fun type(): RxlLight {
        return RxlLight.RANDOM
    }

    override fun name(): String {
        return "Random"
    }

    override fun player(): PlayerType {
        return PlayerType.SINGLE
    }

    override fun onEvent(event: RxlStatusEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReset() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}