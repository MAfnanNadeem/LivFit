/*
 *  Created by Sumeet Kumar on 2/16/20 9:00 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/16/20 8:58 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.pods.rxl

import android.util.SparseArray

class RxlStation(val type: StationType = StationType.ONE) {

    private val stations = SparseArray<RxlColor>()
    private var count = 0

    fun add(activeColor: Int, destructiveColor: Int): RxlStation {
        if (type == StationType.ONE)
            count = 1
        else
            count++
        stations.put(count, RxlColor(activeColor, destructiveColor))
        return this
    }

    fun addColor(activeColor: Int, destructiveColor: Int, position: Int): RxlStation {
        if (type == StationType.ONE)
            count = 1
        else
            count++
        stations.put(count, RxlColor(activeColor, destructiveColor, position))
        return this
    }

    fun add(type: Int, activeColor: Int, destructiveColor: Int): RxlStation {
        stations.put(type, RxlColor(activeColor, destructiveColor))
        return this
    }

    fun list(): SparseArray<RxlColor> = stations

    fun getActiveColor(): Int {
        if (count == 1)
            return stations[count].activeColor
        return 0
    }

    fun getActivePosition(): Int {
        if (count == 1)
            return stations[count].position
        return 0
    }


    fun getDestructiveColor(): Int {
        if (count == 1)
            return stations[count].destructiveColor
        return 0
    }


    fun get2ndActiveColor(): Int {
        if (count > 1)
            return stations[2].activeColor
        return 0
    }


    fun get2ndDestructiveColor(): Int {
        if (count > 1)
            return stations[2].destructiveColor
        return 0
    }

    fun get3rdActiveColor(): Int {
        if (count > 2)
            return stations[3].activeColor
        return 0
    }

    fun get3rdDestructiveColor(): Int {
        if (count > 2)
            return stations[3].destructiveColor
        return 0
    }

    fun get4hActiveColor(): Int {
        if (count > 3)
            return stations[4].activeColor
        return 0
    }

    fun get4thDestructiveColor(): Int {
        if (count > 3)
            return stations[4].destructiveColor
        return 0
    }

}