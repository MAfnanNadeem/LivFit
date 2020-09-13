/*
 *  Created by Sumeet Kumar on 2/20/20 2:58 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/20/20 2:58 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_rxl_backdrop_tabs2.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.main.Navigator


class RxlTabFragment2 : BaseFragment() {
    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_rxl_backdrop_tabs2, container, false)
    }

    var adapter: RxlTabAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RxlTabAdapter(childFragmentManager, context)
        viewPager2.adapter = adapter
        //tabLayout.setupWithViewPager(viewPager2)
        smartTabLayout.setViewPager(viewPager2)
        //controller.setRecycler(recyclerView)
        filter_icon?.setOnClickListener {
            dialogClicked()
            //val d = BottomSheetDialog(context!!)
        }
        viewPager2?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                frg = position.plus(1)
                updateButton()
            }

        })
        setHasOptionsMenu(false)
    }

    var frg = 0;
    private fun dialogClicked() {
        //frg = adapter?.current ?: 1
        if (frg == 1)
            FilterDialog(requireContext(), null).show()
        else if (frg == 2) {
            navigate(Navigator.RXL_COURSE_SELECT, null)
        }
    }

    private fun updateButton() {
        if (frg == 1) {
            filter_icon?.setImageResource(R.drawable.ic_filter_menu)
        } else if (frg == 2) {
            filter_icon?.setImageResource(R.drawable.ic_add_circle_24dp)
        }
    }


    class RxlTabAdapter(val fragment: FragmentManager, var context: Context?) :
        FragmentPagerAdapter(fragment, BEHAVIOR_SET_USER_VISIBLE_HINT) {

        // var current = 0;

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    //current = 1
                    return RxlQuickPlayFragmentOld()
                }
                1 -> {
                    //current = 2
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

}