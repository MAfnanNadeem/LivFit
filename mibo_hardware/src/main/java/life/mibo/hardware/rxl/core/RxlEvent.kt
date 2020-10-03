/*
 *  Created by Sumeet Kumar on 9/8/20 5:25 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 9/8/20 5:25 PM
 *  Mibo Hexa - app
 */

package life.mibo.hardware.rxl.core

data class RxlEvent(val id: Int, val actionTime: Int, val tapTime: Int, var isFocus: Boolean = true)