/*
 *  Created by Sumeet Kumar on 1/25/20 5:36 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/25/20 5:36 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

//import kotlinx.android.synthetic.main.fragment_rxl_initial.*
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_rxl_players.*
import life.mibo.hardware.SessionManager
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.rxl.adapter.PlayersAdapter
import life.mibo.hexa.ui.select_program.ProgramDialog
import life.mibo.hexa.utils.Utils

class ReflexSelectFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rxl_players, container, false)
    }

    var playerName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        btn_quickplay?.setOnClickListener {
//            navigate(Navigator.RXL_TABS, null)
//        }
//
//        btn_create.setOnClickListener {
//           // navigate(Navigator.RXL_COURSE_SELECT, null)
//            navigate(Navigator.RXL_TABS_2, null)
//        }

//        Single.just("").delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
//            navigate(Navigator.HOME_VIEW, true)
//        }.subscribe()

        val member = Prefs.get(context).member

        playerName = member?.firstName + " " + member?.lastName

        onCreate()
    }

    var adapter: PlayersAdapter? = null
    fun onCreate() {
        radio_group?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.player_one -> {
                    updateView(1)
                }
                R.id.player_two -> {
                    updateView(2)

                }
                R.id.player_three -> {
                    updateView(3)

                }
                R.id.player_four -> {
                    updateView(4)
                }
            }
            btn_next?.isEnabled = true
        }

//        val list = arrayListOf(
//            PlayersAdapter.PlayerItem(1, "", "Player 1 Name"),
//            PlayersAdapter.PlayerItem(1, "", "Player 5 Name"),
//            PlayersAdapter.PlayerItem(1, "", "Player 3 Name"),
//            PlayersAdapter.PlayerItem(1, "", "Player 3 Name"),
//            PlayersAdapter.PlayerItem(1, "", "Player 3 Name"),
//            PlayersAdapter.PlayerItem(1, "", "Player 3 Name"),
//            PlayersAdapter.PlayerItem(1, "", "Player 4 Name")
//        )

        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter =
            PlayersAdapter(ArrayList(), object : ItemClickListener<PlayersAdapter.PlayerItem> {
                override fun onItemClicked(item: PlayersAdapter.PlayerItem?, position: Int) {
                    log("PlayersAdapter onItemClicked $item")
                    showColorPicker(item)
                }

            })
        recyclerView.adapter = adapter

        btn_next?.setOnClickListener {
            onNextClicked()
        }
        SessionManager.initUser()
        SessionManager.getInstance().userSession.isRxl = true
        SessionManager.getInstance().userSession.rxlPlayers = null
        SessionManager.getInstance().userSession.rxlDevices = null
    }

    private fun onNextClicked() {
        //var next = false

        var error = false
        var id = -1
        val list = adapter?.list

        if (!list.isNullOrEmpty()) {
            for (it in list) {
                log("onNextClicked playerName ${it.playerName}")
                if (it.playerName.isNullOrEmpty()) {
                    error = true
                    id = it.id
                    break
                }
            }
        }

        if (error && id >= 0) {
            adapter?.updateError(id, getError(id))
        } else {
            // SessionManager.initUser()
            Utils.hideKeyboard(this.activity)
            SessionManager.getInstance().userSession.rxlPlayers = list
            navigate(Navigator.RXL_TABS, list)
        }
    }

    fun getError(id: Int): Int {
        when (id) {
            1 -> {
                return R.string.player_1_error
            }
            2 -> {
                return R.string.player_2_error
            }
            3 -> {
                return R.string.player_3_error
            }
            4 -> {
                return R.string.player_4_error
            }
            else -> {
                return R.string.player_1_error
            }
        }
    }

    fun getColorError(id: Int): Int {
        when (id) {
            1 -> {
                return R.string.player_1_color_error
            }
            2 -> {
                return R.string.player_2_color_error
            }
            3 -> {
                return R.string.player_3_color_error
            }
            4 -> {
                return R.string.player_4_color_error
            }
            else -> {
                return R.string.player_1_color_error
            }
        }
    }


    fun showColorPicker(id: PlayersAdapter.PlayerItem?) {
        if (id == null)
            return
        ProgramDialog(requireContext(), arrayListOf(), object : ItemClickListener<Program> {
            override fun onItemClicked(item: Program?, position: Int) {
                log("ProgramDialog Colors color = ${item?.id}  position $position & id $id")
                item?.id?.let {
                    adapter?.updateColor(id, it)
                    log("ProgramDialog Colors Changed")

                }
            }

        }, ProgramDialog.COLORS).showColors()
    }


    private fun updateView(type: Int) {
        val list = ArrayList<PlayersAdapter.PlayerItem>()
        when (type) {
            1 -> {
                list.add(PlayersAdapter.PlayerItem(1, playerName, "Player 1 Name", 0, Color.RED))
            }
            2 -> {
                list.add(PlayersAdapter.PlayerItem(1, playerName, "Player 1 Name", 0, Color.RED))
                list.add(PlayersAdapter.PlayerItem(2, "", "Player 2 Name", 0, Color.GREEN))

            }
            3 -> {
                list.add(PlayersAdapter.PlayerItem(1, playerName, "Player 1 Name", 0, Color.RED))
                list.add(PlayersAdapter.PlayerItem(2, "", "Player 2 Name", 0, Color.GREEN))
                list.add(PlayersAdapter.PlayerItem(3, "", "Player 3 Name", 0, Color.BLUE))

            }
            4 -> {
                list.add(PlayersAdapter.PlayerItem(1, playerName, "Player 1 Name", 0, Color.RED))
                list.add(PlayersAdapter.PlayerItem(2, "", "Player 2 Name", 0, Color.GREEN))
                list.add(PlayersAdapter.PlayerItem(3, "", "Player 3 Name", 0, Color.BLUE))
                list.add(PlayersAdapter.PlayerItem(4, "", "Player 4 Name", 0, Color.CYAN))
            }
        }

        adapter?.update(list)
    }

    override fun onBackPressed(): Boolean {
        navigate(Navigator.CLEAR_HOME, null)
        return false
    }
}
