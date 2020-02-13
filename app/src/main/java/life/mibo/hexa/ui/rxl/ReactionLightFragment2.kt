package life.mibo.hexa.ui.rxl

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_reactions.*
import life.mibo.hexa.R
import life.mibo.hexa.models.rxl.RXLPrograms
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.MainActivity
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.rxl.adapter.ReflexAdapter
import life.mibo.hexa.ui.rxl.adapter.ReflexFilterAdapter
import life.mibo.hexa.ui.rxl.impl.ReactionObserver
import life.mibo.hexa.ui.rxl.impl.RxlViewModel
import life.mibo.hexa.utils.Toasty
import life.mibo.views.backdrop.BackdropBehavior
import life.mibo.views.dialog.SheetMenu


class ReactionLightFragment2 : BaseFragment(),
    ReactionObserver {


    private lateinit var rxl: RxlViewModel
    private lateinit var controller: ReactionLightController
    var recycler: RecyclerView? = null

    private lateinit var backdropBehavior: BackdropBehavior

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_rxl_backdrop, c, false)
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
        rxl.text.observe(this, Observer {
            //    textView.text = ""//it
        })
        navigate(Navigator.HOME_VIEW, true)
        setFilters(root.findViewById(R.id.recyclerViewTypes), 1)
        setFilters(root.findViewById(R.id.recyclerViewPods), 2)
        setFilters(root.findViewById(R.id.recyclerViewLogic), 3)
        setFilters(root.findViewById(R.id.recyclerViewPlayers), 4)
        setFilters(root.findViewById(R.id.recyclerViewAcces), 5)
        // root.findViewById<View?>(R.id.recyclerViewFilters)?.visibility = View.GONE
        ///setRecycler(recycler)
        setHasOptionsMenu(true)
        backdropBehavior?.addOnDropListener(object : BackdropBehavior.OnDropListener {
            override fun onDrop(dropState: BackdropBehavior.DropState, fromUser: Boolean) {
                if (dropState == BackdropBehavior.DropState.CLOSE && isAdded) {
                    isFilterOpen = false
                    if (isFilterDone) {
                        Toasty.info(
                            this@ReactionLightFragment2.context!!,
                            "closed " + selectedItems.keys.toIntArray().contentToString()
                            , Toasty.LENGTH_SHORT, false
                        ).show()
                        //shuffle()
                    }
                    log("closed" + selectedItems.keys.toIntArray().contentToString())
                } else if (dropState == BackdropBehavior.DropState.OPEN) {
                    isFilterOpen = true
                }
                //invalidateOptionsMenu();
                (activity as MainActivity?)?.supportActionBar?.invalidateOptionsMenu()
            }
        })
        controller.onStart()
        controller.getPrograms()
        log("NO_OF_PODS ${Filter.LIGHT_LOGIC.range.first}")
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

    var isRefresh = false

    // TODO Filters...........
    //val NO_OF_PODS = IntRange(1, 20)
    //    val NO_OF_PODS = 1..20
    ////    val PROGRAM_TYPE : IntRange = 21..29
    ////    val LIGHT_LOGIC : IntRange = 21..29
    enum class Filter(val range: IntRange) {
        NO_OF_PODS(1..20),
        PROGRAM_TYPE(21..29),
        LIGHT_LOGIC(IntRange(31, 35)),
        PLAYERS(41..44),
        ACCESSORIES(51..61)
    }


    @SuppressLint("CheckResult")
    private fun setFilters(view: RecyclerView?, type: Int = 0) {
        if (view == null)
            return
        val list = ArrayList<ReflexFilterAdapter.ReflexFilterModel>()

        Observable.fromCallable {
            when (type) {

                1 -> {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(21, "Agility"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(22, "Balanced"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(23, "Core"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(24, "Flexibility"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(25, "Power"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(26, "Reaction Time"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(27, "Speed"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(28, "Stamina"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(29, "Strength"))
                }
                2 -> {
                    for (i in 1..16) {
                        list.add(ReflexFilterAdapter.ReflexFilterModel(i, "$i"))
                    }
                }
                3 -> {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(31, "Random"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(32, "Sequence"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(33, "All at once"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(34, "Focus"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(35, "Home Base"))
                }
                4 -> {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(41, "1"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(42, "2"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(43, "3"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(44, "4"))
                }
                5 -> {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(51, "No Accessories"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(52, "Battle Rope"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(53, "Laddar"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(54, "Medicine Ball"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(55, "Mirror"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(56, "Poll"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(57, "Pul Up Bar"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(58, "Rig"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(59, "Suspension Straps"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(60, "Tree"))
                    list.add(ReflexFilterAdapter.ReflexFilterModel(61, "Resistance Band"))
                }
                else -> {
                    for (i in 1..50) {
                        list.add(ReflexFilterAdapter.ReflexFilterModel(i, "Option $i"))
                    }
                }
            }
        }.subscribe {
            val adapter =
                ReflexFilterAdapter(list, 3)
            val manager = LinearLayoutManager(
                this@ReactionLightFragment2.activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter.setListener(filterListener)
            view.layoutManager = manager
            view.adapter = adapter
            view.isNestedScrollingEnabled = false
        }


        //list.add(ReflexFilterAdapter.ReflexFilterModel(1, "Start", type = 2))
        //list.add(ReflexFilterAdapter.ReflexFilterModel(1, "Switch", type = 3))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(1, "Pods", type = 1))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(2, "Light Logic", type = 1))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(3, "Players", type = 1))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(4, "Type", type = 1))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(5, "Accessories", type = 1))


    }


    val selectedItems = HashMap<Int, ReflexFilterAdapter.ReflexFilterModel>()


    private val filterListener = object : ReflexFilterAdapter.Listener {
        override fun onClick(data: ReflexFilterAdapter.ReflexFilterModel?) {
            if (data != null)
                selectedItems[data.id] = data
            // backdropBehavior.open(true)
            //showFilterOptions(data)

        }
    }

    fun applyFilters() {
        if (selectedItems.size == 0)
            return

        selectedItems.forEach {
            if (it.value.isSelected) {

            }
        }
    }


    private fun filterList(type: Int): ArrayList<RXLPrograms.Program> {
        var list = ArrayList<RXLPrograms.Program>()
        var result = ArrayList<RXLPrograms.Program>()
        when (type) {
            in Filter.NO_OF_PODS.range -> {
                list.forEach {
                    if (it.numberOfRxl == type) {
                        result.add(it.copy())
                    }
                }

            }
            in Filter.PROGRAM_TYPE.range -> {
//                list.add(ReflexFilterAdapter.ReflexFilterModel(21, "Agility"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(22, "Balanced"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(23, "Core"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(24, "Flexibility"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(25, "Power"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(26, "Reaction Time"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(27, "Speed"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(28, "Stamina"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(29, "Strength"))
                when (type) {
                    21 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    22 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    23 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    24 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    25 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    26 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    27 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    28 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    29 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                }
            }
            in Filter.LIGHT_LOGIC.range -> {


            }
            in Filter.PLAYERS.range -> {


            }
            in Filter.ACCESSORIES.range -> {


            }

        }
        return result
    }


    fun showFilterOptions(data: ReflexFilterAdapter.ReflexFilterModel?) {
        if (data == null)
            return
        SheetMenu(
            data.title,
            listOf("Item 1", "Item 2", "Item 2", "Item 2", "Item 2")
        ).show(this@ReactionLightFragment2.context!!)
    }

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

    val list = ArrayList<RXLPrograms.Program>()
    var adapter: ReflexAdapter? = null

    override fun onDataReceived(programs: ArrayList<RXLPrograms.Program>) {
        isRefresh = false
        swipeToRefresh?.isRefreshing = false
        log("onDataReceived ${programs.size}")

        if (programs.isEmpty()) {
            // this will not happen in final release, because we have at-least few public programs
            Toasty.info(requireContext(), "No programs found").show()
        }

        list.clear()
        list.addAll(programs)

        adapter = ReflexAdapter(list)
        val manager = LinearLayoutManager(this@ReactionLightFragment2.activity)
        recycler?.layoutManager = manager
        recycler?.adapter = adapter
        recycler?.isNestedScrollingEnabled = false
        adapter?.setListener(object : ItemClickListener<RXLPrograms.Program> {
            override fun onItemClicked(item: RXLPrograms.Program?, position: Int) {
                log("onDataReceived onItemClicked ${item?.name}")
                if (position > 1000) {
                    when (position) {
                        2001 -> {
                            val items =
                                arrayOf<CharSequence>("Update", "Delete")

                            AlertDialog.Builder(requireContext()).setTitle("Select Option")
                                .setItems(items) { dialog, i ->
                                    if (i == 1) {
                                        item?.let {
                                            adapter?.delete(it)
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
                navigate(Navigator.RXL_DETAILS, item)
            }

        })
        adapter?.notifyDataSetChanged()
        log("onDataReceived notifyDataSetChanged ${adapter?.list?.size}")

    }

    fun delete(item: RXLPrograms.Program){

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

fun <T : CoordinatorLayout.Behavior<*>> View.findBehavior(): T = layoutParams.run {
    if (this !is CoordinatorLayout.LayoutParams) throw IllegalArgumentException("View's layout params should be CoordinatorLayout.LayoutParams")

    (layoutParams as CoordinatorLayout.LayoutParams).behavior as? T
        ?: throw IllegalArgumentException("Layout's behavior is not current behavior")
}
