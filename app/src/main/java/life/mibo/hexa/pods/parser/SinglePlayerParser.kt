/*
 *  Created by Sumeet Kumar on 2/1/20 8:45 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 8:45 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.parser

import life.mibo.hexa.pods.base.Players

class SinglePlayerParser : Players{
    override fun player(): life.mibo.hexa.pods.pod.Players {
        return life.mibo.hexa.pods.pod.Players.SINGLE
    }
}