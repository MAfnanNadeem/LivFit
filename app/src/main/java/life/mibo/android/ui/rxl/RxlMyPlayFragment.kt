/*
 *  Created by Sumeet Kumar on 2/20/20 2:35 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/20/20 2:29 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_reactions.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseStatus
import life.mibo.android.models.rxl.GetMyWorkout
import life.mibo.android.models.rxl.RxlProgram
import life.mibo.android.models.workout.RXL
import life.mibo.android.models.workout.SearchWorkout
import life.mibo.android.models.workout.SearchWorkoutPost
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.ui.rxl.adapter.ReflexAdapter
import life.mibo.android.ui.rxl.adapter.RxlWorkoutAdapter
import life.mibo.android.ui.rxl.impl.ReactionObserver
import life.mibo.android.utils.Constants
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RxlMyPlayFragment : BaseFragment() {


    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_reactions, c, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        navigate(Navigator.HOME_VIEW, true)
        setHasOptionsMenu(true)
        // controller.onStart()
        /// controller.getPrograms(Prefs.get(context).member?.id()!!)
        //log("NO_OF_PODS ${ReactionLightController.Filter.LIGHT_LOGIC.range.first}")
        swipeToRefresh?.setOnRefreshListener {
            if (isRefresh)
                return@setOnRefreshListener
            isRefresh = true
            getMyPlayWorkouts()
        }
        setSwipeRefreshColors(swipeToRefresh)
        getMyPlayWorkouts()
    }


    private fun getMyPlayWorkouts() {
        val member = Prefs.get(context).member ?: return
        val data = GetMyWorkout.Data(member.id(), 1, 50, "")

        API.request.getApi()
            .getMyRxlWorkout(GetMyWorkout(data, member.accessToken))
            .enqueue(object : Callback<SearchWorkout> {

                override fun onFailure(call: Call<SearchWorkout>, t: Throwable) {
                    getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<SearchWorkout>,
                    response: Response<SearchWorkout>
                ) {
                    getDialog()?.dismiss()

                    val data = response.body()
                    log("getMyPlayWorkouts $data")
                    if (data != null) {
                        val programs = data?.data?.workout
                        val list = ArrayList<RXL>()
                        programs?.forEach {
                            it?.rxl.let { item ->
                                if (item != null) {
                                    item.id = it?.id ?: 1
                                    item.name = it?.name ?: ""
                                    item.desc = it?.description ?: ""
                                    item.borg = it?.borgRating ?: 7
                                    item.icon = it?.icon ?: ""
                                    item.total = it?.durationValue ?: "0"
                                    item.unit = it?.durationUnit ?: ""
                                    item.videoLink = it?.videoLink ?: ""
                                    list.add(item)
                                }
                            }
                        }
                        parseData(list)

                    } else {

                    }
                }
            })
    }

    var isRefresh = false


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_rxl_my_play, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_add -> {
                //backdropBehavior.toggle()
                navigate(Navigator.RXL_CUSTOMIZE, null)
                //navigate(Navigator.RXL_COURSE_SELECT, null)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    val list = ArrayList<RxlProgram>()
    var adapter: RxlWorkoutAdapter? = null

    val programsList = ArrayList<RXL>()

    fun parseData(programs: ArrayList<RXL>) {
        isRefresh = false
        swipeToRefresh?.isRefreshing = false

        activity?.runOnUiThread {
            log("onDataReceived ${programs.size}")

            if (programs.isEmpty()) {
                // this will not happen in final release, because we have at-least few public programs
                Toasty.info(requireContext(), getString(R.string.no_program)).show()
                empty_view?.visibility = View.VISIBLE
                tv_empty?.text = getString(R.string.no_program)
            } else {
                empty_view?.let {
                    it.visibility = View.GONE
                }
            }

            programsList.clear()

            programs.forEach {
                programsList.add(it)
            }

            if (programsList.isEmpty()) {
                empty_view?.visibility = View.VISIBLE
                // tv_empty?.text = """No Exercise found for selected player ($playersCount)"""
            }
            //list.addAll(programs)

            adapter = RxlWorkoutAdapter(programsList)
            val manager = LinearLayoutManager(this@RxlMyPlayFragment.activity)
            recyclerView?.layoutManager = manager
            recyclerView?.adapter = adapter
            recyclerView?.isNestedScrollingEnabled = false
            adapter?.setListener(object : ItemClickListener<RXL> {
                override fun onItemClicked(item: RXL?, position: Int) {
                    log("onDataReceived onItemClicked ${item?.name}")
                    if (position > 1000) {
                        when (position) {
                            1001 -> {
                                // controller.updateProgram(item, true)
                            }
                            1002 -> {
                                // controller.updateProgram(item, false)
                            }
                        }
                        return
                    }
                    //item?.selectedPlayers = players
                    navigate(Navigator.RXL_QUICKPLAY_DETAILS, item)
                }

            })
            adapter?.notifyDataSetChanged()
            log("onDataReceived notifyDataSetChanged ${adapter?.list?.size}")
        }
    }



    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
    }

    override fun onStop() {
        recyclerView?.adapter = null
        super.onStop()
    }

    override fun onDestroy() {
        recyclerView?.adapter = null
        adapter = null
        super.onDestroy()
    }

}
