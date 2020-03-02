/*
 *  Created by Sumeet Kumar on 2/20/20 2:30 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/20/20 2:30 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.fragment_rxl_backdrop_tabs.*
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener
import life.mibo.hexa.ui.main.MiboApplication.Companion.context


class RxlTabFragment : BaseFragment() {
    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_rxl_backdrop_tabs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager2.adapter = RxlTabAdapter(childFragmentManager, arguments)
        //tabLayout.setupWithViewPager(viewPager2)
        smartTabLayout.setViewPager(viewPager2)
        //viewPager2
        //controller.setRecycler(recyclerView)
        val args = arguments
        if (args != null) {

        }
    }


    class RxlTabAdapter(val fragment: FragmentManager, var bundle: Bundle?) :
        FragmentPagerAdapter(fragment, BEHAVIOR_SET_USER_VISIBLE_HINT) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    val frg = RxlQuickPlayFragment()
                    frg?.arguments = bundle
                    return frg
                }
                1 -> {
                    return RxlMyPlayFragment()
                }
            }
            return RxlMyPlayFragment()
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> {
                    return context?.getString(R.string.quick_play) ?: "Quick Play"
                }
                1 -> {
                    return context?.getString(R.string.my_play) ?: "My Play"
                }
            }
            return ""
        }
    }


    // ViewPager2
    class RxlTabAdapter2(val fragment: FragmentActivity, var bundle: Bundle?) :
        FragmentStateAdapter(fragment) {

        fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    val frg = RxlQuickPlayFragment()
                    frg?.arguments = bundle
                    return frg
                }
                1 -> {
                    return RxlMyPlayFragment()
                }
            }
            return RxlMyPlayFragment()
        }

        fun getCount(): Int {
            return 2
        }

        fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> {
                    return context?.getString(R.string.quick_play) ?: "Quick Play"
                }
                1 -> {
                    return context?.getString(R.string.my_play) ?: "My Play"
                }
            }
            return ""
        }


        override fun getItemCount(): Int {
            return getCount()
        }

        override fun createFragment(position: Int): Fragment {
            return getItem(position)
        }
    }

    override fun onBackPressed(): Boolean {
        //navigate(Navigator.CLEAR_HOME, null)
        return super.onBackPressed()
    }
}