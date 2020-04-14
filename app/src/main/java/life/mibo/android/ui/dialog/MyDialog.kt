/*
 * $Created by $Sumeet $Kumar 30/12/2019.
 */

package life.mibo.android.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import life.mibo.android.R
import life.mibo.android.utils.Utils


class MyDialog {

    interface Indeterminate {
        fun setAnimationSpeed(scale: Float)
    }

    companion object {
        fun create(context: Context): MyDialog {
            return MyDialog(context)
        }

        // use this function to
        operator fun get(c: Context): MyDialog {
            return get(c,  cancel = false)
        }

        operator fun get(c: Context, title: String? = null, msg: String? = null, cancel: Boolean = false): MyDialog {
            return MyDialog(c)
                .setCustomView()
                .setLabel(title)
                .setDetailsLabel(msg)
                .setCancellable(cancel)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
        }
    }


    private var mProgressDialog: ProgressDialog? = null
    private var mDimAmount: Float = 0.toFloat()
    private var mWindowColor: Int = 0
    private var mCornerRadius: Float = 0.toFloat()
    private var mContext: Context? = null

    private var mAnimateSpeed: Int = 0

    private var mMaxProgress: Int = 0
    private var mIsAutoDismiss: Boolean = false

    private var mGraceTimeMs: Int = 0
    private var mGraceTimer: Handler? = null
    private var mFinished: Boolean = false

    constructor(context: Context) {
        mContext = context
        mProgressDialog = ProgressDialog(context)
        mDimAmount = 0f

        mWindowColor = ContextCompat.getColor(context, R.color.dialog_color)
        mAnimateSpeed = 1
        mCornerRadius = 10f
        mIsAutoDismiss = true
        mGraceTimeMs = 0
        mFinished = false

        setCustomView()
    }




     fun setCustomView(): MyDialog {
        mProgressDialog!!.setView(DialogView(mContext!!))
        return this
    }

    fun setDimAmount(dimAmount: Float): MyDialog {
        if (dimAmount >= 0 && dimAmount <= 1) {
            mDimAmount = dimAmount
        }
        return this
    }


    fun setSize(width: Int, height: Int): MyDialog {
        mProgressDialog!!.setSize(width, height)
        return this
    }

    fun setWindowColor(color: Int): MyDialog {
        mWindowColor = color
        return this
    }

    fun setBackgroundColor(color: Int): MyDialog {
        mWindowColor = color
        return this
    }

    fun setCornerRadius(radius: Float): MyDialog {
        mCornerRadius = radius
        return this
    }

    fun setAnimationSpeed(scale: Int): MyDialog {
        mAnimateSpeed = scale
        return this
    }

    fun setLabel(label: String?): MyDialog {
        mProgressDialog!!.setLabel(label)
        return this
    }

    fun setLabel(label: String?, color: Int): MyDialog {
        mProgressDialog!!.setLabel(label, color)
        return this
    }

    fun setDetailsLabel(detailsLabel: String?): MyDialog {
        mProgressDialog!!.setDetailsLabel(detailsLabel)
        return this
    }

    fun setDetailsLabel(detailsLabel: String?, color: Int): MyDialog {
        mProgressDialog!!.setDetailsLabel(detailsLabel, color)
        return this
    }

    fun setMaxProgress(maxProgress: Int): MyDialog {
        mMaxProgress = maxProgress
        return this
    }

    fun setProgress(progress: Int) {
        mProgressDialog
        //mProgressDialog.setProgress(progress)
    }

    fun setCustomView(view: View?): MyDialog {
        if (view != null) {
            mProgressDialog!!.setView(view)
        } else {
            throw RuntimeException("Custom view must not be null!")
        }
        return this
    }

    fun setCancellable(isCancellable: Boolean): MyDialog {
        mProgressDialog!!.setCancelable(isCancellable)
        mProgressDialog!!.setOnCancelListener(null)
        return this
    }

    fun setCancellable(listener: DialogInterface.OnCancelListener?): MyDialog {
        mProgressDialog!!.setCancelable(null != listener)
        mProgressDialog!!.setOnCancelListener(listener)
        return this
    }

    fun setAutoDismiss(isAutoDismiss: Boolean): MyDialog {
        mIsAutoDismiss = isAutoDismiss
        return this
    }

    fun setGraceTime(graceTimeMs: Int): MyDialog {
        mGraceTimeMs = graceTimeMs
        return this
    }

    fun show(): MyDialog {
        if (!isShowing()) {
            mFinished = false
            if (mGraceTimeMs == 0) {
                mProgressDialog!!.show()
            } else {
                mGraceTimer = Handler()
                mGraceTimer!!.postDelayed(Runnable {
                    if (mProgressDialog != null && !mFinished) {
                        mProgressDialog!!.show()
                    }
                }, mGraceTimeMs.toLong())
            }
        }
        return this
    }

