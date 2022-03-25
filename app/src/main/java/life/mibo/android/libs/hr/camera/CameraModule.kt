/*
 *  Created by Sumeet Kumar on 2/2/20 3:55 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/2/20 3:21 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.libs.hr.camera

import android.content.Context

object CameraModule {


    fun provideCameraSupport(context: Context): CameraSupport {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            CameraPostLolipop(context)
//        } else {
        return CameraPreLolipop()
//        }
    }
}