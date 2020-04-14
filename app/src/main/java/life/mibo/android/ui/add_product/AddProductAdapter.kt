/*
 *  Created by Sumeet Kumar on 1/8/20 8:11 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 8:11 AM
 */

package life.mibo.android.ui.add_product

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
import life.mibo.android.utils.Constants
import life.mibo.views.hexa.HexagonMaskView
import java.util.*

class AddProductAdapter(
    var list: ArrayList<ProductItem>,
    var listener: ItemClickListener<ProductItem>? = null
) : RecyclerView.Adapter<AddProductAdapter.Holder>() {

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

    private fun getItem(position: Int): ProductItem? {
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

    fun update(newList: ArrayList<ProductItem>) {
        list.clear()
        list.addAll(newList)
    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView? = itemView.findViewById(R.id.tv_product_name)
        var view: View? = itemView.findViewById(R.id.itemView)
        var lockView: View? = itemView.findViewById(R.id.lock_view)
        var hexa: HexagonMaskView? = itemView.findViewById(R.id.hexa_image)
        var image: ImageView? = itemView.findViewById(R.id.product_image)
        var data: ProductItem? = null

        fun bind(item: ProductItem?, listener: ItemClickListener<ProductItem>?) {
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