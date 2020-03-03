/*
 *  Created by Sumeet Kumar on 1/27/20 10:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 10:02 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.impl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.create_pods_grid.view.*
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.ItemClickListener
import java.util.*
import kotlin.collections.ArrayList


class CourseCreateImpl(var context: Context, var listener: Listener? = null) {

    interface Listener {
        fun onDialogItemSelected(item: ReflexDialog.Item, type: Int)
    }

    //var listener: Listener? = null

    enum class Type(val type: Int) {
        STATIONS(1), CYCLES(2), PODS(3), LIGHT_LOGIC(4), PLAYERS(5), DELAY(6),
        DURATION(7), ACTION(8), STRUCTURE(9), PLAYER_1(11), PLAYER_2(12), PLAYER_3(13), PLAYER_4(14)
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

    fun showPlayers(type: Type, max: Int) {

        if (max > 2) {
            val list = ArrayList<ReflexDialog.Item?>()
            val title = "Select No.of Pods"

            for (i in 2..max) {
                list.add(ReflexDialog.Item(i, "$i"))
            }

            showDialog(list, title, type.type)
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
                list.add(ReflexDialog.Item(4, "All at Once"))
                list.add(ReflexDialog.Item(5, "Tap at All"))
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
        ReflexDialog(context!!, list, title, null)
    }


    private fun showDialog(list: ArrayList<ReflexDialog.Item?>, title: String, type: Int) {
        val dialog = ReflexDialog(
            context!!, list, title,
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


    fun createSequenceList(recyclerView: RecyclerView, device: Int = 4) {
        val items = ArrayList<Int>(device)
        for (i in 1 until device.plus(1))
            items.add(i)
        recyclerView.layoutManager =
            GridLayoutManager(recyclerView.context, device, RecyclerView.VERTICAL, false)
        val adapter = MyAdapter(items)
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled() = true
            override fun isItemViewSwipeEnabled() = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags =
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags =
                    if (isItemViewSwipeEnabled) ItemTouchHelper.START or ItemTouchHelper.END else 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if (viewHolder.itemViewType != target.itemViewType)
                    return false
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                val item = items.removeAt(fromPosition)
                items.add(toPosition, item)
                recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                items.remove(position)
                recyclerView.adapter!!.notifyItemRemoved(position)
            }


        })
        //itemTouchHelper.attachToRecyclerView(recyclerView)
        val touch = ItemTouchHelper(TouchCallback(adapter))
        touch.attachToRecyclerView(recyclerView)
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var text = view.text1
    }

    class MyAdapter(val list: ArrayList<Int>) : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.create_pods_grid,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = list[position]
            //holder.itemView.setBackgroundColor(if (data % 2 == 0) 0xffff0000.toInt() else 0xff00ff00.toInt())
            holder.text.text = "$data"
        }
    }

    class TouchCallback(var mAdapter: MyAdapter) : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        ItemTouchHelper.DOWN or ItemTouchHelper.UP
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(mAdapter.list, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(mAdapter.list, i, i - 1)
                }
            }
            mAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val svH: MyViewHolder = viewHolder as MyViewHolder
//            val index: Int = mAdapter.getCapitolos().indexOf(svH.currentItem)
//            mAdapter.getCapitolos().remove(svH.currentItem)
//            mAdapter.notifyItemRemoved(index)
//            if (emptyView != null) {
//                if (mAdapter.getCapitolos().size() > 0) {
//                    emptyView.setVisibility(TextView.GONE)
//                } else {
//                    emptyView.setVisibility(TextView.VISIBLE)
//                }
//            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
        }

    }

}