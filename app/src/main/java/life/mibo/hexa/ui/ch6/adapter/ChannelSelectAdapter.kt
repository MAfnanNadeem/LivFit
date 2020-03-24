/*
 *  Created by Sumeet Kumar on 3/24/20 10:13 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/24/20 10:07 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.ch6.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.ItemClickListener
import java.util.*


class ChannelSelectAdapter(
    var list: ArrayList<Item>?,
    val type: Int = 0
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //var list: ArrayList<Item>? = null
    private var listener: ItemClickListener<Item>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (type == 2)
            return MuscleHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_select_muscle,
                    parent,
                    false
                )
            )
        return ChanelHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_select_channel,
                parent,
                false
            )
        )
    }

    fun setListener(listener: ItemClickListener<Item>) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Item? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChanelHolder)
            holder.bind(getItem(position), listener)
        if (holder is MuscleHolder)
            holder.bind(position, getItem(position), listener)
    }


    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    fun update(newList: ArrayList<Item>) {

    }

    class MuscleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var text: TextView? = itemView.findViewById(R.id.test_text)
        var image: ImageView? = itemView.findViewById(R.id.iv_muscle)
        var odd: View? = itemView.findViewById(R.id.iv_muscle1)
        var even: View? = itemView.findViewById(R.id.iv_muscle2)
        var isSelected = false;

        fun bind(position: Int, item: Item?, listener: ItemClickListener<Item>?) {
            if (item == null)
                return

            image?.setBackgroundResource(item.imageRes)
            if (position % 2 == 0) {
                odd?.visibility = View.GONE
                even?.visibility = View.VISIBLE
            } else {
                even?.visibility = View.GONE
                odd?.visibility = View.VISIBLE
            }

            image?.setOnClickListener {
                item.isSelected = item.isSelected.not()
                listener?.onItemClicked(item, adapterPosition)
            }
        }
    }

    class ChanelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var text: TextView? = itemView.findViewById(R.id.test_text)
        var image: AppCompatImageView? = itemView.findViewById(R.id.iv_muscle)
        var perc: TextView? = itemView.findViewById(R.id.et_perc)
        var minusBtn: AppCompatImageButton? = itemView.findViewById(R.id.button_minus)
        var plusBtn: AppCompatImageButton? = itemView.findViewById(R.id.button_plus)
        var data: Item? = null;

        fun bind(item: Item?, listener: ItemClickListener<Item>?) {
            if (item == null)
                return
            data = item;

            image?.setBackgroundResource(item.imageRes);
            //image?.setBackgroundResource(item.imageRes)
            minusBtn?.setOnClickListener {
                if (item.value > 0) {
                    item?.decValue()
                    perc?.text = "${item.value} %"
                }
            }

            plusBtn?.setOnClickListener {
                if (item.value < 50) {
                    item?.incValue()
                    perc?.text = "${item.value} %"
                }
            }

            perc?.text = "${item.value} %"

        }
    }

    data class Item(var position: Int, var imageRes: Int) {

        var isSelected = false
        var value: Int = 25

        fun incValue() {
            value += 1
        }

        fun decValue() {
            value -= 1
        }
    }
}