package life.mibo.hexa.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment


class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel
    var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        // val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            //  textView.text = it
        })

        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        recyclerView = root.findViewById(R.id.hexagonRecycler)
        setRecycler(recyclerView!!)
        return root
    }

    private fun setRecycler(view: RecyclerView) {
        val list = ArrayList<HomeItem>();
        for (i in 1..20
        ) {
            list.add(HomeItem(0, "$i"))
        }
        val adapter = HomeAdapter(list)
        //val manager = LinearLayoutManager(this@HomeFragment.activity)
        val grid = GridLayoutManager(this@HomeFragment.activity, 3)
//        grid.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                var size = 1
//                if ((position + 1) % 5 == 0) {
//                    size = 2
//                }
//                return size
//            }
//        }


        view.layoutManager = grid

        view.adapter = adapter

    }
}