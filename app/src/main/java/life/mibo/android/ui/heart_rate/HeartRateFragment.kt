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
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import life.mibo.android.R
import life.mibo.android.libs.hr.HeartRateMonitor
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.home.HomeItem
import life.mibo.android.ui.main.Navigator
import life.mibo.hardware.events.HeartRateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class HeartRateFragment : BaseFragment(), HeartRateObserver {

    companion object {
        fun create(type: Int): HeartRateFragment {
            val fragment = HeartRateFragment()
            val args = Bundle()
            args.putInt("type_", type)
            fragment.arguments = args
            return fragment
        }

        fun bundle(type: Int): Bundle {
            val args = Bundle()
            args.putInt("type_", type)
            return args
        }
    }

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

    var type: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = HearRateController(this@HeartRateFragment, this)
        type = arguments?.getInt("type_") ?: 0
        viewPager2.adapter = PagerAdapter(getTabList(type), childFragmentManager)
        //tabLayout.setupWithViewPager(viewPager2)
        //controller.setRecycler(recyclerView)
    }

    fun getTabList(type: Int): ArrayList<Fragment> {
        val list = ArrayList<Fragment>()
        if (type == 1) {
            list.add(HeartRateTabMonitor.create(0))
           // list.add(HeartRateTabFragment.create(0))
            //list.add(HeartRateTabFragment.create(1))
            //list.add(HeartRateTabFragment.create(2))
        } else {
            list.add(IntroFragment())
        }
        return list
    }

    @Subscribe
    fun onHeartEvent(event: HeartRateEvent) {
        log("onHeartEvent $event")
        val frg = childFragmentManager?.fragments?.get(0)
        if (frg is HeartRateTabMonitor) {
            frg.onEvent(event)
        }
        //viewPager2?.findFragment<>()
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
        EventBus.getDefault().register(this)
    }


    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        controller.onStop()
    }

    override fun onBackPressed(): Boolean {
        navigate(Navigator.CLEAR_HOME, null)
        return false
    }

    override fun onNavBackPressed(): Boolean {
        navigate(Navigator.CLEAR_HOME, null)
        return false
    }

    class PagerAdapter(val list: ArrayList<Fragment>, val fragment: FragmentManager) :
        FragmentPagerAdapter(fragment, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return list[position]
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> {
                    return "LAST SESSION"
                }
                1 -> {
                    return "WEEKLY"
                }
                2 -> {
                    return "MONTHLY"
                }
                3 -> {
                    return "Default"
                }
            }
            return "Default"
        }

    }

    class IntroFragment() : BaseFragment() {


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
                View? {
            return inflater.inflate(R.layout.fragment_hr_intro, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            var scan = view?.findViewById<Button>(R.id.btn_scan)
            scan?.setOnClickListener {
                navigate(Navigator.HOME, HomeItem(HomeItem.Type.BOOSTER_SCAN))
            }
        }

        override fun onStart() {
            super.onStart()
            ( activity as AppCompatActivity?)?.supportActionBar?.hide()
        }

        override fun onStop() {
            ( activity as AppCompatActivity?)?.supportActionBar?.show()
            super.onStop()
        }
    }

}