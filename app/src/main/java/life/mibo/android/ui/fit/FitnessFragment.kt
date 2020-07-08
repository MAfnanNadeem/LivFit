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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_google_fit.*
import life.mibo.android.R
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.fit.fitbit.Fitbit
import life.mibo.android.ui.home.HomeItem
import life.mibo.android.ui.main.MiboEvent

class FitnessFragment : BaseFragment() {

    val TAG = "FitnessFragment"

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        return i.inflate(R.layout.fragment_google_fit, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()


    }

    //@Synchronized
    private fun setupAdapters() {
        log("setupAdapters")
        var header = ""
        val list = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        if (GoogleFit(this).isConnected()) {
            list.add(GoogleFitStepsFragment.create(Fitbit.GOOGLE))
            titles.add("Google Fit")
            header += "Google Fit"
        }
        if (Fitbit.isLogged()) {
            list.add(GoogleFitStepsFragment.create(Fitbit.FITBIT))
            titles.add("Fitbit")
            header += " - Fitbit"
        }
        if (SHealth(context).isConnected()) {
            list.add(GoogleFitStepsFragment.create(Fitbit.SAMSUNG))
            titles.add("S Health")
            header += " - S Health"
        }
        log("setupAdapters list $list")
        log("setupAdapters titles $titles")
        log("setupAdapters titles ${titles.size}  : ${list.size}")
        when {
            list.size > 1 -> {
                empty_view?.visibility = View.GONE
                viewPager2.adapter = ViewPagerAdapter(list, this)
                TabLayoutMediator(
                    tabLayout, viewPager2,
                    TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        tab.text = titles[position]
                    }).attach()
            }
            list.size > 0 -> {
                empty_view?.visibility = View.GONE
                viewPager2.adapter = ViewPagerAdapter(list, this)
                tabLayout?.visibility = View.GONE
            }
            else -> {
                empty_view?.visibility = View.VISIBLE
                val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogPhoto)
                builder.setTitle(R.string.fitness_account_title)
                builder.setMessage(R.string.fitness_account_msg)
                builder.setPositiveButton(R.string.yes_text) { dialog, which ->
                    dialog?.dismiss()
                    navigate(0, HomeItem(HomeItem.Type.MY_ACCOUNT))
                }
                builder.setNegativeButton(R.string.no_text) { dialog, which ->
                    dialog?.dismiss()
                }

                builder.show()
            }
        }
        // viewPager2.adapter?.notifyDataSetChanged()
        viewPager2?.isUserInputEnabled = false

        MiboEvent.event("fitness_fragment", "STEPS Pages $header", header)

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