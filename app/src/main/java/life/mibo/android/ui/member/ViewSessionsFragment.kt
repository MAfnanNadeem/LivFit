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
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_recycler.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.PostData
import life.mibo.android.models.calories.Calories
import life.mibo.android.models.calories.CaloriesData
import life.mibo.android.models.rxt.GetAllIslandPost
import life.mibo.android.models.rxt.GetAllIslandsByLocation
import life.mibo.android.models.rxt.GetMemberScores
import life.mibo.android.models.rxt.GetMemberScoresReport
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.rxt.ConfigureIslandActivity
import life.mibo.android.ui.rxt.RXTUtils.playSequence
import life.mibo.android.ui.rxt.model.Tile
import life.mibo.hardware.core.Logger
import org.threeten.bp.LocalTime
import org.threeten.bp.temporal.ChronoUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewSessionsFragment : BaseFragment() {


    companion object {
        fun create(type: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt("session_type", type)
            return bundle
        }
    }

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
            getApis()
        }
        recyclerView?.layoutManager = GridLayoutManager(context, 1)

        type_ = arguments?.getInt("session_type", 0) ?: 0

        getApis()
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

    fun getApis() {
        if (type_ == 2) {
            getReactSession(false)
        } else if (type_ == 5) {
            getAllIslands()
        } else {
            getSessions()
        }
    }

    private fun getReactSession(isRxl: Boolean) {
        val member = Prefs.get(context).member ?: return
        showProgress()

        val post = GetMemberScores(GetMemberScores.Data("", "${member.id}"), member.accessToken)
        API.request.getApi().getScore(post).enqueue(object : Callback<GetMemberScoresReport> {
            override fun onFailure(call: Call<GetMemberScoresReport>, t: Throwable) {
                hideProgress()
            }

            override fun onResponse(
                call: Call<GetMemberScoresReport>,
                response: Response<GetMemberScoresReport>
            ) {
                hideProgress()
                // fragment.getDialog()?.dismiss()
                //val data = response.body()
                parseRXTScores(response?.body()?.data)
            }
        })

    }

    private fun getAllIslands() {
        val member = Prefs.get(context).member ?: return
        showProgress()

        val post =
            GetAllIslandPost(
                GetAllIslandPost.Data("${member.locationID}"),
                member.accessToken,
                "GetIslandTilesByLocation"
            )
        API.request.getApi().getIslandsByLocation(post)
            .enqueue(object : Callback<GetAllIslandsByLocation> {
                override fun onFailure(call: Call<GetAllIslandsByLocation>, t: Throwable) {
                    hideProgress()
                }

                override fun onResponse(
                    call: Call<GetAllIslandsByLocation>,
                    response: Response<GetAllIslandsByLocation>
                ) {
                    hideProgress()
                    // fragment.getDialog()?.dismiss()
                    val data = response.body()
                    parseIslands(data?.data)
                }
            })

    }


    private fun getSessions() {
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


    private fun parseRXTScores(list: List<GetMemberScoresReport.Data?>?) {

        if (list == null || list.isEmpty()) {
            tv_empty?.setText(R.string.no_data_found)
            tv_empty?.visibility = View.VISIBLE
            return
        }

        val sessions = ArrayList<GetMemberScoresReport.Data>()
        for (c in list) {
            if (c != null)
                sessions.add(c)
        }

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val addressAdapters =
            RxtScoreAdapters(sessions, object : ItemClickListener<GetMemberScoresReport.Data> {
                override fun onItemClicked(item: GetMemberScoresReport.Data?, position: Int) {

                }
            })

        recyclerView?.adapter = addressAdapters
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


    private fun parseIslands(list: List<GetAllIslandsByLocation.Island?>?) {

        if (list == null || list.isEmpty()) {
            tv_empty?.setText(R.string.no_data_found)
            tv_empty?.visibility = View.VISIBLE
            return
        }

        val clients = ArrayList<GetAllIslandsByLocation.Island>()
        for (c in list) {
            if (c != null) {
                clients.add(c)
            }
        }

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val addressAdapters =
            ViewIslandAdapters(
                clients,
                object : ItemClickListener<GetAllIslandsByLocation.Island?> {
                    override fun onItemClicked(
                        item: GetAllIslandsByLocation.Island?,
                        position: Int
                    ) {
                        try {
                            item?.tiles?.let {
                                if (position == 0) {
                                    val tiles = it.split(",")
                                    val list = ArrayList<Tile>()
                                    for (i in tiles) {
                                        val sp = i.split("-")
                                        list.add(Tile(sp[0], sp[1].toIntOrNull() ?: 0))
                                    }
                                    playSequence(list)

                                } else if (position == 1) {
                                    ConfigureIslandActivity.launch(
                                        this@ViewSessionsFragment,
                                        item.name,
                                        item.id ?: 0,
                                        item.getX(),
                                        item.getY(),
                                        item.getTileCount(),
                                        7
                                    )

                                }
                            }
                        } catch (e: Exception) {

                        }
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

    class RxtScoreAdapters(
        val list: ArrayList<GetMemberScoresReport.Data>,
        val listener: ItemClickListener<GetMemberScoresReport.Data>?
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
        var service_header: TextView? = itemView.findViewById(R.id.tv_service)
        var program_header: TextView? = itemView.findViewById(R.id.tv_program)
        var trainer_header: TextView? = itemView.findViewById(R.id.tv_trainer)
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

        fun bind(item: GetMemberScoresReport.Data?) {
            Logger.e("CaloriesAdapter bind item $item")
            if (item == null)
                return

            val dur = getInt(item.duration)
            if (dur >= 3660) {
                //minutes?.text = "cal"
                //time?.text = "0"
                time?.text = String.format("%02d:%02d", dur.div(3660), dur % 60)
            } else {
                //minutes?.text = "sec"
                time?.text = String.format("%02d:%02d", dur.div(60), dur % 60)
            }
            minutes?.text = "---"

            calories?.text = "${item.exerciseType?.toUpperCase()}"
            service_header?.text = "Hits: "
            program_header?.text = "Missed: "
            trainer_header?.text = "Total: "
            service?.text = "${item.hits}"
            program?.text = "${item.missed}"
            trainer?.text = "${item.total}"
            date?.text = "${item.exerciseDate}"

        }

        fun getInt(str: String?): Int {
            try {
                return str?.toIntOrNull() ?: 0
            } catch (e: Exception) {

            }
            return 0
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

    class ViewIslandAdapters(
        val list: ArrayList<GetAllIslandsByLocation.Island>,
        val listener: ItemClickListener<GetAllIslandsByLocation.Island?>?
    ) : RecyclerView.Adapter<ViewIslandAdapters.IslandHolder>() {
        var grid = false
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewIslandAdapters.IslandHolder {
            return ViewIslandAdapters.IslandHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.list_item_rxt_view_islands,
                        parent,
                        false
                    )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewIslandAdapters.IslandHolder, position: Int) {
            holder.bind(list[position], listener)
        }

        class IslandHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var image: ImageView? = itemView.findViewById(R.id.imageView1)
            var name: TextView? = itemView.findViewById(R.id.tv_name)
            var info: TextView? = itemView.findViewById(R.id.tv_tiles)
            var tiles: TextView? = itemView.findViewById(R.id.tv_tiles_no)
            var play: View? = itemView.findViewById(R.id.imageViewPlay)

            fun bind(
                item: GetAllIslandsByLocation.Island?,
                listener: ItemClickListener<GetAllIslandsByLocation.Island?>?
            ) {
                Logger.e("CaloriesAdapter bind item $item")
                if (item == null)
                    return

                name?.text = item.name
                //name?.visibility = View.GONE
                info?.text = "${item.islandWidth} x ${item.islandHeight}"
                tiles?.text = "Tiles #: ${item.getTileCount()}"

                item?.islandImage?.let {
                    Glide.with(image!!).load(it).fitCenter().into(image!!)
                }

                play?.setOnClickListener {
                    listener?.onItemClicked(item, 0)
                }

                itemView?.setOnClickListener {
                    listener?.onItemClicked(item, 1)
                }

            }
        }

    }

}