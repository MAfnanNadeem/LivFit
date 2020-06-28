/*
 *  Created by Sumeet Kumar on 6/4/20 10:29 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/3/20 11:39 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_catalog_products.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.catalog.GetInvoices
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InvoicesFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catalog_products, container, false)
    }

    var isRefreshing = false
    var isGrid = false
    var productAdapters: InvoiceAdapters? = null
    var products = ArrayList<GetInvoices.Invoice>()
    //var backupList = ArrayList<Services.Data>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupAdapter()

        setSwipeRefreshColors(swipeToRefresh)
        swipeToRefresh?.setOnRefreshListener {
            log("swipeToRefresh?.setOnRefreshListener $isRefreshing")
            isRefreshing = true
            getInvoices()
        }
        recyclerView?.layoutManager = GridLayoutManager(context, 1)

        getInvoices()
        //  getProfessionals()
//        imageViewFilter?.setOnClickListener {
//            updateGrid()
//        }
        // setHasOptionsMenu(true)
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


    private fun getInvoices() {
        val member = Prefs.get(context).member ?: return
        showProgress()
        API.request.getApi().getAllInvoice(
            MemberPost(member.id(), member.accessToken!!, "GetAllInvoices")
        ).enqueue(object : Callback<GetInvoices> {
            override fun onFailure(call: Call<GetInvoices>, t: Throwable) {
                hideProgress()
            }

            override fun onResponse(call: Call<GetInvoices>, response: Response<GetInvoices>) {
                hideProgress()
                parseProducts(response?.body()?.data)

            }

        })

    }

    private fun parseProducts(invoices: GetInvoices.Data?) {

        val data = invoices?.invoice
        if (data != null && data.isNotEmpty()) {
            tv_empty?.visibility = View.GONE
            products.clear()
            //backupList.clear()
            for (i in data) {
                if (i != null) {
                    products.add(i)
                    // backupList.add(i)
                }
            }
            productAdapters =
                InvoiceAdapters(0, products, object : ItemClickListener<GetInvoices.Invoice> {

                    override fun onItemClicked(item: GetInvoices.Invoice?, position: Int) {
                        if (item != null) {
                            if (item.packageType?.toLowerCase() == "product")
                                return
                            if (item.paidStatus == null || item.paidStatus?.toLowerCase() == "pending") {
                                val i =
                                    Intent(this@InvoicesFragment.activity, BuyActivity::class.java)
                                i.putExtra("type_type", BuyActivity.TYPE_INVOICE)
                                i.putExtra("type_data", item.invoiceNumber)
                                startActivity(i)
                                return
                            }
                            InvoiceDetailsDialog(item, object : ItemClickListener<String> {
                                override fun onItemClicked(item: String?, position: Int) {

                                }

                            }).show(
                                childFragmentManager,
                                "InvoiceDetailsDialog"
                            )
                        }
                    }

                })

            recyclerView?.adapter = productAdapters

        } else {
            tv_empty?.setText(R.string.no_invoices_found)
            tv_empty?.visibility = View.VISIBLE
        }
    }

    fun payNow() {

    }

    class InvoiceAdapters(
        val type: Int = 1,
        val list: ArrayList<GetInvoices.Invoice>,
        val listener: ItemClickListener<GetInvoices.Invoice>?
    ) : RecyclerView.Adapter<Holder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.list_item_invoices,
                        parent,
                        false
                    )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(list[position], listener)

        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_info)
        val desc: TextView? = itemView.findViewById(R.id.tv_info2)
        val price: TextView? = itemView.findViewById(R.id.tv_info3)

        fun bind(item: GetInvoices.Invoice?, listener: ItemClickListener<GetInvoices.Invoice>?) {
            if (item == null)
                return
            name?.text = item.name ?: item.invoiceNumber
            desc?.text = item.invoiceDate
            price?.text = item.currency + " " + item.price

            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }

}