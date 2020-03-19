/*
 *  Created by Sumeet Kumar on 3/5/20 12:17 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/5/20 12:11 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl

data class Event(val id: Int, val actionTime: Int, val tapTime: Int, var isFocus: Boolean = true)