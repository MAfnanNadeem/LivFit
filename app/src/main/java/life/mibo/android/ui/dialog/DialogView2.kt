/*
 * $Created by $Sumeet $Kumar 2019.
 */

package life.mibo.android.ui.dialog

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.animation.*
import androidx.appcompat.widget.AppCompatImageView
import life.mibo.android.R


class DialogView2(context: Context, attrs: AttributeSet?, res: Int) :
    AppCompatImageView(context, attrs, res),
    MyDialog.Indeterminate {
    init {
        init()
    }

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        // init()
    }



    private fun init() {
        setImageResource(R.drawable.mibo_loading_logo)
//        val fadeIn = AlphaAnimation(0f, 1f)
//        fadeIn.interpolator = DecelerateInterpolator()
//        fadeIn.duration = 200
//
//        val fadeOut = AlphaAnimation(1f, 0f)
//        fadeOut.interpolator = AccelerateInterpolator()
//        fadeOut.startOffset = 200
//        fadeOut.duration = 200
//
//        val animation = AnimationSet(false)
//        animation.repeatCount = -1
//        animation.addAnimation(fadeIn)
//        animation.addAnimation(fadeOut)

    }

    override fun setAnimationSpeed(scale: Float) {
       // mFrameTime = (1000f / 12f / scale).toInt()
    }

    override fun startUpdate() {
        this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.item_animation_scale_dialog))
    }

    override fun onDraw(canvas: Canvas) {
        //canvas.rotate(mRotateDegrees, (width / 2).toFloat(), (height / 2).toFloat())
        super.onDraw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //mNeedToUpdateView = true
       // post(mUpdateViewRunnable)
    }

    override fun onDetachedFromWindow() {
       // mNeedToUpdateView = false
        super.onDetachedFromWindow()
    }


}