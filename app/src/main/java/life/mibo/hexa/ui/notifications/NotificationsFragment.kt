package life.mibo.hexa.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.adapters.RecyclerAdapter
import life.mibo.hexa.ui.dashboard.DashboardViewModel
import life.mibo.hexa.view.recycler.HexagonRecyclerView

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        //  val textView: TextView = root.findViewById(R.id.text_dashboard)
        val recycler: HexagonRecyclerView = root.findViewById(R.id.recyclerView)
        notificationsViewModel.text.observe(this, Observer {
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
        val adapter = RecyclerAdapter(list, 1)
        // val manager = LinearLayoutManager(this@DashboardFragment.activity)
        //view.layoutManager = manager

        view.adapter = adapter

    }
}