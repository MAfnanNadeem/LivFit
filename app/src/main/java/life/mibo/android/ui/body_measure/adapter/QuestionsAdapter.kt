/*
 *  Created by Sumeet Kumar on 4/22/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 12:51 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener

class QuestionsAdapter(
    var type: Int,
    var list: List<Item>,
    var listener: ItemClickListener<Item>
) :
    RecyclerView.Adapter<QuestionsAdapter.Holder>() {

    //var list: ArrayList<Item>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var item = LayoutInflater.from(parent.context)
            .inflate(if(type == 2) R.layout.list_item_goal_questions_activity else R.layout.list_item_goal_questions, parent, false)
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

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.tv_title)
        var desc: TextView? = itemView.findViewById(R.id.tv_desc)
        var imageView: ImageView? = itemView.findViewById(R.id.imageView)

        // var hexa: HexagonImageView? = itemView.findViewById(R.id.test_image_hexa)
        fun bind(item: Item?, listener: ItemClickListener<Item>?) {
            if (item != null) {
                text?.text = item.title
                desc?.text = item.desc
                if (item.selected) {
                    text?.setTextColor(Color.WHITE)
                    //desc?.setTextColor(Color.WHITE)
                    imageView?.setImageResource(R.drawable.bg_goal_gues_selected)
                } else {
                    text?.setTextColor(
                        ContextCompat.getColor(
                            text?.context!!,
                            R.color.colorPrimary
                        )
                    )
                    //desc?.setTextColor(ContextCompat.getColor(text?.context!!, R.color.textColor))
                    imageView?.setImageResource(R.drawable.bg_goal_gues_unselected)
                }

                imageView?.setOnClickListener {
                    //item?.selected = item?.selected?.not()
                    listener?.onItemClicked(item, adapterPosition)
                }
            }
        }
    }

    data class Item(
        var id: Int = 0,
        var title: String,
        var desc: String,
        var selected: Boolean = false
    )
}