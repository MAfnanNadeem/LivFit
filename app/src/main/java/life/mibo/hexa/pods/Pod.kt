/*
 *  Created by Sumeet Kumar on 1/21/20 8:43 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/21/20 8:43 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods

import life.mibo.hardware.models.Device

class Pod(val name: String, val uid: String, val address: String) {

    companion object {
        fun from(device: Device?): Pod? {
            if(device == null)
                return null
            val pod = Pod(device.name, device.uid, device.id)
            return pod
        }
    }

}