/*
 *  Created by Sumeet Kumar on 2/1/20 8:50 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/1/20 8:40 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.base

import life.mibo.hexa.pods.pod.LightLogic
import life.mibo.hexa.pods.pod.Players
import life.mibo.hexa.pods.pod.Program

interface RXLParser {
    fun type(): LightLogic
    fun name(): String
    fun player(): Players
    //fun program(): Program
}