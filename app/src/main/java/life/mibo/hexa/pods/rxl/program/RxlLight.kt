/*
 *  Created by Sumeet Kumar on 2/16/20 9:04 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/16/20 9:04 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl.program

enum class RxlLight(var type: String) {
    RANDOM(""), SEQUENCE(""), FOCUS(""), ALL_AT_ONCE(""), TAP_AT_ONCE(""), TAP_AT_ALL(""),
    ALL_AT_ALL(""), HOME_BASED(""), UNKNOWN("")
}