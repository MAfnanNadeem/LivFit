package life.mibo.android.ui.rxl

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.fragment_reactions.*
import life.mibo.android.R
import life.mibo.android.models.rxl.RxlProgram
import life.mibo.android.models.workout.RXL
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.main.Navigator
import life.mibo.android.ui.rxl.adapter.PlayersAdapter
import life.mibo.android.ui.rxl.adapter.ReflexFilterAdapter
import life.mibo.android.ui.rxl.adapter.ReflexHolder
import life.mibo.android.ui.rxl.adapter.RxlWorkoutAdapter
import life.mibo.android.ui.rxl.impl.RXLObserver
import life.mibo.android.utils.Constants
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger


class RxlQuickPlayFragment : BaseFragment(),
    RXLObserver {


    // private lateinit var rxl: RxlViewModel
    private lateinit var controller: ReactionLightController
    var recycler: RecyclerView? = null

    //private lateinit var backdropBehavior: BackdropBehavior

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        postponeEnterTransition()
        val transition =
            TransitionInflater.from(this.activity).inflateTransition(R.transition.transition_course)

        sharedElementReturnTransition = androidx.transition.ChangeScroll().apply {
            duration = 750
        }
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
        return i.inflate(R.layout.fragment_reactions, c, false)
    }

    var playersCount = 1
    var players: ArrayList<PlayersAdapter.PlayerItem>? = null

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        controller = ReactionLightController(this, this)
        //ViewModelProvider(this).get(RxlViewModel::class.java)
        //rxl = ViewModelProvider(this).get(RxlViewModel::class.java)
        // rxl = ViewModelProviders.of(this).get(RxlViewModel::class.java)d

        recycler = root.findViewById(R.id.recyclerView)
//        rxl.text.observe(this.viewLifecycleOwner, Observer {
//            //    textView.text = ""//it
//        })

        val types = arguments?.getSerializable(Constants.BUNDLE_DATA)

        if (types is List<*>) {
            playersCount = types.size
            try {
                players = types as ArrayList<PlayersAdapter.PlayerItem>?
            } catch (e: Exception) {
            }
        }

        navigate(Navigator.HOME_VIEW, true)
        // setFilters(root)
        setHasOptionsMenu(true)
        //setBackdrop()
        controller.onStart()
        controller.getRxlExercisesServer("")

        //log("NO_OF_PODS ${ReactionLightController.Filter.LIGHT_LOGIC.range.first}")
        swipeToRefresh?.setOnRefreshListener {
            if (isRefresh)
                return@setOnRefreshListener
            isRefresh = true
            controller.getRxlExercisesServer("")
        }
        setSwipeRefreshColors(swipeToRefresh)

    }

