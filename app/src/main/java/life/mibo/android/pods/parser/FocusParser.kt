/*
 *  Created by Sumeet Kumar on 2/1/20 8:37 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 8:37 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.pods.parser

import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.android.pods.base.RXLParser
import life.mibo.hardware.rxl.program.PlayerType
import life.mibo.android.pods.rxl.program.RxlLight

class FocusParser : RXLParser {
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