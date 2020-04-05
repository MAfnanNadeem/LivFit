/*
 *  Created by Sumeet Kumar on 1/8/20 5:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 3:04 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.programs

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.utils.Constants
import life.mibo.views.hexa.HexagonMaskView
import java.util.*

class ProgramAdapter(
    var list: ArrayList<Program>,
    var listener: ItemClickListener<Program>? = null
) : RecyclerView.Adapter<ProgramAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_add_product, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getItem(position: Int): Program? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
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

    fun update(newList: ArrayList<Program>) {
        list.clear()
        list.addAll(newList)
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView? = itemView.findViewById(R.id.tv_product_name)
        var view: View? = itemView.findViewById(R.id.itemView)
        var lockView: View? = itemView.findViewById(R.id.lock_view)
        var hexa: HexagonMaskView? = itemView.findViewById(R.id.hexa_image)
        var image: ImageView? = itemView.findViewById(R.id.product_image)
        var data: Program? = null

        fun bind(item: Program?, listener: ItemClickListener<Program>?) {
            if (item == null)
                return
            data = item
            title?.text = "${item.title}"
            hexa!!.setGradient(intArrayOf(Color.WHITE, Color.WHITE), Constants.PRIMARY)
            if (item.iconRes != 0)
                image?.setImageDrawable(ContextCompat.getDrawable(image!!.context, item.iconRes))
            if (item.isPurchased)
                lockView?.visibility = View.GONE
            else lockView?.visibility = View.VISIBLE
            itemView.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }
    }
}