//    private fun setBackdrop() {
//
//        backdropBehavior?.addOnDropListener(object : BackdropBehavior.OnDropListener {
//            @SuppressLint("RestrictedApi")
//            override fun onDrop(dropState: BackdropBehavior.DropState, fromUser: Boolean) {
//                if (dropState == BackdropBehavior.DropState.CLOSE && isAdded) {
//                    isFilterOpen = false
//                    if (isFilterDone) {
////                        Toasty.info(
////                            this@ReactionLightFragment2.context!!,
////                            "closed " + selectedItems.keys.toIntArray().contentToString()
////                            , Toasty.LENGTH_SHORT, false
////                        ).show()
//                        controller.applyFilters()
//                        //shuffle()
//                    }
//                    //log("closed" + selectedItems.keys.toIntArray().contentToString())
//                } else if (dropState == BackdropBehavior.DropState.OPEN) {
//                    isFilterOpen = true
//                }
//                //invalidateOptionsMenu();
//                (activity as MainActivity?)?.supportActionBar?.invalidateOptionsMenu()
//            }
//        })
//    }


    var isRefresh = false


    lateinit var menu_: Menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu_ = menu
    }

    var isFilterOpen = false
    var isFilterDone = false

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isFilterOpen)
            inflater.inflate(R.menu.menu_reactions_fragment_done, menu)
        else inflater.inflate(R.menu.menu_reactions_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                //backdropBehavior.toggle()
                showFilterDialog()
            }
            R.id.action_filter_cancel -> {
                isFilterDone = false
                // backdropBehavior.close()
            }
            R.id.action_filter_done -> {
                isFilterDone = true
                //  backdropBehavior.close()
            }
        }
        //Toasty.error(this@ReactionLightFragment2.context!!, "click").show()
        //Toasty.error(this@ReactionLightFragment2.context!!, "click").show()
        return super.onOptionsItemSelected(item)
    }


    var results = ArrayList<ReflexFilterAdapter.ReflexFilterModel>()
    private fun showFilterDialog() {
        val copy = ArrayList(results)
        log("showFilterDialog copy --  $copy")
        FilterDialog(
            requireContext(),
            object : ItemClickListener<ArrayList<ReflexFilterAdapter.ReflexFilterModel>> {
                override fun onItemClicked(
                    item: ArrayList<ReflexFilterAdapter.ReflexFilterModel>?,
                    position: Int
                ) {
                    log("FilterDialog items $item")
                    results.clear()
                    if (item != null)
                        results.addAll(item)
                    applyFilters()
                }

            }, copy
        ).show()
    }

    private val testPlayers = true
    private fun applyFilters() {
        programsList.clear()
        if (results.isNotEmpty() && backupPrograms.isNotEmpty()) {
            for (p in backupPrograms) {
                for (r in results) {
                    when (r.filterType) {
                        1 -> {
                            if (p.isCategory(r.title))
                                addProgram(p)
                        }
                        2 -> {
                            if (p.isPod(r.title))
                                addProgram(p)
                        }
                        3 -> {
                            if (p.isAccessories(r.title))
                                addProgram(p)

                        }
                    }
                }
            }
        } else {
            for (p in backupPrograms) {
                addProgram(p)
            }
        }

        activity?.runOnUiThread {
            adapter?.notifyDataSetChanged()
        }
    }

    fun addProgram(prg: RXL) {
        //if (testPlayers || prg.players() == playersCount)
        if (prg.players() == playersCount)
            programsList.add(prg)
    }

    private val programsList = ArrayList<RXL>()
    private val backupPrograms = ArrayList<RXL>()
    private var adapter: RxlWorkoutAdapter? = null

    override fun onDataReceived2(programs: ArrayList<RXL>) {
        activity?.runOnUiThread {
            isRefresh = false
            swipeToRefresh?.isRefreshing = false
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
            backupPrograms.clear()

            programs.forEach {
               // log("it.players == playersCount ${it.players()} == $playersCount")
                //if (it.players() == playersCount)
                addProgram(it)
                backupPrograms.add(it)
            }

            if (programsList.isEmpty()) {
                empty_view?.visibility = View.VISIBLE
                tv_empty?.text = """No Exercise found for selected player ($playersCount)"""
            }
            //list.addAll(programs)

            adapter = RxlWorkoutAdapter(programsList)
            val manager = LinearLayoutManager(this@RxlQuickPlayFragment.activity)
            recycler?.layoutManager = manager
            recycler?.adapter = adapter
            recycler?.isNestedScrollingEnabled = false
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
                    item?.selectedPlayers = players
                    navigate(Navigator.RXL_QUICKPLAY_DETAILS, item)
                }

            })
            adapter?.notifyDataSetChanged()
            log("onDataReceived notifyDataSetChanged ${adapter?.list?.size}")
        }
    }

    override fun onUpdateList2(programs: ArrayList<RXL>) {
        adapter?.filterUpdate(programs)
    }

    override fun onDataReceived(programs: ArrayList<RxlProgram>) {
        isRefresh = false
        swipeToRefresh?.isRefreshing = false
    }

    override fun onUpdateList(programs: ArrayList<RxlProgram>) {
        //adapter?.filterUpdate(programs)
    }

    fun delete(item: RxlProgram) {

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

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        recyclerView?.adapter = null
        //backdropBehavior?.dispose()
        //controller?.dispose(frontLayout)
        super.onStop()
    }

    override fun onDestroy() {
        recycler?.adapter = null
        adapter = null
        super.onDestroy()
    }

}
