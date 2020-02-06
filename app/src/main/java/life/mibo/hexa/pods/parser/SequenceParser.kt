/*
 *  Created by Sumeet Kumar on 2/1/20 8:40 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 8:39 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.parser

import life.mibo.hexa.pods.base.RXLParser
import life.mibo.hexa.pods.pod.LightLogic
import life.mibo.hexa.pods.pod.Players

class SequenceParser : RXLParser {
    private var player: life.mibo.hexa.pods.base.Players? = null

    override fun type(): LightLogic {
        return LightLogic.RANDOM
    }

    override fun name(): String {
        return "Sequence"
    }

    override fun player(): Players {
        return player?.player() ?: Players.SINGLE
    }
}