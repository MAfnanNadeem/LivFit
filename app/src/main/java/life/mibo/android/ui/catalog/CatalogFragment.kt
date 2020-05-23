/*
 *  Created by Sumeet Kumar on 5/18/20 4:04 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/17/20 6:44 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_search_catalog.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener


class CatalogFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupAdapter()
        recyclerView?.layoutManager = GridLayoutManager(context, 1)
        getProfessionals()
        imageViewFilter?.setOnClickListener {
            updateGrid()
        }
    }

    var isGrid = false
    val ipList = ArrayList<CartItem>()
    var searchAdapters: SearchAdapters? = null
    private fun getProfessionals() {
        ipList.clear()
        for (p in 0..20) {
            if (p != null)
                ipList.add(
                    CartItem(
                        p,
                        "Product/Service Name",
                        "AED 200.80"
                    )
                )
        }

        ipList.add(
            CartItem(
                30,
                "Deliverable Product/Service",
                "AED 399.99", "", 2
            )
        )

        ipList.add(
            CartItem(
                30,
                "Product/Service Name",
                "AED 399.99", "", 2
            )
        )
        searchAdapters =
            SearchAdapters(
                1,
                ipList,
                object :
                    ItemClickListener<CartItem> {
                    override fun onItemClicked(
                        item: CartItem?,
                        position: Int
                    ) {

                        CatalogDetailsDialog(item).show(
                            childFragmentManager,
                            "CatalogDetailsDialog"
                        )
                    }

                })
        recyclerView?.adapter = searchAdapters;
        searchAdapters?.notifyDataSetChanged()
    }


    fun updateGrid() {
        if (isGrid) {
            recyclerView?.layoutManager = GridLayoutManager(context, 2)
        } else {
            recyclerView?.layoutManager = GridLayoutManager(context, 1)
        }
        searchAdapters?.grid = isGrid
        val list = ArrayList<CartItem>()
        for (p in 0..20) {
            if (p != null)
                list.add(
                    CartItem(
                        p,
                        "Product/Service Name",
                        "AED 200.80"
                    )
                )
        }

        list.add(
            CartItem(
                30,
                "Product/Service Name",
                "AED 399.99", "", 2
            )
        )
        activity?.runOnUiThread {
            searchAdapters?.update(list)
        }
        // recyclerView?.adapter?.notifyDataSetChanged()
        isGrid = !isGrid
    }

    private fun setupAdapter(type: Int = 1) {

//        recyclerView?.layoutManager = GridLayoutManager(context, 1)
//        val list = ArrayList<Professional>()
//        for (i in 0..20) {
//            list.add(Professional(i))
//        }
//
//
//        val adapters = SearchAdapters(1, list, object : ItemClickListener<SearchItem> {
//            override fun onItemClicked(item: SearchItem?, position: Int) {
//                RememberMeDialog(300).show(childFragmentManager, "RememberMeDialog")
//            }
//
//        })
//        recyclerView?.adapter = adapters;
    }

    class SearchAdapters(
        val type: Int = 1,
        val list: ArrayList<CartItem>,
        val listener: ItemClickListener<CartItem>?
    ) : RecyclerView.Adapter<SearchHolder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
            val v = if (!grid) LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.list_item_trainer_search,
                    parent,
                    false
                ) else LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_item_trainer_search_grid, parent, false)
            return SearchHolder(v)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: SearchHolder, position: Int) {
            holder.bind(list[position], listener)

        }

        fun update(ipList: ArrayList<CartItem>) {
            list.clear()
            ipList.shuffle()
            list.addAll(ipList)
            this.notifyDataSetChanged()

        }

    }

    data class CartItem(
        val id: Int,
        var name: String,
        var price: String,
        var desc: String = "",
        var type: Int = 0
    )

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val desc: TextView? = itemView.findViewById(R.id.tv_info)
        val img: ImageView? = itemView.findViewById(R.id.imageView)

        fun bind(item: CartItem, listener: ItemClickListener<CartItem>?) {
            name?.text = item.name
            desc?.text = item.price
            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }
}