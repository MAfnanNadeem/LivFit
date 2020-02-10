/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.dichotome.profilebar.ui.tabPager.TabFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerFragments = arrayListOf(
            Tab1Fragment.newInstance("Tab 1"),
            Tab2Fragment.newInstance("Tab 2")
        )

        val member = Prefs.get(context).member
        //drawer_user_email?.text = member.imageThumbnail
            "${member?.firstName} ${member?.lastName}"
        profileBar.apply {
            photo = ContextCompat.getDrawable(context, R.drawable.login_bg)
            subtitle = "Joined on "+SimpleDateFormat("dd MMMM, yyyy").format(Date())
            title =  "${member?.firstName} ${member?.lastName}"
            wallpaper = ContextCompat.getDrawable(context, R.drawable.login_bg)
            optionWindow.changeWallpaperButton.visibility = View.GONE
            optionWindow.changeUsernameButton.visibility = View.GONE
            optionWindow.logOutButton.visibility = View.GONE
            optionWindow.changePhotoButton.visibility = View.GONE
            tabsEnabled = false


        }
        //profilePager.adapter = TabPagerAdapter(childFragmentManager)
        //profilePager.fragments = pagerFragments

        //profileBar.setupWithViewPager(profilePager)

    }


    class Tab1Fragment() : TabFragment() {
        companion object {
            fun newInstance(tabTitle: String) = Tab1Fragment().apply {
                title = tabTitle
            }
        }
    }

    class Tab2Fragment() : TabFragment() {
        companion object {
            fun newInstance(tabTitle: String) = Tab2Fragment().apply {
                title = tabTitle
            }
        }
    }

    override fun onBackPressed(): Boolean {
       // if(profileBar.onBackPressed())
        return super.onBackPressed()
    }

}