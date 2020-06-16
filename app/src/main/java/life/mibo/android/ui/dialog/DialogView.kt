/*
 * $Created by $Sumeet $Kumar 2019.
 */

package life.mibo.android.ui.dialog

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import life.mibo.android.R


class DialogView(context: Context, attrs: AttributeSet?, res: Int) :
    AppCompatImageView(context, attrs, res),
    MyDialog.Indeterminate {
    init {
        init()
    }

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        // init()
    }

    private var mRotateDegrees: Float = 0.toFloat()
    private var mFrameTime: Int = 0
    private var mNeedToUpdateView: Boolean = false
    private var mUpdateViewRunnable: Runnable? = null


    private fun init() {
        setImageResource(R.drawable.dialog_spinner)
        mFrameTime = 1000 / 12
        mUpdateViewRunnable = object : Runnable {
            override fun run() {
                mRotateDegrees += 30f
                mRotateDegrees = if (mRotateDegrees < 360) mRotateDegrees else mRotateDegrees - 360
                invalidate()
                if (mNeedToUpdateView) {
                    postDelayed(this, mFrameTime.toLong())
                }
            }
        }
    }

    override fun setAnimationSpeed(scale: Float) {
        mFrameTime = (1000f / 12f / scale).toInt()
    }

    override fun startUpdate() {

    }

    override fun onDraw(canvas: Canvas) {
        canvas.rotate(mRotateDegrees, (width / 2).toFloat(), (height / 2).toFloat())
        super.onDraw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mNeedToUpdateView = true
        post(mUpdateViewRunnable)
    }

    override fun onDetachedFromWindow() {
        mNeedToUpdateView = false
        super.onDetachedFromWindow()
    }


}