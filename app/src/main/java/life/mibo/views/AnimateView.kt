package life.mibo.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.View
import java.lang.ref.WeakReference

class AnimateView(view: View) {
    private val view: WeakReference<View>?

    init {
        this.view = WeakReference(view)
//        if (this.view.get() != null) {
//            if (!this.view.get()?.hasOnClickListeners()!!) {
//                this.view.get()?.setOnClickListener{ }
//            }
//        }
        if (this.view.get() != null)
            explode()
    }

    private fun explode() {
        view?.get()?.setOnTouchListener(View.OnTouchListener { v, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    //  has_touch = true
                    animate(v, 0)
                }
                MotionEvent.ACTION_CANCEL -> {
                    v.animate().cancel()
                    animate(v, 1)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    v.animate().cancel()
                    animate(v, 2)
                    animate(v, 3)
                    v.callOnClick()
                    false
                }
            }
            false
        })
    }

    private fun animate(view: View, sequence: Int) {

        var delay = 0
        var duration = 0
        var zoomX = 0f
        var zoomY = 0f

        when (sequence) {
            0 -> {
                zoomX = 0.9f
                zoomY = 0.9f
                duration = 100
                delay = 0
            }
            1 -> {
                zoomX = 1f
                zoomY = 1f
                duration = 100
                delay = 0
            }
            2 -> {
                zoomX = 0.9f
                zoomY = 0.9f
                duration = 100
                delay = 0
            }
            3 -> {
                zoomX = 1f
                zoomY = 1f
                duration = 100
                delay = 101
            }
        }

        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", zoomX)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", zoomY)
        val animatorSet = AnimatorSet()
        scaleX.duration = duration.toLong()
        scaleY.duration = duration.toLong()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.startDelay = delay.toLong()
        animatorSet.start()
    }

}