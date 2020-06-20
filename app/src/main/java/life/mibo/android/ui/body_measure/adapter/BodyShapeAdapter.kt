/*
 *  Created by Sumeet Kumar on 4/16/20 1:41 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 12:51 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import java.io.Serializable

class BodyShapeAdapter(var list: List<Item>, var listener: ItemClickListener) :
    RecyclerView.Adapter<BodyShapeAdapter.Holder>() {


    public interface ItemClickListener {
        fun onItemClicked(item: Item?, holder: Holder, position: Int)
    }

    //var list: ArrayList<Item>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var item = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_measure_shape, parent, false)
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
                    i.unit = it.unit
                    break;
                }
            }
            notifyDataSetChanged()
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.tv_title)
        var value: TextView? = itemView.findViewById(R.id.tv_value)
        var unit: TextView? = itemView.findViewById(R.id.tv_value_unit)
        var image: ImageView? = itemView.findViewById(R.id.iv_body_image)

        // var hexa: HexagonImageView? = itemView.findViewById(R.id.test_image_hexa)
        fun bind(item: Item?, listener: ItemClickListener?) {
            if (item != null) {
                text?.text = item.title
                if (!item.value.isNullOrEmpty()) {
                    value?.text = item.value
                    unit?.text = item.unit
                } else {
                    value?.text = ""
                    unit?.text = ""
                }
                image?.setImageResource(item.imageRes)
                ViewCompat.setTransitionName(image!!, item.transitionImage())
                ViewCompat.setTransitionName(text!!, item.transitionTitle())
                ViewCompat.setTransitionName(value!!, item.transitionValue())
                ViewCompat.setTransitionName(unit!!, item.transitionUnit())
                image?.setOnClickListener {
                    //item?.selected = item?.selected?.not()
                    listener?.onItemClicked(item, this, adapterPosition)
                }


            }
        }
    }

    data class Item(
        var id: Int = 0,
        var imageRes: Int = 0,
        var title: String,
        var value: String,
        var unit: String,
        var minValue: Int = 0,
        var maxValue: Int = 100,
        var defValue: Int = 0,
        var selected: Boolean = false
    ) : Serializable {

        fun transitionImage(): String {
            return "" + id + "_image"
        }

        fun transitionValue(): String {
            return "" + id + "_value"
        }

        fun transitionUnit(): String {
            return "" + id + "_unit"
        }

        fun transitionTitle(): String {
            return "" + id + "_title"
        }
    }
}