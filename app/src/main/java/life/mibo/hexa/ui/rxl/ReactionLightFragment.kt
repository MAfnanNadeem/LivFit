package life.mibo.hexa.ui.rxl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.ui.rxl.model.ReflexAdapter
import life.mibo.hexa.ui.rxl.model.ReflexFilterAdapter
import life.mibo.hexa.ui.rxl.model.ReflexModel
import ru.whalemare.sheetmenu.SheetMenu

class ReactionLightFragment : Fragment() {

    private lateinit var rxl: RxlViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rxl = ViewModelProviders.of(this).get(RxlViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_reactions, container, false)
        val recycler: RecyclerView = root.findViewById(R.id.recyclerView)
        rxl.text.observe(this, Observer {
            //    textView.text = ""//it
        })
        setFilters(root.findViewById(R.id.recyclerViewFilters))
        setRecycler(recycler)
        return root
    }

    fun setFilters(view: RecyclerView) {
        val list = ArrayList<ReflexFilterAdapter.ReflexFilterModel>();
        for (i in 1..50
        ) {
            list.add(ReflexFilterAdapter.ReflexFilterModel(i, "Select $i"))
        }
        val adapter = ReflexFilterAdapter(list)
        val manager = LinearLayoutManager(
            this@ReactionLightFragment.activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        adapter.setListener(object : ReflexFilterAdapter.Listener {
            override fun onClick(data: ReflexFilterAdapter.ReflexFilterModel?) {
                showFilterOptions(data)
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
        val manager = LinearLayoutManager(this@ReactionLightFragment.activity)
        view.layoutManager = manager
        view.adapter = adapter
    }

    fun showFilterOptions(data: ReflexFilterAdapter.ReflexFilterModel?) {
        if (data == null)
            return
        SheetMenu(data.title, listOf("Item 1", "Item 2", "Item 2", "Item 2", "Item 2")).show(this@ReactionLightFragment.context!!)
    }


}