/*
 *  Created by Sumeet Kumar on 6/4/20 4:06 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/4/20 2:03 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.catalog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_catalog_products.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.MemberPost
import life.mibo.android.models.catalog.GetInvoices
import life.mibo.android.models.catalog.ShipmentAddress
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdersFragment : BaseFragment() {

    companion object {
        fun create(type: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt("type_", type)
            return bundle
        }
    }

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

    private var type_ = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupAdapter()

//        val kk = Utils.getRefreshColors()
//        val colorRes = IntArray(kk.size)
//        for (i in kk) {
//            colorRes[i] = ContextCompat.getColor(requireContext(), i)
//        }
//        swipeToRefresh?.setColorSchemeColors(kk)
//        swipeToRefresh?.setColorSchemeResources(
//            R.color.colorPrimary, R.color.colorAccent,
//            R.color.infoColor2, R.color.textColorApp2, R.color.textColorApp
//        )
        setSwipeRefreshColors(swipeToRefresh)
        swipeToRefresh?.setOnRefreshListener {
            log("swipeToRefresh?.setOnRefreshListener $isRefreshing")
            isRefreshing = true
            getInvoices()
        }
        recyclerView?.layoutManager = GridLayoutManager(context, 1)

        type_ = arguments?.getInt("type_", 0) ?: 0

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
        if (type_ == 2) {
            getAddress()
            return
        }
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

    private fun getAddress() {
        val member = Prefs.get(context).member ?: return
        showProgress()
        API.request.getApi().getMemberShippingAddress(
            MemberPost(
                member.id(),
                member.accessToken!!,
                "GetMemberShippingAddress"
            )
        ).enqueue(object : Callback<ShipmentAddress> {
            override fun onFailure(call: Call<ShipmentAddress>, t: Throwable) {
                hideProgress()
            }

            override fun onResponse(
                call: Call<ShipmentAddress>,
                response: Response<ShipmentAddress>
            ) {
                hideProgress()
                val list = response?.body()?.data
                val address = ArrayList<BuyActivity.AddressItem>()
                if (list != null && list.isNotEmpty()) {
                    for (i in list) {
                        if (i != null)
                            address.add(
                                BuyActivity.AddressItem(
                                    i.id!!,
                                    i.name,
                                    i.address,
                                    i.city,
                                    i.country,
                                    i.phone
                                )
                            )
                    }

                }
                activity?.runOnUiThread {
                    setupAddresses(address)
                }
            }

        })

    }

    private fun setupAddresses(address: ArrayList<BuyActivity.AddressItem>?) {
        if (address == null || address.isEmpty()) {
            tv_empty?.setText(R.string.no_data_found)
            tv_empty?.visibility = View.VISIBLE
            return
        }

        for (add in address) {
            add.isViewMode = true
        }

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val addressAdapters =
            BuyActivity.AddressAdapters(
                address,
                object : ItemClickListener<BuyActivity.AddressItem> {
                    override fun onItemClicked(item: BuyActivity.AddressItem?, position: Int) {
                        showAddressDialog()
                    }

                })

        recyclerView?.adapter = addressAdapters

    }

    private fun showAddressDialog() {
        val options = arrayOf(
            getString(R.string.update),
            getString(R.string.delete)
        )
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    //update
                }
                1 -> {
                    //delete
                }
                2 -> {

                }
            }
        }
        builder.show()
    }


    private fun parseProducts(invoices: GetInvoices.Data?) {
        //isRefreshing = false
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
                                    Intent(this@OrdersFragment.activity, BuyActivity::class.java)
                                i.putExtra("type_type", BuyActivity.TYPE_INVOICE)
                                i.putExtra("type_data", item.invoiceNumber)
                                i.putExtra("type_location", item.locationID)
                                startActivity(i)
                                return
                            }
//                            InvoiceDetailsDialog(item, object : ItemClickListener<String> {
//                                override fun onItemClicked(item: String?, position: Int) {
//
//                                }
//
//                            }).show(
//                                childFragmentManager,
//                                "InvoiceDetailsDialog"
//                            )
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
        private val name: TextView? = itemView.findViewById(R.id.tv_info)
        private val invoice: TextView? = itemView.findViewById(R.id.tv_info1)
        private val desc: TextView? = itemView.findViewById(R.id.tv_info2)
        private val price: TextView? = itemView.findViewById(R.id.tv_info3)
        private val paid: TextView? = itemView.findViewById(R.id.tv_paid)

        fun bind(item: GetInvoices.Invoice?, listener: ItemClickListener<GetInvoices.Invoice>?) {
            if (item == null)
                return
            if (item.name != null && item.name!!.isNotEmpty()) {
                name?.text = item.name
                invoice?.text = item.invoiceNumber
                invoice?.visibility = View.VISIBLE
            } else {
                name?.text = item.invoiceNumber
                invoice?.visibility = View.GONE
            }
            if (item.paidStatus?.toLowerCase() == "pending") {
                paid?.visibility = View.VISIBLE
                paid?.setText(R.string.unpaid)
            } else {
                paid?.visibility = View.GONE
            }

            desc?.text = item.invoiceDate
            price?.text = item.currency + " " + item.price

            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

    }
}