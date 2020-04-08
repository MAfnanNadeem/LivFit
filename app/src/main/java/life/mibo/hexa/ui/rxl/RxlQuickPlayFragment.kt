package life.mibo.hexa.ui.rxl

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_reactions.*
import kotlinx.android.synthetic.main.fragment_rxl_backdrop.*
import life.mibo.hexa.R
import life.mibo.hexa.models.rxl.RxlProgram
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.MainActivity
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.rxl.adapter.PlayersAdapter
import life.mibo.hexa.ui.rxl.adapter.ReflexAdapter
import life.mibo.hexa.ui.rxl.impl.ReactionObserver
import life.mibo.hexa.ui.rxl.impl.RxlViewModel
import life.mibo.hexa.utils.Constants
import life.mibo.hexa.utils.Toasty
import life.mibo.views.backdrop.BackdropBehavior


class RxlQuickPlayFragment : BaseFragment(),
    ReactionObserver {


   // private lateinit var rxl: RxlViewModel
    private lateinit var controller: ReactionLightController
    var recycler: RecyclerView? = null

    private lateinit var backdropBehavior: BackdropBehavior

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        postponeEnterTransition()
        val transition =
            TransitionInflater.from(this.activity).inflateTransition(R.transition.transition_course)

        sharedElementReturnTransition = androidx.transition.ChangeScroll().apply {
            duration = 750
        }
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
        return i.inflate(R.layout.fragment_rxl_backdrop, c, false)
    }

    var playersCount = 1
    var players: ArrayList<PlayersAdapter.PlayerItem>? = null

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        backdropBehavior = root.findViewById<View>(R.id.frontLayout).findRxlBehavior()
        with(backdropBehavior) {
            attachBackLayout(R.id.backLayout)
            attachToolbar((activity as MainActivity).toolbar)
        }
//        with(toolbar) {
//            setTitle(R.string.app_name)
//        }

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
        setFilters(root)
        setHasOptionsMenu(true)
        setBackdrop()
        controller.onStart()
        controller.getPrograms("")
        //log("NO_OF_PODS ${ReactionLightController.Filter.LIGHT_LOGIC.range.first}")
        swipeToRefresh?.setOnRefreshListener {
            if (isRefresh)
                return@setOnRefreshListener
            isRefresh = true
            controller.getRxlExercisesServer("")
        }
        swipeToRefresh?.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark,
            R.color.infoColor2,
            R.color.successColor
        )


    }

    private fun setBackdrop() {

        backdropBehavior?.addOnDropListener(object : BackdropBehavior.OnDropListener {
            @SuppressLint("RestrictedApi")
            override fun onDrop(dropState: BackdropBehavior.DropState, fromUser: Boolean) {
                if (dropState == BackdropBehavior.DropState.CLOSE && isAdded) {
                    isFilterOpen = false
                    if (isFilterDone) {
//                        Toasty.info(
//                            this@ReactionLightFragment2.context!!,
//                            "closed " + selectedItems.keys.toIntArray().contentToString()
//                            , Toasty.LENGTH_SHORT, false
//                        ).show()
                        controller.applyFilters()
                        //shuffle()
                    }
                    //log("closed" + selectedItems.keys.toIntArray().contentToString())
                } else if (dropState == BackdropBehavior.DropState.OPEN) {
                    isFilterOpen = true
                }
                //invalidateOptionsMenu();
                (activity as MainActivity?)?.supportActionBar?.invalidateOptionsMenu()
            }
        })
    }

    private fun setFilters(root: View) {
       //recyclerViewTypes.requestDisallowInterceptTouchEvent(true);

        controller.setFilters(
            root.findViewById(R.id.recyclerViewTypes),
            ReactionLightController.Filter.PROGRAM_TYPE
        )
        controller.setFilters(
            root.findViewById(R.id.recyclerViewPods),
            ReactionLightController.Filter.NO_OF_PODS
        )
//        controller.setFilters(
//            root.findViewById(R.id.recyclerViewLogic),
//            ReactionLightController.Filter.LIGHT_LOGIC
//        )
//        controller.setFilters(
//            root.findViewById(R.id.recyclerViewPlayers),
//            ReactionLightController.Filter.PLAYERS
//        )
        controller.setFilters(
            root.findViewById(R.id.recyclerViewAcces),
            ReactionLightController.Filter.ACCESSORIES
        )
    }

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
                backdropBehavior.toggle()
            }
            R.id.action_filter_cancel -> {
                isFilterDone = false
                backdropBehavior.close()
            }
            R.id.action_filter_done -> {
                isFilterDone = true
                backdropBehavior.close()
            }
        }
        //Toasty.error(this@ReactionLightFragment2.context!!, "click").show()
        //Toasty.error(this@ReactionLightFragment2.context!!, "click").show()
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
            Toasty.info(requireContext(), getString(R.string.no_program)).show()
            empty_view?.visibility = View.VISIBLE
            tv_empty?.text = getString(R.string.no_program)
        } else {
            empty_view?.let {
                it.visibility = View.GONE
            }
        }

        list.clear()

        programs.forEach {
            log("it.players == playersCount ${it.players} == $playersCount")
            if (it.players == playersCount)
                list.add(it)
        }

        if (list.isEmpty()) {
            empty_view?.visibility = View.VISIBLE
            tv_empty?.text = """No Exercise found for selected player ($playersCount)"""
        }
        //list.addAll(programs)

        adapter = ReflexAdapter(list)
        val manager = LinearLayoutManager(this@RxlQuickPlayFragment.activity)
        recycler?.layoutManager = manager
        recycler?.adapter = adapter
        recycler?.isNestedScrollingEnabled = false
        adapter?.setListener(object : ItemClickListener<RxlProgram> {
            override fun onItemClicked(item: RxlProgram?, position: Int) {
                log("onDataReceived onItemClicked ${item?.name}")
                if (position > 1000) {
                    when (position) {
                        1001 -> {
                            controller.updateProgram(item, true)
                        }
                        1002 -> {
                            controller.updateProgram(item, false)
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

    override fun onUpdateList(programs: ArrayList<RxlProgram>) {
        adapter?.filterUpdate(programs)
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
        backdropBehavior?.dispose()
        controller?.dispose(frontLayout)
        super.onStop()
    }

    override fun onDestroy() {
        recycler?.adapter = null
        adapter = null
        super.onDestroy()
    }



}

fun <T : CoordinatorLayout.Behavior<*>> View.findRxlBehavior(): T = layoutParams.run {
    if (this !is CoordinatorLayout.LayoutParams) throw IllegalArgumentException("View's layout params should be CoordinatorLayout.LayoutParams")

    (layoutParams as CoordinatorLayout.LayoutParams).behavior as? T
        ?: throw IllegalArgumentException("Layout's behavior is not current behavior")
}
