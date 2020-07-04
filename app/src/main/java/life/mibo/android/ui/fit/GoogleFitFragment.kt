/*
 *  Created by Sumeet Kumar on 6/3/20 2:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/14/20 10:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_google_fit.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseFragment

class GoogleFitFragment : BaseFragment() {

    val TAG = "GoogleFitFragment"

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_google_fit, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = arrayListOf<Fragment>(
            GoogleFitStepsFragment.create(0),
            FitHistoryFragment.create(1),
            FitHistoryFragment.create(2),
            GoogleFitHistoryFragment()
        )

        viewPager2.adapter = ViewPagerAdapter(list, this)

        TabLayoutMediator(tabLayout, viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = "History"
                    1 -> tab.text = "Sessions"
                    2 -> tab.text = "Sensors"
                    3 -> tab.text = "Record"
                    else -> tab.text = "Tab " + (position + 1)
                }

            }).attach()
    }

    class ViewPagerAdapter(val list: List<Fragment>, manager: Fragment) :
        FragmentStateAdapter(manager) {

        @NonNull
        override fun createFragment(position: Int): Fragment {
            return list[position]
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }
}