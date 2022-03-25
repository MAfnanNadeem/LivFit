/*
 *  Created by Sumeet Kumar on 2/2/20 3:55 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/2/20 3:21 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.libs.hr.camera

import android.hardware.Camera
import android.view.SurfaceHolder
import life.mibo.hardware.core.Logger

abstract class CameraSupport {

    abstract fun open(cameraId: Int): CameraSupport

    /**
     * [Camera.CameraInfo.html#orientation](https://developer.android.com/reference/android/hardware/Camera.CameraInfo.html#orientation)
     *
     * 0, 90, 180, 270
     */
    abstract fun getOrientation(cameraId: Int): @CameraOrientation Int

    abstract fun setDisplayOrientation(orientation: @CameraOrientation Int)

    open var parameters: Camera.Parameters? = null

    abstract fun setPreviewDisplay(holder: SurfaceHolder?)

    abstract fun setPreviewCallback(previewCallback: Camera.PreviewCallback?)

    abstract fun startPreview()

    abstract fun stopPreview()

    abstract fun release()

    abstract fun hasFlash(): Boolean

    abstract fun setFlash(flashMode: Int): Boolean

    protected fun log(throwable: Throwable) {
        Logger.e("HeartRateOmeter", throwable)
    }

    protected fun log(message: String?) {
        Logger.e("HeartRateOmeter", "" + message)

    }
}