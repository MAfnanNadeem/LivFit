/*
 *  Created by Sumeet Kumar on 7/8/20 10:44 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/7/20 5:48 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recycler.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.TrainerID
import life.mibo.android.models.catalog.GetInvoices
import life.mibo.android.models.trainer.TrainerInvoices
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Toasty
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MySalesFragment : BaseFragment() {

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
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    var isRefreshing = false
    var adapters: InvoiceAdapters? = null

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
        showProgress()
        API.request.getApi()
            .getTrainerInvoices(
                TrainerID(
                    member.id,
                    member.accessToken,
                    "GetAllTrainerInvoices"
                )
            )
            .enqueue(object : Callback<TrainerInvoices> {
                override fun onFailure(call: Call<TrainerInvoices>, t: Throwable) {
                    // getmDialog()?.dismiss()
                    hideProgress()

                }

                override fun onResponse(
                    call: Call<TrainerInvoices>,
                    response: Response<TrainerInvoices>
                ) {
                    log("ProfessionalDetails getDetails >> onResponse ")
                    try {
                        val data = response?.body();
                        log("ProfessionalDetails getDetails >> onResponse success $data")
                        if (data != null && data.isSuccess()) {
                            val list = data.data
                            parseData(list)

                        } else {
                            log("ProfessionalDetails getDetails >> onResponse failed ${response.body()}")
                            parseData(null)
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.let {
                                    if (it?.code != 404)
                                        Toasty.snackbar(recyclerView, it?.message)
                                }
                            //tv_service_no?.visibility = View.VISIBLE
                            //tv_specialization_no?.visibility = View.VISIBLE
                            //tv_certificate_no?.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    //getmDialog()?.dismiss()
                    hideProgress()
                }

            })

    }

    private fun parseData(list: List<TrainerInvoices.Data?>?) {
        val invoices = ArrayList<TrainerInvoices.Data>()
        if (list == null || list.isEmpty()) {
            tv_empty?.setText(R.string.no_data_found)
            tv_empty?.visibility = View.VISIBLE
            return
        }

        for (i in list) {
            i?.let {
                invoices.add(it)
            }
        }

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val addressAdapters =
            InvoiceAdapters(0, invoices, object : ItemClickListener<TrainerInvoices.Data> {
                override fun onItemClicked(item: TrainerInvoices.Data?, position: Int) {

                }
            })

        recyclerView?.adapter = addressAdapters

    }


    class InvoiceAdapters(
        val type: Int = 1,
        val list: ArrayList<TrainerInvoices.Data>,
        val listener: ItemClickListener<TrainerInvoices.Data>?
    ) : RecyclerView.Adapter<Holder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.list_item_trainer_invoices,
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

        private val name: TextView? = itemView.findViewById(R.id.tv_name)
        private val user: TextView? = itemView.findViewById(R.id.tv_purchased_by)

        //private val invoice: TextView? = itemView.findViewById(R.id.tv_order)
        private val quantity: TextView? = itemView.findViewById(R.id.tv_quantity)
        //private val price: TextView? = itemView.findViewById(R.id.tv_price)
        //private val vat: TextView? = itemView.findViewById(R.id.tv_vat)

        private val total: TextView? = itemView.findViewById(R.id.tv_total)
        // private val paid: TextView? = itemView.findViewById(R.id.tv_paid)

        private val year: TextView? = itemView.findViewById(R.id.tv_year)
        private val month: TextView? = itemView.findViewById(R.id.tv_month)
        private val date: TextView? = itemView.findViewById(R.id.tv_date)


        fun bind(item: TrainerInvoices.Data?, listener: ItemClickListener<TrainerInvoices.Data>?) {
            if (item == null)
                return
            name?.text = item.name
            user?.text = item.member

            val d = LocalDate.parse(item.date)
            quantity?.text = "${item.quantity ?: 1}"
            total?.text = item.currency + " " + item.price

            month?.text = "${d.year}"
            date?.text = "${d.dayOfMonth}"
            year?.text = d.month?.getDisplayName(TextStyle.SHORT, Locale.getDefault())

            itemView.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

        fun bind(item: GetInvoices.Invoice?, listener: ItemClickListener<GetInvoices.Invoice>?) {
            if (item == null)
                return
//            invoice?.text = item.invoiceNumber
//            invoice?.visibility = View.VISIBLE
//            if (item.name != null && item.name!!.isNotEmpty()) {
//                name?.text = item.name
//            } else {
//                name?.text = item.packageType
//            }
//            if (item.paidStatus?.toLowerCase() == "pending") {
//                paid?.visibility = View.VISIBLE
//                paid?.setText(R.string.unpaid)
//                paid?.setTextColor(ContextCompat.getColor(paid?.context, R.color.textColorApp2))
//            } else {
//                paid?.visibility = View.VISIBLE
//                //val st = item?.paidStatus
//                // if (st != null && st.length > 1)
//                //   paid?.text = st
//                // else paid?.setText(R.string.paid)
//                paid?.setText(R.string.paid)
//                paid?.setTextColor(ContextCompat.getColor(paid?.context, R.color.textColor2))
//            }
//
//            // val dates = item.invoiceDate?.split("-")
//            val d = LocalDate.parse(item.invoiceDate)
//
//            month?.text = "${d.year}"
//            date?.text = "${d.dayOfMonth}"
//            year?.text = d.month?.getDisplayName(TextStyle.SHORT, Locale.getDefault())
//
//            quantity?.text = "${item.quantity ?: 1}"
//            price?.text = item.currency + " " + item.price
//            vat?.text = item.currency + " " + item.vat
//            total?.text = item.currency + " " + item.totalPrice
//
//            itemView?.setOnClickListener {
//                listener?.onItemClicked(item, adapterPosition)
//            }
        }
    }


}