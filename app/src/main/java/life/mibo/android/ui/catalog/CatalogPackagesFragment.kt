/*
 *  Created by Sumeet Kumar on 6/1/20 7:01 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/1/20 7:01 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_catalog_products.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.product.GetMemberServices
import life.mibo.android.models.product.Packages
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CatalogPackagesFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog_products, container, false)
    }

    var isRefreshing = false
    var isGrid = false
    var productAdapters: ProductAdapters? = null
    var products = ArrayList<Packages.Data>()
    var backupList = ArrayList<Packages.Data>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupAdapter()

        swipeToRefresh?.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark,
            R.color.infoColor2,
            R.color.successColor
        )
        swipeToRefresh?.setOnRefreshListener {
            log("swipeToRefresh?.setOnRefreshListener $isRefreshing")
            isRefreshing = true
            getProducts()
        }
        recyclerView?.layoutManager = GridLayoutManager(context, 1)

        getProducts()
        //  getProfessionals()
//        imageViewFilter?.setOnClickListener {
//            updateGrid()
//        }
        setHasOptionsMenu(true)
    }


    fun showProgress() {
        activity?.runOnUiThread {
            // progressBar?.visibility = View.VISIBLE
            isRefreshing = true
            swipeToRefresh?.isRefreshing = isRefreshing

        }
    }

    fun hideProgress() {
        activity?.runOnUiThread {
            //progressBar?.visibility = View.GONE
            isRefreshing = false
            swipeToRefresh?.isRefreshing = isRefreshing

        }
    }


    fun updateGrid() {
//        isGrid = !isGrid
//
//        if (isGrid) {
//            recyclerView?.layoutManager = GridLayoutManager(context, 2)
//        } else {
//            recyclerView?.layoutManager = LinearLayoutManager(context)
//        }
//        productAdapters?.grid = isGrid
//        val list = ArrayList<Product>()
//        for (p in 0..20) {
//            if (p != null)
//                list.add(
//                    Product(
//                        p,
//                        "life.mibo.android.models.product.Product/Service Name",
//                        "AED 200.80"
//                    )
//                )
//        }
//
//        list.add(
//            CartItem(
//                30,
//                "life.mibo.android.models.product.Product/Service Name",
//                "AED 399.99", "", 2
//            )
//        )
//        activity?.runOnUiThread {
//            productAdapters?.update(list)
//        }
//        // recyclerView?.adapter?.notifyDataSetChanged()
    }


    private fun getProducts() {
       // Toasty.info(requireContext(), "Loading Packages... ").show()
        log("getProducts packages ")
        val member = Prefs.get(context).member ?: return
        showProgress()
        API.request.getApi().getMemberPackages(
            GetMemberServices(
                GetMemberServices.Data(member.id),
                member.accessToken, "GetMemberPackages"
            )
        ).enqueue(object : Callback<Packages> {
            override fun onFailure(call: Call<Packages>, t: Throwable) {
                hideProgress()
                //Toasty.info(requireContext(), "Loading Packages Failed").show()
                t.printStackTrace()
            }

            override fun onResponse(call: Call<Packages>, response: Response<Packages>) {
                hideProgress()
                //Toasty.info(requireContext(), "Loading Packages done ").show()
                parseProducts(response?.body()?.data)

            }

        })

    }

    private fun parseProducts(data: List<Packages.Data?>?) {

        log("parseProducts data ${data?.size}")
        if (data != null && data.isNotEmpty()) {
            tv_empty?.visibility = View.GONE
            products.clear()
            backupList.clear()
            for (i in data) {
                if (i != null) {
                    products.add(i)
                    backupList.add(i)
                }
            }
            log("parseProducts products ${products.size}")
            productAdapters =
                ProductAdapters(0, products, object : ItemClickListener<Packages.Data> {

                    override fun onItemClicked(item: Packages.Data?, position: Int) {
                        if (item != null)
                            ProductDetailsActivity.launch(requireContext(), item)
                    }

                })

            recyclerView?.adapter = productAdapters
            productAdapters?.notifyDataSetChanged()

            log("parseProducts recycler update")
        } else {
            tv_empty?.setText(R.string.no_package_found)
            tv_empty?.visibility = View.VISIBLE
        }
    }

    class ProductAdapters(
        val type: Int = 1,
        val list: ArrayList<Packages.Data>,
        val listener: ItemClickListener<Packages.Data>?
    ) : RecyclerView.Adapter<Holder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val v = if (!grid) LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.list_item_trainer_search,
                    parent,
                    false
                ) else LayoutInflater.from(
                parent.context
            ).inflate(R.layout.list_item_trainer_search_grid, parent, false)
            return Holder(v)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(list[position], listener)

        }

        fun update(ipList: ArrayList<Packages.Data>) {
            list.clear()
            ipList.shuffle()
            list.addAll(ipList)
            this.notifyDataSetChanged()

        }

        fun filter(backup: ArrayList<Packages.Data>, text: String?) {
            list.clear()
            if (text.isNullOrEmpty()) {
                for (i in backup)
                    list.add(i)
            } else {
                val q = text.toLowerCase()
                for (item in backup) {
                    if (item.match(q)) {
                        list.add(item)
                    }
                }
            }
            notifyDataSetChanged()
        }

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_title)
        val desc: TextView? = itemView.findViewById(R.id.tv_info)
        val price: TextView? = itemView.findViewById(R.id.tv_price)
        val img: ImageView? = itemView.findViewById(R.id.imageView)

        fun bind(item: Packages.Data?, listener: ItemClickListener<Packages.Data>?) {
            if (item == null)
                return
            name?.text = item.name
            desc?.text = item.description
            price?.text = item.currencyType + " " + item.price
            img?.visibility = View.GONE
            //Glide.with(img!!).load(item.image).fitCenter().into(img)
            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_search, menu)
        try {
            val item = menu.findItem(R.id.action_search)
            val searchView: SearchView? =
                SearchView((activity as AppCompatActivity?)?.supportActionBar?.themedContext)
            // MenuItemCompat.setShowAsAction(item, //MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | //MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
            //  MenuItemCompat.setActionView(item, searchView);
            // These lines are deprecated in API 26 use instead
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            item.actionView = searchView
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    productAdapters?.filter(backupList, newText)
                    return true
                }
            })
            searchView?.setOnClickListener {
                log("search clicked")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            //updateGrid()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}