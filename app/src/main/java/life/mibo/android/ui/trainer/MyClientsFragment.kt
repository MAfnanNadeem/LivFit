/*
 *  Created by Sumeet Kumar on 7/8/20 10:44 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/7/20 5:48 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recycler.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.TrainerID
import life.mibo.android.models.trainer.TrainerClients
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.utils.Utils
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyClientsFragment : BaseFragment() {

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
    var adapters: ClientsAdapters? = null

    private var type_ = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSwipeRefreshColors(swipeToRefresh)
        swipeToRefresh?.setOnRefreshListener {
            log("swipeToRefresh?.setOnRefreshListener $isRefreshing")
            isRefreshing = true
            getClients()
        }
        recyclerView?.layoutManager = GridLayoutManager(context, 1)

        type_ = arguments?.getInt("type_", 0) ?: 0

        getClients()
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


    private fun getClients() {
        val member = Prefs.get(context).member ?: return
        showProgress()
        API.request.getApi().getCustomers(
            TrainerID(member.id, member.accessToken!!, "GetIndependentProfessionalCustomers")
        ).enqueue(object : Callback<TrainerClients> {
            override fun onFailure(call: Call<TrainerClients>, t: Throwable) {
                hideProgress()
            }

            override fun onResponse(
                call: Call<TrainerClients>,
                response: Response<TrainerClients>
            ) {
                hideProgress()
                parseData(response?.body()?.data)
            }

        })

    }

    private fun parseData(list: List<TrainerClients.Client?>?) {

        if (list == null || list.isEmpty()) {
            tv_empty?.setText(R.string.no_data_found)
            tv_empty?.visibility = View.VISIBLE
            return
        }

        val clients = ArrayList<TrainerClients.Client>()
        for (c in list) {
            if (c != null)
                clients.add(c)
        }

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val addressAdapters =
            ClientsAdapters(0, clients, object : ItemClickListener<TrainerClients.Client> {
                override fun onItemClicked(item: TrainerClients.Client?, position: Int) {

                }
            })

        recyclerView?.adapter = addressAdapters
    }


    class ClientsAdapters(
        val type: Int = 1,
        val list: ArrayList<TrainerClients.Client>,
        val listener: ItemClickListener<TrainerClients.Client>?
    ) : RecyclerView.Adapter<Holder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.list_item_my_clients,
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
        private val image: ImageView? = itemView.findViewById(R.id.imageView)

        //private val info: TextView? = itemView.findViewById(R.id.tv_order)
        private val servicesView: LinearLayout? = itemView.findViewById(R.id.ll_services)

        fun bind(
            item: TrainerClients.Client?,
            listener: ItemClickListener<TrainerClients.Client>?
        ) {
            if (item == null)
                return
            name?.text = item.name
//            if (item.avatar != null && image != null)
//                Glide.with(image).load(item.avatar).fitCenter().error(R.drawable.ic_user_test)
//                    .fallback(R.drawable.ic_user_test).into(image)
            Utils.loadImage(image, item?.avatar, true)
            val services = item.services
            if (services != null && services.isNotEmpty()) {
                for (service in services) {
                    val v = createView(service, itemView.context)
                    if (v != null) {
                        servicesView?.addView(
                            v,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                }
            }

//            if (services != null && services.isNotEmpty()) {
//                for (service in services) {
//                    service?.endDate = "2020-07-20"
//                    val v = createView(service, itemView.context)
//                    if (v != null) {
//                        servicesView?.addView(
//                            v,
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT
//                        )
//                    }
//                }
//            }
//
//            if (services != null && services.isNotEmpty()) {
//                for (service in services) {
//                    service?.endDate = "2020-07-10"
//                    val v = createView(service, itemView.context)
//                    if (v != null) {
//                        servicesView?.addView(
//                            v,
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT
//                        )
//                    }
//                }
//            }

            //val d = LocalDate.parse(item.invoiceDate)
//            quantity?.text = "${item.quantity ?: 1}"
//            price?.text = item.currency + " " + item.price
//            vat?.text = item.currency + " " + item.vat
//            total?.text = item.currency + " " + item.totalPrice

            itemView?.setOnClickListener {
                listener?.onItemClicked(item, adapterPosition)
            }
        }

        private fun createView(service: TrainerClients.Service?, context: Context): View? {
            if (service != null) {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.list_item_my_clients_inner, null)
                val name: TextView? = view.findViewById(R.id.tv_service_name)
                val complete: TextView? = view.findViewById(R.id.tv_completed_value)
                val remaining: TextView? = view.findViewById(R.id.tv_remaining_value)
                val expire: TextView? = view.findViewById(R.id.tv_expire_value)
                val started: View? = view.findViewById(R.id.viewStarted)

                name?.text = service.serviceName
                complete?.text = "${service.completedSessions}"
                remaining?.text = "${service.totalSessions?.minus(service.completedSessions ?: 0)}"
                expire?.text = service.endDate

                try {
                    val date = LocalDate.parse(service.endDate)
                    if (date.isBefore(LocalDate.now())) {
                        started?.setBackgroundColor(Color.GRAY)
                    } else if (ChronoUnit.DAYS.between(LocalDate.now(), date) < 15) {

                        expire?.setTextColor(
                            expire?.context?.getColor(R.color.textColorApp2) ?: Color.RED
                        )
                        started?.setBackgroundColor(Color.RED)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return view
            }
            return null
        }
    }


}