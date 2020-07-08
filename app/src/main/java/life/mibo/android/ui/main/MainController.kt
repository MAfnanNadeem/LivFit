/*
 *  Created by Sumeet Kumar on 1/14/20 4:45 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/14/20 4:45 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.main

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationView
import life.mibo.android.R
import life.mibo.android.utils.Toasty

class MainController(var navController: NavController?, val activity: MainActivity) {


    private fun getNavOptions(): NavOptions {
        return NavOptions.Builder().setExitAnim(R.anim.exit_to_left)
            .setEnterAnim(R.anim.enter_from_right).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()

    }

    private fun navigate(
        actionId: Int,
        fragmentId: Int,
        args: Bundle? = null,
        options: NavOptions? = getNavOptions(),
        extras: androidx.navigation.Navigator.Extras? = null
    ) {

        try {
            if (navController == null)
                navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
            if (actionId != 0) {
                val action = navController?.currentDestination?.getAction(actionId)
                    ?: navController?.graph?.getAction(actionId)
                if (action != null && navController?.currentDestination?.id != action.destinationId) {
                    navController?.navigate(actionId, args, options, extras)
                    return
                }
            }
            if (fragmentId != 0 && fragmentId != navController?.currentDestination?.id)
                navController?.navigate(fragmentId, args, options, extras)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MiboEvent.log(e)
            //IllegalAccessException when action id not match in fragment
            try {
                Toasty.info(activity, R.string.error_occurred, Toasty.LENGTH_SHORT, false).show()
            } catch (ex2: java.lang.Exception) {
                ex2.printStackTrace()
            }
        }
    }

    private var lastId = -1
    private var navigation: NavigationView? = null

    private fun popup(fragmentId: Int) {
        try {
            navController?.popBackStack(fragmentId, false)
            lastId = -1
        } catch (e: java.lang.Exception) {
            navigate(0, fragmentId)
        }
        if (fragmentId == R.id.navigation_home)
            navigation?.setCheckedItem(R.id.nav_home)
    }
}