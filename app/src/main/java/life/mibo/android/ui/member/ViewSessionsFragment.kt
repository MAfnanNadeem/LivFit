/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.member

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
import life.mibo.android.models.base.PostData
import life.mibo.android.models.calories.Calories
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.hardware.core.Logger
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewSessionsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_recycler, container, false)
        return root
    }

    var isRefreshing = false
    //var adapters: SessionAdapters? = null

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

        val post = PostData("${member.id}", member.accessToken, "CaloriesBurnt")
        API.request.getApi().getAllCaloriesBurnt(post).enqueue(object : Callback<Calories> {
            override fun onFailure(call: Call<Calories>, t: Throwable) {
                hideProgress()
            }

            override fun onResponse(call: Call<Calories>, response: Response<Calories>) {
                hideProgress()
                // fragment.getDialog()?.dismiss()
                //val data = response.body()
                parseData(response?.body()?.data)
            }
        })

    }

    private fun parseData(list: List<CaloriesData?>?) {

        if (list == null || list.isEmpty()) {
            tv_empty?.setText(R.string.no_data_found)
            tv_empty?.visibility = View.VISIBLE
            return
        }

        val clients = ArrayList<CaloriesData>()
        for (c in list) {
            if (c != null)
                clients.add(c)
        }

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val addressAdapters =
            SessionAdapters(0, clients, object : ItemClickListener<CaloriesData> {
                override fun onItemClicked(item: CaloriesData?, position: Int) {

                }
            })

        recyclerView?.adapter = addressAdapters
    }


    class SessionAdapters(
        val type: Int = 1,
        val list: ArrayList<CaloriesData>,
        val listener: ItemClickListener<CaloriesData>?
    ) : RecyclerView.Adapter<Holder>() {
        var grid = false
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.list_item_session_history,
                        parent,
                        false
                    )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(list[position])

        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var time: TextView? = itemView.findViewById(R.id.tv_action)
        var minutes: TextView? = itemView.findViewById(R.id.tv_minutes)
        var calories: TextView? = itemView.findViewById(R.id.tv_calories)
        var date: TextView? = itemView.findViewById(R.id.tv_date_name)
        var service: TextView? = itemView.findViewById(R.id.tv_service_name)
        var program: TextView? = itemView.findViewById(R.id.tv_program_name)
        var trainer: TextView? = itemView.findViewById(R.id.tv_trainer_name)


        var data: CaloriesData? = null

        fun bind(item: CaloriesData?) {
            Logger.e("CaloriesAdapter bind item $item")
            if (item == null)
                return
            data = item

            //time?.text = getDiff(item.startDatetime, item.endDatetime)
            val dur = item?.duration?.toIntOrNull() ?: 0
            when {
                dur > 60 -> {
                    minutes?.text = minutes?.context?.getString(R.string.minutes)
                    time?.text = "${dur.div(60)}"
                }
                dur > 0 -> {
                    minutes?.text = minutes?.context?.getString(R.string.seconds)
                    time?.text = "$dur"
                }
                else -> {
                    minutes?.text = "----"
                    time?.text = "0"
                }
            }

            calories?.text = "${item.caloriesBurnt} cal"
            date?.text = "${item.startDatetime?.split(" ")?.get(0)}"
            service?.text = item.serviceName
            program?.text = item.programCircuitName
            trainer?.text = item.trainerName

        }

        fun getDiff(start: String?, end: String?): String? {
            //.e("CaloriesAdapter getDiff call")
            //val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var time = ""
            try {
                val s = LocalTime.parse(start?.split(" ")!![1])
                val e = LocalTime.parse(end?.split(" ")!![1])
                val d = org.threeten.bp.Duration.between(s, e)
                //Logger.e("CaloriesAdapter getDiff $d")
                time = "" + ChronoUnit.MINUTES.between(s, e)
                //format.parse(end)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return time
        }
    }
}