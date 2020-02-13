/*
 *  Created by Sumeet Kumar on 1/27/20 10:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 10:02 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.impl

import android.view.View
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener

class CourseCreateImpl(var fragment: BaseFragment, var listener: Listener? = null) {

    interface Listener {
        fun onDialogItemSelected(item: ReflexDialog.Item, type: Int)
    }

    //var listener: Listener? = null

    enum class Type(val type: Int) {
        STATIONS(1), CYCLES(2), PODS(3), LIGHT_LOGIC(4), PLAYERS(5), DELAY(6),
        DURATION(7), ACTION(8), STRUCTURE(9)
    }

    fun bindViews(root: View) {

    }

    fun getTitle(type: Type): String {
        when (type) {
            Type.STATIONS -> {
                return "1"
            }
            Type.CYCLES -> {
                return "1"
            }
            Type.PODS -> {
                return "4"
            }
            Type.LIGHT_LOGIC -> {
                return "Sequence"
            }
            Type.PLAYERS -> {
                return "1"
            }
            Type.DELAY -> {
                return "0 sec"
            }
            Type.DURATION -> {
                return "30 Sec"
            }
            Type.ACTION -> {
                return "1 sec"
            }
            else -> {
                return ""
            }
        }

    }

    fun showDialog(type: Type) {
        val list = ArrayList<ReflexDialog.Item?>()
        var title = ""
        list.clear()

        when (type) {
            Type.STATIONS -> {
                title = "Select Working Stations"
                for (i in 1..4) {
                    list.add(ReflexDialog.Item(i, "$i"))
                }
            }
            Type.CYCLES -> {
                title = "Select Cycles"
                for (i in 1..10) {
                    list.add(ReflexDialog.Item(i, "$i"))
                }
            }
            Type.PODS -> {
                title = "Select No.of Pods"
                for (i in 3..16) {
                    list.add(ReflexDialog.Item(i, "$i"))
                }
            }
            Type.LIGHT_LOGIC -> {
                title = "Lights turn On/Off"
                list.add(ReflexDialog.Item(1, "Sequence"))
                list.add(ReflexDialog.Item(2, "Random"))
                list.add(ReflexDialog.Item(3, "Focus"))
                list.add(ReflexDialog.Item(4, "All at once"))
            }
            Type.PLAYERS -> {
                title = "Select Players"
                for (i in 1..4) {
                    list.add(ReflexDialog.Item(i, "$i"))
                }
            }
            Type.DELAY -> {
                title = "Choose Delay (Seconds)"
                for (i in 0..10) {
                    list.add(ReflexDialog.Item(i, "$i seconds"))
                }
            }
            Type.DURATION -> {
                list.add(ReflexDialog.Item(0, "5 seconds"))
                for (i in 1..10) {
                    list.add(ReflexDialog.Item(i, "${i.times(15)} seconds"))
                }
                title = "Total Duration (Seconds)"
            }
            Type.ACTION -> {
                for (i in 1..5) {
                    list.add(ReflexDialog.Item(i, "$i seconds"))
                }
                title = "Action Duration (Seconds)"
            }
            Type.STRUCTURE -> {
                list.add(ReflexDialog.Item(21, "Agility"))
                list.add(ReflexDialog.Item(22, "Balanced"))
                list.add(ReflexDialog.Item(23, "Core"))
                list.add(ReflexDialog.Item(29, "Cardio"))
                list.add(ReflexDialog.Item(29, "Coordination"))
                list.add(ReflexDialog.Item(29, "Fitness Test"))
                list.add(ReflexDialog.Item(24, "Flexibility"))
                list.add(ReflexDialog.Item(29, "Functional"))
                list.add(ReflexDialog.Item(29, "Power"))
                list.add(ReflexDialog.Item(26, "Reaction Time"))
                list.add(ReflexDialog.Item(27, "Speed"))
                list.add(ReflexDialog.Item(28, "Stamina"))
                list.add(ReflexDialog.Item(29, "Strength"))
                list.add(ReflexDialog.Item(29, "Suspension"))
                title = "Select Structure"
            }
        }
        showDialog(list, title, type.type)
    }

    fun dialogTest(title: String) {
        val list = ArrayList<ReflexDialog.Item?>()
        for (i in 1..10) {
            list.add(ReflexDialog.Item(i, "$i"))
        }
        ReflexDialog(fragment.context!!, list, title, null)
    }


    private fun showDialog(list: ArrayList<ReflexDialog.Item?>, title: String, type: Int) {
        val dialog = ReflexDialog(
            fragment.context!!, list, title,
            object : ItemClickListener<ReflexDialog.Item> {
                override fun onItemClicked(item: ReflexDialog.Item?, position: Int) {
                    item?.let {
                        listener?.onDialogItemSelected(it, position)
                    }
                }

            }, type
        )
        dialog.show()
    }
}