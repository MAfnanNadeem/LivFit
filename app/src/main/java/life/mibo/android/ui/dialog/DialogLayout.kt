package life.mibo.android.ui.dialog

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import life.mibo.android.R
import life.mibo.android.utils.Utils


class DialogLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {
    private var mCornerRadius: Float = 0.toFloat()
    private var mBackgroundColor: Int = 0

    init {
        init()
    }

    constructor(context: Context) : this(context, null, 0)

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        // init()
    }

    private fun init() {
        val color = context.resources.getColor(R.color.dialog_color)
        initBackground(color, mCornerRadius)
    }

    private fun initBackground(color: Int, cornerRadius: Float) {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setColor(color)
        drawable.cornerRadius = cornerRadius
        background = drawable
    }

    fun setCornerRadius(radius: Float) {
        mCornerRadius = Utils.dpToPixel(radius, context)
        initBackground(mBackgroundColor, mCornerRadius)
    }

    fun setBaseColor(color: Int) {
        mBackgroundColor = color
        initBackground(mBackgroundColor, mCornerRadius)
    }
}