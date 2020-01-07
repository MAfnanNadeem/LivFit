package life.mibo.hexa.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.login.Member
import life.mibo.hexa.ui.base.BaseFragment


class HomeFragment : BaseFragment(), HomeObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: HomeController
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
        //recyclerView = root.findViewById(R.id.hexagonRecycler)
        // setRecycler(recyclerView!!)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = HomeController(this@HomeFragment, this)
        iv_dashboard_1.setGradient(intArrayOf(Color.LTGRAY, Color.GRAY, Color.DKGRAY))
        val member: Member? = Prefs.get(this.context)?.getMember(Member::class.java)
        tv_user_name.text = "${member?.firstName}  ${member?.lastName}"
        iv_user_pic.setImageDrawable(ContextCompat.getDrawable(this@HomeFragment.context!!, R.drawable.ic_person_black_24dp))
        controller.getDashboard()

        //controller.setRecycler(recyclerView!!)
    }

    override fun onDataRecieved(list: ArrayList<HomeItem>) {
        getDialog()?.dismiss()
       list.forEachIndexed { i, item ->
           when(i){
               0 -> {
                   constraintLayout2.visibility = View.VISIBLE
                   group.visibility = View.VISIBLE
                   item.bind(iv_dashboard_1, iv_dashboard_1_icon, iv_dashboard_1_text)
               }
               1 -> {
                   group2.visibility = View.VISIBLE
                   item.bind(iv_dashboard_2, iv_dashboard_2_icon, iv_dashboard_2_text)
               }
               2 -> {
                   group3.visibility = View.VISIBLE
                   item.bind(iv_dashboard_3, iv_dashboard_3_icon, iv_dashboard_3_text)
               }
               3 -> {
                   constraintLayout3.visibility = View.VISIBLE
                   group4.visibility = View.VISIBLE
                   item.bind(iv_dashboard_4, iv_dashboard_4_icon, iv_dashboard_4_text)
               }
               4 -> {
                   group5.visibility = View.VISIBLE
                   item.bind(iv_dashboard_5, iv_dashboard_5_icon, iv_dashboard_5_text)
               }
               5 -> {
                   constraintLayout4.visibility = View.VISIBLE
                   group6.visibility = View.VISIBLE
                   item.bind(iv_dashboard_6, iv_dashboard_6_icon, iv_dashboard_6_text)
               }
               6 -> {
                   group7.visibility = View.VISIBLE
                   item.bind(iv_dashboard_7, iv_dashboard_7_icon, iv_dashboard_7_text)
               }
               7 -> {
                   constraintLayout5.visibility = View.VISIBLE
                   group8.visibility = View.VISIBLE
                   item.bind(iv_dashboard_8, iv_dashboard_1_icon, iv_dashboard_1_text)
               }
               8 -> {
                   item.bind(iv_dashboard_9, iv_dashboard_1_icon, iv_dashboard_1_text)
                   group9.visibility = View.VISIBLE
               }
               8 -> {
                   item.bind(iv_dashboard_10, iv_dashboard_10_icon, iv_dashboard_10_text)
                   group10.visibility = View.VISIBLE
               }
           }
       }






    }

    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}