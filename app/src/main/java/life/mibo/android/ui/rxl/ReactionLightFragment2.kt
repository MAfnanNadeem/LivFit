package life.mibo.android.ui.rxl

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_reactions.*
import life.mibo.android.R
import life.mibo.android.core.findBehavior
import life.mibo.android.models.rxl.RxlProgram
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.main.MainActivity
import life.mibo.android.ui.main.Navigator
import life.mibo.android.ui.rxl.adapter.ReflexAdapter
import life.mibo.android.ui.rxl.impl.ReactionObserver
import life.mibo.android.ui.rxl.impl.RxlViewModel
import life.mibo.android.utils.Toasty
import life.mibo.views.backdrop.BackdropBehavior


class ReactionLightFragment2 : BaseFragment(),
    ReactionObserver {


    private lateinit var rxl: RxlViewModel
    private lateinit var controller: ReactionLightController
    var recycler: RecyclerView? = null

    private lateinit var backdropBehavior: BackdropBehavior

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_rxl_backdrop2, c, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        backdropBehavior = root.findViewById<View>(R.id.frontLayout).findBehavior()
        with(backdropBehavior) {
            attachBackLayout(R.id.backLayout)
            attachToolbar((activity as MainActivity).toolbar)
        }
//        with(toolbar) {
//            setTitle(R.string.app_name)
//        }

        controller = ReactionLightController(this, this)
        //ViewModelProvider(this).get(RxlViewModel::class.java)
        rxl = ViewModelProvider(this).get(RxlViewModel::class.java)
        // rxl = ViewModelProviders.of(this).get(RxlViewModel::class.java)d

        recycler = root.findViewById(R.id.recyclerView)
        rxl.text.observe(this.viewLifecycleOwner, Observer {
            //    textView.text = ""//it
        })
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
            controller.getProgramsServer()
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
        controller.setFilters(
            root.findViewById(R.id.recyclerViewTypes),
            ReactionLightController.Filter.PROGRAM_TYPE
        )
        controller.setFilters(
            root.findViewById(R.id.recyclerViewPods),
            ReactionLightController.Filter.NO_OF_PODS
        )
        controller.setFilters(
            root.findViewById(R.id.recyclerViewLogic),
            ReactionLightController.Filter.LIGHT_LOGIC
        )
        controller.setFilters(
            root.findViewById(R.id.recyclerViewPlayers),
            ReactionLightController.Filter.PLAYERS
        )
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
            Toasty.info(requireContext(), "No programs found").show()
        }

        //list.clear()
       // list.addAll(programs)

        adapter = ReflexAdapter(list)
        val manager = LinearLayoutManager(this@ReactionLightFragment2.activity)
        recycler?.layoutManager = manager
        recycler?.adapter = adapter
        recycler?.isNestedScrollingEnabled = false
        adapter?.setListener(object : ItemClickListener<RxlProgram> {
            override fun onItemClicked(item: RxlProgram?, position: Int) {
//                log("onDataReceived onItemClicked ${item?.name}")
//                if (position > 1000) {
//                    when (position) {
//                        2001 -> {
//                            val items =
//                                arrayOf<CharSequence>("Update", "Delete")
//
//                            AlertDialog.Builder(requireContext()).setTitle("Select Option")
//                                .setItems(items) { dialog, i ->
//                                    if (i == 1) {
//                                        log("delete $item")
//                                        controller.deleteProgram(item) {
//                                            activity?.runOnUiThread {
//                                                log("delete2 $it")
//                                                adapter?.delete(it)
//                                            }
//                                        }
//                                    }
//                                }.show()
//                        }
//                        1001 -> {
//                            controller.updateProgram(item, true)
//                        }
//                        1002 -> {
//                            controller.updateProgram(item, false)
//                        }
//                    }
//                    return
//                }
//                navigate(Navigator.RXL_DETAILS, item)
            }

        })
        adapter?.notifyDataSetChanged()
        log("onDataReceived notifyDataSetChanged ${adapter?.list?.size}")

    }

    override fun onUpdateList(programs: ArrayList<RxlProgram>) {
       // adapter?.filterUpdate(programs)
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

}