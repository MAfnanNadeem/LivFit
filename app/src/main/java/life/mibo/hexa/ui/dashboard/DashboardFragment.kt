package life.mibo.hexa.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.adapters.RecyclerAdapter

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
      //  val textView: TextView = root.findViewById(R.id.text_dashboard)
        val recycler: RecyclerView = root.findViewById(R.id.recyclerView)
        dashboardViewModel.text.observe(this, Observer {
        //    textView.text = ""//it
        })
        setRecycler(recycler)
        return root
    }

    fun setRecycler(view: RecyclerView) {
        val list = ArrayList<RecyclerAdapter.Item>();
        for (i in 1..50
        ) {
            list.add(RecyclerAdapter.Item(0, "$i"))
        }
        val adapter = RecyclerAdapter(list)
       // val manager = LinearLayoutManager(this@DashboardFragment.activity)
        //view.layoutManager = manager
        view.adapter = adapter

    }

}