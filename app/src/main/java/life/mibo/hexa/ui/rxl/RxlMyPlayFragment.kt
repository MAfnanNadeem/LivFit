/*
 *  Created by Sumeet Kumar on 2/20/20 2:35 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/20/20 2:29 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_reactions.*
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.rxl.RXLPrograms
import life.mibo.hexa.models.rxl.RxlExercises
import life.mibo.hexa.models.rxl.RxlProgram
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.rxl.adapter.ReflexAdapter
import life.mibo.hexa.ui.rxl.impl.ReactionObserver


class RxlMyPlayFragment : BaseFragment(),
    ReactionObserver {


    private lateinit var controller: ReactionLightController

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_reactions, c, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        controller = ReactionLightController(this, this)
        navigate(Navigator.HOME_VIEW, true)
        setHasOptionsMenu(true)
        controller.onStart()
        controller.getPrograms(Prefs.get(context).member?.id()!!)
        //log("NO_OF_PODS ${ReactionLightController.Filter.LIGHT_LOGIC.range.first}")
        swipeToRefresh?.setOnRefreshListener {
            if (isRefresh)
                return@setOnRefreshListener
            isRefresh = true
            controller.getRxlExercisesServer(Prefs.get(context).member?.id()!!)
        }
        swipeToRefresh?.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark,
            R.color.infoColor2,
            R.color.successColor
        )

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
                navigate(Navigator.RXL_COURSE_SELECT, null)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    val list = ArrayList<RxlProgram>()
    var adapter: ReflexAdapter? = null

    override fun onDataReceived(programs: ArrayList<RxlProgram>) {
        isRefresh = false
        swipeToRefresh?.isRefreshing = false
        log("onDataReceived ${programs.size}")

        if (programs.isEmpty()) {
            // this will not happen in final release, because we have at-least few public programs
            //Toasty.info(requireContext(), "No programs found").show()
            empty_view?.visibility = View.VISIBLE
            // tv_empty?.text = getString(R.string.no_program)
        } else {
            empty_view?.visibility = View.GONE
        }

        list.clear()
        list.addAll(programs)

        adapter = ReflexAdapter(list)
        val manager = LinearLayoutManager(this@RxlMyPlayFragment.activity)
        recyclerView?.layoutManager = manager
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        adapter?.setListener(object : ItemClickListener<RxlProgram> {
            override fun onItemClicked(item: RxlProgram?, position: Int) {
                log("onDataReceived onItemClicked ${item?.name}")
                if (position > 1000) {
                    when (position) {
                        2001 -> {
                            val items =
                                arrayOf<CharSequence>(
                                    getString(R.string.delete),
                                    getString(R.string.cancel)
                                )

                            AlertDialog.Builder(requireContext())
                                .setTitle(getString(R.string.delete_option))
                                .setItems(items) { dialog, i ->
                                    if (i == 0) {
                                        log("delete $item")
                                        controller.deleteProgram(item) {
                                            activity?.runOnUiThread {
                                                log("delete2 $it")
                                                adapter?.delete(it)
                                            }
                                        }
                                    }
                                }.show()
                        }
                        1001 -> {
                            controller.updateProgram(item, true)
                        }
                        1002 -> {
                            controller.updateProgram(item, false)
                        }
                    }
                    return
                }
                navigate(Navigator.RXL_QUICKPLAY_DETAILS, item)
            }

        })
        adapter?.notifyDataSetChanged()
        log("onDataReceived notifyDataSetChanged ${adapter?.list?.size}")

    }

    override fun onUpdateList(programs: ArrayList<RxlProgram>) {
        adapter?.filterUpdate(programs)
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

    override fun onDestroy() {
        recyclerView?.adapter = null
        adapter = null
        super.onDestroy()
    }

}
