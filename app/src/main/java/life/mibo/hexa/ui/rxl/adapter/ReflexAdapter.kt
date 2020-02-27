/*
 *  Created by Sumeet Kumar on 2/11/20 2:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 12:46 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.models.rxl.RXLPrograms
import life.mibo.hexa.models.rxl.RxlExercises
import life.mibo.hexa.ui.base.ItemClickListener
import java.util.*


class ReflexAdapter(var list: ArrayList<RxlExercises.Program>?) :
    RecyclerView.Adapter<ReflexHolder>() {

    //var list: ArrayList<Item>? = null
    private var listener: ItemClickListener<RxlExercises.Program>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReflexHolder {
        return ReflexHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_reflex,
                parent,
                false
            )
        )
    }

    fun setListener(listener: ItemClickListener<RxlExercises.Program>) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): RxlExercises.Program? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: ReflexHolder, position: Int) {
        Logger.e("ReflexAdapter: onBindViewHolder $position")

        holder.bind(getItem(position), listener)
    }


    fun notify(id: Int) {
//        Observable.fromArray(list).flatMapIterable { x -> x }.subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread()).subscribe {
//                if (it.id == id) {
//                }
//            }

        list?.forEachIndexed { index, item ->
            if (item.id == id) {
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    fun delete(program: RxlExercises.Program?) {
        if (program == null)
            return
        var pos = -1
        list?.forEachIndexed { index, item ->
            if (item.id == program.id) {
                pos = index
            }
        }
        if (pos != -1) {
            list?.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }


    fun update(newList: ArrayList<RxlExercises.Program>) {
        if (list == null || list?.isEmpty()!!) {
            list = newList
            notifyDataSetChanged()
            return
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return newList[newItem].id == list!![oldItem].id
            }

            override fun getOldListSize(): Int {
                return list!!.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return true
            }

        })
        list = newList
        result.dispatchUpdatesTo(this)

    }

    fun filterUpdate(newList: ArrayList<RxlExercises.Program>) {
        if (list == null || list?.isEmpty()!!) {
            list = newList
            notifyDataSetChanged()
            return
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return newList[newItem].id == list!![oldItem].id
            }

            override fun getOldListSize(): Int {
                return list!!.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return true
            }

        })
        list = newList
        result.dispatchUpdatesTo(this)

    }

}