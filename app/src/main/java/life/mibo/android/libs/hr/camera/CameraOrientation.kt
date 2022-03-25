/*
 *  Created by Sumeet Kumar on 2/2/20 3:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/2/20 3:21 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.libs.hr.camera

import androidx.annotation.IntDef

@Target(AnnotationTarget.TYPE)
@IntDef(0, 90, 180, 270)
@Retention(AnnotationRetention.SOURCE)
annotation class CameraOrientation