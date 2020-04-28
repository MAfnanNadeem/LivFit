/*
 *  Created by Sumeet Kumar on 4/22/20 3:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 10:23 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R

class BodyAdapter(var list: List<Item>, val type: Int = 0) :
    RecyclerView.Adapter<BodyAdapter.Holder>() {

    //var list: ArrayList<Item>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val id = if (type == 0) R.layout.list_item_test else R.layout.list_item_test_vertical
        val w = parent.measuredWidth / 3
        var item = LayoutInflater.from(parent.context).inflate(id, parent, false)
        item.minimumWidth = w
        return Holder(item)

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
        val item = getItem(position)
        holder.text?.text = item?.title
        //holder.image?.load(R.drawable.login_bg)

//        Coil.loader().load(holder.image?.context!!, R.drawable.login_bg) {
//            this.target(holder.image!!).
//        }
        //holder.view?.setBackgroundColor(getRandomColor())
        //holder.image
        //if (item?.image != 0)
        // holder.hexa?.setImageDrawable(ColorDrawable(getRandomColor()))
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.test_text)
        var view: View? = itemView.findViewById(R.id.itemView)
        // var hexa: HexagonImageView? = itemView.findViewById(R.id.test_image_hexa)
    }

    data class Item(val image: Int = 0, val title: String)
}