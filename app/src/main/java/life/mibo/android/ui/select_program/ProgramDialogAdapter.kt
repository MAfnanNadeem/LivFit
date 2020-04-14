/*
 *  Created by Sumeet Kumar on 1/16/20 12:16 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 12/26/19 11:02 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.select_program

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.models.program.Program
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.views.CircleView


class ProgramDialogAdapter(var list: ArrayList<Program?>, val type: Int) :
    RecyclerView.Adapter<ProgramDialogAdapter.Holder>() {

    //var list: ArrayList<Item>? = null
    private var listener: ItemClickListener<Program>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val id =
            if (type == 1) R.layout.list_item_spinner_programs else R.layout.list_item_color_programs
        return Holder(
            LayoutInflater.from(parent.context).inflate(id, parent, false), type
        )
    }

    fun setListener(listener: ItemClickListener<Program>) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Program? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class Holder(itemView: View, val type: Int) : RecyclerView.ViewHolder(itemView) {
        // var text: TextView? = itemView.findViewById(R.id.test_text)
        var name: TextView? = itemView.findViewById(R.id.tv_name_value)
        var duration: TextView? = itemView.findViewById(R.id.tv_duration_value)
        var desc: TextView? = itemView.findViewById(R.id.tv_desc_value)
        var circle: CircleView? = itemView.findViewById(R.id.circleImage)

        fun bind(item: Program?, listener: ItemClickListener<Program>?) {
            if (item == null)
                return
            if (type == 1) {
                name?.text = "${item?.name}"
                duration?.text = "${item?.duration?.value?.toInt()?.div(60)} Minutes"
                desc?.text = "${item?.description}"
            } else {
                val shape = GradientDrawable()
               // circle?.background = circle(item?.id!!)
                circle?.circleColor = item.id!!
            }

            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

        fun circle(color: Int): GradientDrawable {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            shape.setColor(color)
            return shape
        }
    }
}