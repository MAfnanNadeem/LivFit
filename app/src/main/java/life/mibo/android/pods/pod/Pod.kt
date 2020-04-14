/*
 *  Created by Sumeet Kumar on 1/27/20 1:58 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 1:49 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.pods.pod

import life.mibo.hardware.models.Device

class Pod(val name: String, val uid: String, val address: String) {

    var type: Int = 0

    companion object {
        fun from(device: Device?): Pod? {
            if(device == null)
                return null
            val pod = Pod(device.name, device.uid, device.ipToString)
            pod.type = 1
            return pod
        }
    }


    override fun equals(other: Any?): Boolean {
        if (other is Pod)
            return this.uid == other.uid

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + uid.hashCode()
        return result
    }

}