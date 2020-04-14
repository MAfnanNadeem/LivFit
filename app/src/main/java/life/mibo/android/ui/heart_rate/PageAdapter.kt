/*
 *  Created by Sumeet Kumar on 1/8/20 11:26 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 11:26 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.heart_rate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

//import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(val fragment: FragmentManager) :
    FragmentPagerAdapter(fragment, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return HeartRateTabFragment.create(position)
    }

    override fun getCount(): Int {
        return 3
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


//    override fun getItemCount(): Int {
//        return 3
//    }
//
//    override fun createFragment(position: Int): Fragment {
//        return HeartRateTabFragment.create(position)
//    }
}