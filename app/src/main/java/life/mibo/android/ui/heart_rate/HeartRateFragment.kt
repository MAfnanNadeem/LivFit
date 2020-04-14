/*
 *  Created by Sumeet Kumar on 1/8/20 11:23 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 10:30 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.heart_rate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener


class HeartRateFragment : BaseFragment(), HeartRateObserver {
    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    override fun onDataReceived(list: ArrayList<HeartRateItem>) {
//        val adapter = AddProductAdapter(list)
//        recyclerView?.layoutManager = LinearLayoutManager(context)
//
//        recyclerView?.adapter = adapter
    }

    override fun onItemClicked(item: HeartRateItem?) {

    }

    private lateinit var controller: HearRateController
    lateinit var tabLayout: TabLayout
    //lateinit var viewPager2: ViewPager2
    lateinit var viewPager2: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(life.mibo.android.R.layout.fragment_heart_rate, container, false)
        tabLayout = root.findViewById(life.mibo.android.R.id.tabLayout)
        viewPager2 = root.findViewById(life.mibo.android.R.id.viewPager2)

//        tabLayout.addTab(TabLayout.Tab().setText("TODAY"))
//        tabLayout.addTab(TabLayout.Tab().setText("WEEKLY"))
//        tabLayout.addTab(TabLayout.Tab().setText("MONTHLY"))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = HearRateController(this@HeartRateFragment, this)
        viewPager2.adapter = PageAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(viewPager2)
        //controller.setRecycler(recyclerView)
    }


    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

}