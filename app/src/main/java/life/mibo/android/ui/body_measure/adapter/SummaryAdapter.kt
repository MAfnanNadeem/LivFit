/*
 *  Created by Sumeet Kumar on 4/22/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/16/20 5:41 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.hardware.core.Logger

class SummaryAdapter(var list: List<Item>, var listener: ItemClickListener<Item>) :
    RecyclerView.Adapter<SummaryAdapter.Holder>() {

    //var list: ArrayList<Item>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var item = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_measure_summary, parent, false)
        return Holder(
            item
        )

    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Item? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    fun select(item: Item?) {
        if (item != null) {
            for (i in list) {
                i.selected = i.id == item.id
            }
            notifyDataSetChanged()
        }

    }

    fun update(item: Item?) {
        item?.let {
            for (i in list) {
                if (i.id == it.id) {
                    i.value = it.value
                    break;
                }
            }
            notifyDataSetChanged()
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.tv_value_text)
        var value: TextView? = itemView.findViewById(R.id.tv_value)

        // var unit: TextView? = itemView.findViewById(R.id.tv_value_unit)
        var bgImage: ImageView? = itemView.findViewById(R.id.image_bg)
        var icon: ImageView? = itemView.findViewById(R.id.image_circle)

        // var hexa: HexagonImageView? = itemView.findViewById(R.id.test_image_hexa)
        fun bind(item: Item?, listener: ItemClickListener<Item>?) {
            //Logger.e("SummaryAdapter $item")
            if (item != null) {
                var t = item.title?.split(" ")
                if (t.size > 2) {
                    var words = ""
                    t.forEachIndexed { index, s ->
                        if (index == 2)
                            words += "\n"
                        words += "$s "
                    }
                    text?.text = words

                } else {
                    text?.text = item.title
                }
                value?.text =
                    itemView?.context?.getString(R.string.summary_format, item.value, item.unit)
                // value?.text = item.unit
//                when (item.value) {
//                    is Double -> value?.text = String.format("%.2f " + item.unit, item.value)
//                    is Float -> value?.text = String.format("%.2f " + item.unit, item.value)
//                    is Int -> value?.text = String.format("%df " + item.unit, item.value)
//                    else -> value?.text = "" + item.value
//                }
                icon?.setImageResource(item.iconRes)
                try {
                    val d =
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.bg_body_measure_summary
                        )!!.constantState!!.newDrawable().mutate()
                    d.setColorFilter(item.imageColor, PorterDuff.Mode.MULTIPLY)
                    bgImage!!.setImageDrawable(d)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                bgImage?.setOnClickListener {
                    //item?.selected = item?.selected?.not()
                    listener?.onItemClicked(item, adapterPosition)
                }

            }
        }
    }

    data class Item(
        var id: Int = 0,
        var imageRes: Int = 0,
        var iconRes: Int = 0,
        var imageColor: Int = 0,
        var title: String,
        var value: Double,
        var unit: String = "",
        var normal: String = "",
        var selected: Boolean = false
    )
}