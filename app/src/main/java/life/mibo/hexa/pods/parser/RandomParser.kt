/*
 *  Created by Sumeet Kumar on 2/1/20 8:38 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 2:16 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.parser

import life.mibo.hexa.pods.base.RXLParser
import life.mibo.hexa.pods.pod.LightLogic
import life.mibo.hexa.pods.pod.Players

class RandomParser : RXLParser {
    override fun type(): LightLogic {
        return LightLogic.RANDOM
    }

    override fun name(): String {
       return "Random"
    }

    override fun player(): Players {
       return Players.SINGLE
    }
}