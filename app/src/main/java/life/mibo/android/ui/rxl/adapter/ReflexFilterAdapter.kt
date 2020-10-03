/*
 *  Created by Sumeet Kumar on 2/11/20 2:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 10:05 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.hardware.core.Logger
import java.util.*


class ReflexFilterAdapter(var list: ArrayList<ReflexFilterModel>?, val type: Int = 0) :
    RecyclerView.Adapter<ReflexFilterAdapter.Holder>() {

    private var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_reflex_filters,
                parent,
                false
            )
        )
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.type
        //return super.getItemViewType(position)
    }

    private fun getItem(position: Int): ReflexFilterModel? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //holder.bind(getItem(position), listener, type)
        holder.bind2(getItem(position), listener)
    }


    // public classes
    interface Listener {
        fun onClick(data: ReflexFilterModel?)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.text_filter)
        var view: View? = itemView.findViewById(R.id.item_view)
        var heart: View? = itemView.findViewById(R.id.image_filter)
        var arrow: View? = itemView.findViewById(R.id.image_arrow)
        var switch_: View? = itemView.findViewById(R.id.switch_filter)
        var data: ReflexModel? = null

        fun bind2(item: ReflexFilterModel?, listener: Listener?) {
            if (item == null)
                return

            heart?.visibility = View.GONE
            switch_?.visibility = View.GONE
            view?.visibility = View.VISIBLE
            arrow?.visibility = View.GONE

            if (item.isSelected) {
                view?.setBackgroundResource(R.drawable.item_rxl_filters_selected4)
                text?.setTextColor(Color.WHITE)
            } else {
                view?.setBackgroundResource(R.drawable.item_rxl_filters)
                text?.setTextColor(Color.DKGRAY)
            }
            text?.text = item.title
            view?.setOnClickListener {
                item.isSelected = !item.isSelected
                listener?.onClick(item)
                if (item.isSelected) {
                    view?.setBackgroundResource(R.drawable.item_rxl_filters_selected4)
                    text?.setTextColor(Color.WHITE)
                } else {
                    view?.setBackgroundResource(R.drawable.item_rxl_filters)
                    text?.setTextColor(Color.DKGRAY)
                }
            }
        }


        fun bind(item: ReflexFilterModel?, listener: Listener?, type: Int = 0) {
            if (item == null)
                return
            Logger.e("ReflexFilterAdapter $type  item = $item")
            when (item.type) {
                2 -> {
                    heart?.visibility = View.VISIBLE
                    switch_?.visibility = View.GONE
                    view?.visibility = View.GONE
                }

                3 -> {
                    heart?.visibility = View.GONE
                    switch_?.visibility = View.VISIBLE
                    view?.visibility = View.GONE
                    arrow?.visibility = View.GONE

                    if (item.isSelected) {
                        view?.setBackgroundResource(R.drawable.item_rxl_filters_selected4)
                        text?.setTextColor(Color.WHITE)
                    } else {
                        view?.setBackgroundResource(R.drawable.item_rxl_filters)
                        text?.setTextColor(Color.DKGRAY)
                    }

                }
                else -> {
                    if (type == 5) {
                        heart?.visibility = View.GONE
                        switch_?.visibility = View.GONE
                        view?.visibility = View.VISIBLE
                        text?.visibility = View.VISIBLE
                        arrow?.visibility = View.GONE
                        if (item.isSelected) {
                            view?.setBackgroundResource(R.drawable.item_rxl_filters_selected4)
                            text?.setTextColor(Color.WHITE)
                        } else {
                            view?.setBackgroundResource(R.drawable.item_rxl_filters)
                            text?.setTextColor(Color.BLACK)
                        }
                        view?.setOnClickListener {
                            item.isSelected = !item.isSelected
                            listener?.onClick(item)
                            updateView(item, 5)

                        }
                        text?.text = item.title
                        return
                    }
                    heart?.visibility = View.GONE
                    switch_?.visibility = View.GONE
                    view?.visibility = View.VISIBLE

                    if (type == 3) {
                        arrow?.visibility = View.GONE
                        if (item.isSelected) {
                            if (type == 5) {
                                view?.setBackgroundResource(R.drawable.item_rxl_filters_selected4)
                                text?.setTextColor(Color.WHITE)
                            } else {
                                view?.setBackgroundResource(R.drawable.item_rxl_filters_selected2)
                                text?.setTextColor(Color.DKGRAY)
                            }
                        } else {
                            if (type == 5) {
                                view?.setBackgroundResource(R.drawable.item_rxl_filters)
                                text?.setTextColor(Color.BLACK)
                            } else {
                                view?.setBackgroundResource(R.drawable.item_rxl_filters)
                                text?.setTextColor(Color.WHITE)
                            }

                        }
                    } else {
                        if (item.isSelected)
                            view?.setBackgroundResource(R.drawable.item_rxl_filters_selected)
                        else
                            view?.setBackgroundResource(R.drawable.item_rxl_filters)
                    }
                    text?.text = item.title
                    view?.setOnClickListener {
                        item.isSelected = !item.isSelected
                        listener?.onClick(item)
                        updateView(item, type)

                    }

                }
            }
        }

        private fun updateView(item: ReflexFilterModel, type: Int) {
            if (type == 3) {
                arrow?.visibility = View.GONE
                if (item.isSelected) {
                    view?.setBackgroundResource(R.drawable.item_rxl_filters_selected4)
                    text?.setTextColor(Color.WHITE)
                } else {
                    view?.setBackgroundResource(R.drawable.item_rxl_filters)
                    text?.setTextColor(Color.DKGRAY)
                }
            } else if (type == 5) {
                if (item.isSelected) {
                    view?.setBackgroundResource(R.drawable.item_rxl_filters_selected4)
                    text?.setTextColor(Color.WHITE)
                } else {
                    view?.setBackgroundResource(R.drawable.item_rxl_filters)
                    text?.setTextColor(Color.BLACK)
                }
            } else {
                if (item.isSelected)
                    view?.setBackgroundResource(R.drawable.item_rxl_filters_selected)
                else
                    view?.setBackgroundResource(R.drawable.item_rxl_filters)
            }
        }


    }

    data class ReflexFilterModel(
        val id: Int,
        val title: String,
        val image: Int = 0,
        val type: Int = 0
    ) {
        var isSelected = false
        var filterType = 0

        constructor(id: Int, text: String, type: Int) : this(id, text, 0, 0) {
            filterType = type
        }

        fun setSelected(text: String) {
            if (title?.toLowerCase() == text?.toLowerCase())
                isSelected = true
        }

        fun isCat() = filterType == 1
        fun isPod() = filterType == 2
        fun isAcc() = filterType == 3

        override fun equals(other: Any?): Boolean {
            if (other != null && other is ReflexFilterModel) {
                return other.id == id
            }
            return false
        }

        override fun hashCode(): Int {
            return id
        }

        override fun toString(): String {
            return "FilterModel($id, $title)"
        }
    }
}