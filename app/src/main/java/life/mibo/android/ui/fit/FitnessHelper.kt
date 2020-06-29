/*
 *  Created by Sumeet Kumar on 6/27/20 7:15 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/27/20 7:15 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.fit

import androidx.fragment.app.Fragment

class FitnessHelper(var fragment: Fragment) {

    companion object {
        const val GOOGLE_REQUEST_CODE = 1001
        const val GOOGLE_SUBSCRIBE_REQUEST = 1002
        const val GOOGLE_RED_REQUEST = 1003
        const val FITBIT_REQUEST_CODE = 2002
        const val SAMSUNG_REQUEST_CODE = 3001
        const val APPLE_REQUEST_CODE = 4001

    }

    interface Listener<A> {
        fun onComplete(success: Boolean, data: A?, ex: Exception?)
    }

    enum class FIT {
        SUBSCRIBE,
        READ_DATA,
        READ_WEEK_DATA,
        READ_MONTH_DATA,
        READ_CUSTOM_DATA
    }

    fun getGoogleFit(): GoogleFit {
        return GoogleFit(fragment)
    }

    fun getAppleHealth(): GoogleFit {
        return GoogleFit(fragment)
    }

}