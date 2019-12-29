package life.mibo.hexa.ui.rxl

import android.os.Bundle
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import life.mibo.hexa.MainActivity
import life.mibo.hexa.R
import life.mibo.hexa.ui.rxl.model.ReflexAdapter
import life.mibo.hexa.ui.rxl.model.ReflexFilterAdapter
import life.mibo.hexa.ui.rxl.model.ReflexModel
import life.mibo.hexa.view.backdrop.BackdropBehavior
import life.mibo.hexa.view.dialog.SheetMenu

fun <T : CoordinatorLayout.Behavior<*>> View.findBehavior(): T = layoutParams.run {
    if (this !is CoordinatorLayout.LayoutParams) throw IllegalArgumentException("View's layout params should be CoordinatorLayout.LayoutParams")

    (layoutParams as CoordinatorLayout.LayoutParams).behavior as? T
        ?: throw IllegalArgumentException("Layout's behavior is not current behavior")
}

class ReactionLightFragment2 : Fragment() {

    private lateinit var rxl: RxlViewModel
    private lateinit var controller: ReactionLightController

    private lateinit var backdropBehavior: BackdropBehavior

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_rxl_backdrop, container, false)

        backdropBehavior = root.findViewById<View>(R.id.frontLayout).findBehavior()
        with(backdropBehavior) {
            attachBackLayout(R.id.backLayout)
            attachToolbar((activity as MainActivity).toolbar)
        }
//        with(toolbar) {
//            setTitle(R.string.app_name)
//        }


        rxl = ViewModelProviders.of(this).get(RxlViewModel::class.java)

        val recycler: RecyclerView = root.findViewById(R.id.recyclerView)
        rxl.text.observe(this, Observer {
            //    textView.text = ""//it
        })
        setFilters(root.findViewById(R.id.recyclerViewTypes), 1)
        setFilters(root.findViewById(R.id.recyclerViewPods), 2)
        setFilters(root.findViewById(R.id.recyclerViewLogic), 3)
        setFilters(root.findViewById(R.id.recyclerViewPlayers), 4)
        root.findViewById<View?>(R.id.recyclerViewFilters)?.visibility = View.GONE
        setRecycler(recycler)
        setHasOptionsMenu(true)
        return root
    }

    private fun setFilters(view: RecyclerView?, type: Int = 0) {
        if (view == null)
            return
        val list = ArrayList<ReflexFilterAdapter.ReflexFilterModel>()

        when (type) {

            1 -> {
                list.add(ReflexFilterAdapter.ReflexFilterModel(21, "1"))
            }
            2 -> {
                for (i in 1..16) {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(i, "$i"))
                }
            }
            3 -> {
                list.add(ReflexFilterAdapter.ReflexFilterModel(31, "Random"))
                list.add(ReflexFilterAdapter.ReflexFilterModel(31, "Sequence"))
                list.add(ReflexFilterAdapter.ReflexFilterModel(31, "All at once"))
                list.add(ReflexFilterAdapter.ReflexFilterModel(31, "Focus"))
                list.add(ReflexFilterAdapter.ReflexFilterModel(31, "Home Base"))
            }
            4 -> {
                list.add(ReflexFilterAdapter.ReflexFilterModel(41, "1"))
                list.add(ReflexFilterAdapter.ReflexFilterModel(42, "2"))
                list.add(ReflexFilterAdapter.ReflexFilterModel(43, "3"))
                list.add(ReflexFilterAdapter.ReflexFilterModel(43, "4"))
            }
            else -> {
                for (i in 1..50) {
                    list.add(ReflexFilterAdapter.ReflexFilterModel(i, "Option $i"))
                }
            }
        }


        //list.add(ReflexFilterAdapter.ReflexFilterModel(1, "Start", type = 2))
        //list.add(ReflexFilterAdapter.ReflexFilterModel(1, "Switch", type = 3))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(1, "Pods", type = 1))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(2, "Light Logic", type = 1))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(3, "Players", type = 1))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(4, "Type", type = 1))
//        list.add(ReflexFilterAdapter.ReflexFilterModel(5, "Accessories", type = 1))

        val adapter = ReflexFilterAdapter(list)
        val manager = LinearLayoutManager(
            this@ReactionLightFragment2.activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        adapter.setListener(object : ReflexFilterAdapter.Listener {
            override fun onClick(data: ReflexFilterAdapter.ReflexFilterModel?) {
                // backdropBehavior.open(true)
                //showFilterOptions(data)
                activity?.runOnUiThread {
                    adapter?.notifyDataSetChanged()
                }
            }
        })
        view.layoutManager = manager
        view.adapter = adapter
    }

    fun setRecycler(view: RecyclerView) {
        val list = ArrayList<ReflexModel>();
        for (i in 1..50
        ) {
            list.add(ReflexModel(i))
        }
        val adapter = ReflexAdapter(list)
        val manager = LinearLayoutManager(this@ReactionLightFragment2.activity)
        view.layoutManager = manager
        view.adapter = adapter
    }

    fun showFilterOptions(data: ReflexFilterAdapter.ReflexFilterModel?) {
        if (data == null)
            return
        SheetMenu(
            data.title,
            listOf("Item 1", "Item 2", "Item 2", "Item 2", "Item 2")
        ).show(this@ReactionLightFragment2.context!!)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_reactions_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            backdropBehavior.toggle()
        }
        //Toasty.error(this@ReactionLightFragment2.context!!, "click").show()
        return super.onOptionsItemSelected(item)
    }

}