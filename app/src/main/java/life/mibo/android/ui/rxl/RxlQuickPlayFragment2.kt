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
import life.mibo.android.ui.rxl.impl.ReactionObserver2
import life.mibo.android.utils.Constants
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger


class RxlQuickPlayFragment2 : BaseFragment(),
    ReactionObserver2 {


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
        swipeToRefresh?.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark,
            R.color.infoColor2,
            R.color.successColor
        )


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
                //backdropBehavior.toggle()
                //showFilterDialog()
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

    fun showFilterDialog() {
        FilterDialog(
            requireContext(),
            object : ItemClickListener<ArrayList<ReflexFilterAdapter.ReflexFilterModel>> {
                override fun onItemClicked(
                    item: ArrayList<ReflexFilterAdapter.ReflexFilterModel>?,
                    position: Int
                ) {
                    log("FilterDialog items $item")
                }

            }).show()
    }

    val list = ArrayList<RXL>()
    var adapter: RecyclerAdapter? = null

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

            list.clear()

            programs.forEach {
                log("it.players == playersCount ${it.players()} == $playersCount")
                if (it.players() == playersCount)
                    list.add(it)
            }

            if (list.isEmpty()) {
                empty_view?.visibility = View.VISIBLE
                tv_empty?.text = """No Exercise found for selected player ($playersCount)"""
            }
            //list.addAll(programs)

            adapter = RecyclerAdapter(list)
            val manager = LinearLayoutManager(this@RxlQuickPlayFragment2.activity)
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


    class RecyclerAdapter(var list: java.util.ArrayList<RXL>?) :
        RecyclerView.Adapter<ReflexHolder>() {

        //var list: ArrayList<Item>? = null
        private var listener: ItemClickListener<RXL>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReflexHolder {
            return ReflexHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_reflex,
                    parent,
                    false
                )
            )
        }

        fun setListener(listener: ItemClickListener<RXL>) {
            this.listener = listener
        }

        override fun getItemCount(): Int {
            if (list != null)
                return list?.size!!
            return 0
        }

        private fun getItem(position: Int): RXL? {
            return list?.get(position)
        }

        override fun onBindViewHolder(holder: ReflexHolder, position: Int) {
            Logger.e("ReflexAdapter: onBindViewHolder $position")

            holder.bind(getItem(position), listener)
        }


        fun notify(id: Int) {

            list?.forEachIndexed { index, item ->
                if (item.id == id) {
                    notifyItemChanged(index)
                    return@forEachIndexed
                }
            }
        }

        fun delete(program: RxlProgram?) {
            if (program == null)
                return
            var pos = -1
            list?.forEachIndexed { index, item ->
                if (item.id == program.id) {
                    pos = index
                }
            }
            if (pos != -1) {
                list?.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }


        fun update(newList: java.util.ArrayList<RXL>) {
            if (list == null || list?.isEmpty()!!) {
                list = newList
                notifyDataSetChanged()
                return
            }

            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                    return newList[newItem].id == list!![oldItem].id
                }

                override fun getOldListSize(): Int {
                    return list!!.size
                }

                override fun getNewListSize(): Int {
                    return newList.size
                }

                override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                    return true
                }

            })
            list = newList
            result.dispatchUpdatesTo(this)

        }

        fun filterUpdate(newList: java.util.ArrayList<RXL>) {
            if (list == null || list?.isEmpty()!!) {
                list = newList
                notifyDataSetChanged()
                return
            }

            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                    return newList[newItem].id == list!![oldItem].id
                }

                override fun getOldListSize(): Int {
                    return list!!.size
                }

                override fun getNewListSize(): Int {
                    return newList.size
                }

                override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                    return true
                }

            })
            list = newList
            result.dispatchUpdatesTo(this)

        }
    }

}
