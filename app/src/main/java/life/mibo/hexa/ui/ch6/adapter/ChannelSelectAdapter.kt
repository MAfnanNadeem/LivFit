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
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.models.muscle.Muscle
import life.mibo.hexa.ui.base.ItemClickListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


class ChannelSelectAdapter(
    var list: ArrayList<Muscle>?,
    val type: Int = 0, var listener: ItemClickListener<Muscle>?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //var list: ArrayList<Item>? = null
    // private var listener: ItemClickListener<Item>? = null

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


    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Muscle? {
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

    fun update(item: Muscle) {
        if (list == null)
            return
        var add = true
        var pos = 0;
        val iterator = list!!.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if (it.position == item.position) {
                add = false;
                iterator.remove()
                notifyItemRemoved(pos)
                return
            }
            pos++

        }

        if (add) {
            list!!.add(item)
            notifyItemInserted(list!!.size)
        }
    }

    fun update(newList: Collection<Muscle>?) {
        if (newList == null)
            return
        if (list == null)
            list = ArrayList();
        list!!.clear()
        list!!.addAll(newList)
        notifyDataSetChanged()
    }

    fun append(newList: Collection<Muscle>) {
        if (list == null)
            list = ArrayList();
        list!!.addAll(newList)
        notifyDataSetChanged()
    }


    class MuscleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var text: TextView? = itemView.findViewById(R.id.test_text)
        var image: ImageView? = itemView.findViewById(R.id.iv_muscle)
        var imageBg: View? = itemView.findViewById(R.id.iv_muscle_select)
        var odd: View? = itemView.findViewById(R.id.iv_muscle1)
        var even: View? = itemView.findViewById(R.id.iv_muscle2)
        var isSelected = false;

        fun bind(position: Int, item: Muscle?, listener: ItemClickListener<Muscle>?) {
            if (item == null)
                return
            image?.load(item.image)
            if (position % 2 == 0) {
                odd?.visibility = View.GONE
                even?.visibility = View.VISIBLE
            } else {
                even?.visibility = View.GONE
                odd?.visibility = View.VISIBLE
            }

            image?.setOnClickListener {
                item.isSelected = item.isSelected.not()
                if (item.isSelected)
                    imageBg?.visibility = View.VISIBLE
                else
                    imageBg?.visibility = View.INVISIBLE

                listener?.onItemClicked(item, adapterPosition)
            }
        }
    }

    class ChanelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var text: TextView? = itemView.findViewById(R.id.test_text)
        var image: AppCompatImageView? = itemView.findViewById(R.id.iv_muscle)
        var perc: EditText? = itemView.findViewById(R.id.et_perc)
        var minusBtn: AppCompatImageButton? = itemView.findViewById(R.id.button_minus)
        var plusBtn: AppCompatImageButton? = itemView.findViewById(R.id.button_plus)
        var data: Muscle? = null;

        fun bind(item: Muscle?, listener: ItemClickListener<Muscle>?) {
            if (item == null)
                return
            data = item;

            image?.load(item.image)
            perc?.setText("${item.channelValue} %")
            perc?.keyListener = null
//            perc?.addTextChangedListener {
//                Logger.e("addTextChangedListener $it")
//            }
            //image?.setImageResource(item.imageRes)
            //image?.setBackgroundResource(item.imageRes)
            minusBtn?.setOnClickListener {
                if (item.channelValue > 0) {
                    item?.decValue()
                    perc?.setText("${item.channelValue} %")
                }
            }

            plusBtn?.setOnClickListener {
                if (item.channelValue < 50) {
                    item?.incValue()
                    perc?.setText("${item.channelValue} %")
                }
            }

           // perc?.text = "${item.value} %"

        }
    }
}