    fun isShowing(): Boolean {
        return mProgressDialog != null && mProgressDialog!!.isShowing
    }

    fun dismiss() {
        mFinished = true
        if (mContext != null && mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.dismiss()
        }
        if (mGraceTimer != null) {
            mGraceTimer!!.removeCallbacksAndMessages(null)
            mGraceTimer = null
        }
    }

    private inner class ProgressDialog(context: Context) : Dialog(context) {

        //private var mDeterminateView: Determinate? = null
        private var mIndeterminateView: Indeterminate? = null
        private var mView: View? = null
        private var mLabelText: TextView? = null
        private var mDetailsText: TextView? = null
        private var mLabel: String? = null
        private var mDetailsLabel: String? = null
        private var mCustomViewContainer: FrameLayout? = null
        private var mBackgroundLayout: DialogLayout? = null
        private var mWidth: Int = 0
        private var mHeight: Int = 0
        private var mLabelColor = Color.WHITE
        private var mDetailColor = Color.WHITE

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_layout)

            window?.setBackgroundDrawable(ColorDrawable(0))
            window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val layoutParams = window?.attributes
            layoutParams?.dimAmount = mDimAmount
            layoutParams?.gravity = Gravity.CENTER
            window?.attributes = layoutParams

            setCanceledOnTouchOutside(false)

            initViews()
        }

        private fun initViews() {
            mBackgroundLayout = findViewById(R.id.background)
            mBackgroundLayout!!.setBaseColor(mWindowColor)
            mBackgroundLayout!!.setCornerRadius(mCornerRadius)
            if (mWidth != 0) {
                updateBackgroundSize()
            }

            mCustomViewContainer = findViewById(R.id.container)
            addViewToFrame(mView)

            if (mIndeterminateView != null) {
                mIndeterminateView!!.setAnimationSpeed(mAnimateSpeed.toFloat())
            }

            mLabelText = findViewById(R.id.label) as TextView
            setLabel(mLabel, mLabelColor)
            mDetailsText = findViewById(R.id.details_label)
            setDetailsLabel(mDetailsLabel, mDetailColor)
        }

        private fun addViewToFrame(view: View?) {
            if (view == null) return
            val wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT
            val params = ViewGroup.LayoutParams(wrapParam, wrapParam)
            mCustomViewContainer!!.addView(view, params)
        }

        private fun updateBackgroundSize() {
            val params = mBackgroundLayout!!.getLayoutParams()
            params.width = Utils.dpToPixel(mWidth, context)
            params.height = Utils.dpToPixel(mHeight, context)
            mBackgroundLayout!!.setLayoutParams(params)
        }


        fun setView(view: View?) {
            if (view != null) {
                if (view is Indeterminate) {
                    mIndeterminateView = view as Indeterminate?
                }
                mView = view
                if (isShowing) {
                    mCustomViewContainer!!.removeAllViews()
                    addViewToFrame(view)
                }
            }
        }

        fun setLabel(label: String?) {
            mLabel = label
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText!!.text = label
                    mLabelText!!.visibility = View.VISIBLE
                } else {
                    mLabelText!!.visibility = View.GONE
                }
            }
        }

        fun setDetailsLabel(detailsLabel: String?) {
            mDetailsLabel = detailsLabel
            if (mDetailsText != null) {
                if (detailsLabel != null) {
                    mDetailsText!!.text = detailsLabel
                    mDetailsText!!.visibility = View.VISIBLE
                } else {
                    mDetailsText!!.visibility = View.GONE
                }
            }
        }

        fun setLabel(label: String?, color: Int) {
            mLabel = label
            mLabelColor = color
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText!!.text = label
                    mLabelText!!.setTextColor(color)
                    mLabelText!!.visibility = View.VISIBLE
                } else {
                    mLabelText!!.visibility = View.GONE
                }
            }
        }

        fun setDetailsLabel(detailsLabel: String?, color: Int) {
            mDetailsLabel = detailsLabel
            mDetailColor = color
            if (mDetailsText != null) {
                if (detailsLabel != null) {
                    mDetailsText!!.text = detailsLabel
                    mDetailsText!!.setTextColor(color)
                    mDetailsText!!.visibility = View.VISIBLE
                } else {
                    mDetailsText!!.visibility = View.GONE
                }
            }
        }

        fun setSize(width: Int, height: Int) {
            mWidth = width
            mHeight = height
            if (mBackgroundLayout != null) {
                updateBackgroundSize()
            }
        }
    }
}