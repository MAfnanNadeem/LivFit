/*
 *  Created by Sumeet Kumar on 1/20/20 2:43 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/20/20 2:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.barcode

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.*
import android.media.Image
import android.os.Build
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.PlanarYUVLuminanceSource
import com.kroegerama.kaiteki.bcode.R
import java.io.ByteArrayOutputStream

internal fun AttributeSet?.handleArguments(
    context: Context, attrs: IntArray, defStyleAttr: Int, defStyleRes: Int,
    block: TypedArray.() -> Unit
) = this?.let {
    val arr = context.obtainStyledAttributes(it, attrs, defStyleAttr, defStyleRes)
    block(arr)
    arr.recycle()
}

internal typealias Style = R.styleable


internal class Debouncer(
    val debounceTime: Int
) {
    private var lastShot = 0L

    operator fun <T> invoke(block: () -> T) = if (System.currentTimeMillis() - lastShot > debounceTime) {
        block.invoke().also {
            lastShot = System.currentTimeMillis()
        }
    } else {
        null
    }
}

internal fun Image.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

internal fun PlanarYUVLuminanceSource.toBitmap() =
    Bitmap.createBitmap(renderThumbnail(), thumbnailWidth, thumbnailHeight, Bitmap.Config.ARGB_8888)