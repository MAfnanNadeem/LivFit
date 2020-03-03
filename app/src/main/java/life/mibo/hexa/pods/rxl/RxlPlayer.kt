/*
 *  Created by Sumeet Kumar on 2/16/20 9:31 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/16/20 9:31 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl

import life.mibo.hardware.models.Device

class RxlPlayer(
    var id: Int,
    var name: String,
    var color: Int,
    var colorId: Int,
    var noOfPods: Int,
    var pods: ArrayList<Device>
) {


    var station = RxlStation().addColor(color, 0, colorId)

    enum class Player {
        SINGLE, TWO_PLAYER, MULTI_PLAYERS;
    }
}