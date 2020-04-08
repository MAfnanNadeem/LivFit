package life.mibo.views.backdrop

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import life.mibo.hexa.R

/**
 * Created by Sumeet Gehi 29/12/2019
 * Ref Google Backdrop Example
 * */



class BackdropBehavior : CoordinatorLayout.Behavior<View> {

    enum class DropState {
        OPEN,
        CLOSE
    }

    interface OnDropListener {

        fun onDrop(dropState: DropState, fromUser: Boolean)
    }

    companion object {
        private const val DEFAULT_DURATION = 300L
        private const val WITHOUT_DURATION = 0L
        private val DEFAULT_DROP_STATE = DropState.CLOSE

        private const val ARG_DROP_STATE = "mibo_rxl_filters"
    }

    private var toolbarId: Int? = null
    private var backLayoutId: Int? = null

    private var toolbar: Toolbar? = null
    private var backLayout: ViewGroup? = null
    private var frontLayout: View? = null

    private var closedIconId: Int = R.drawable.ic_filter_menu
    private var openedIconRes: Int = R.drawable.ic_cancel_circle

    private var dropState: DropState = DEFAULT_DROP_STATE

    private var needToInitializing = true

    private var dropListeners: OnDropListener? = null

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: View): Parcelable {
        return Bundle().apply {
            putSerializable(ARG_DROP_STATE, dropState)
        }
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: View, state: Parcelable) {
        super.onRestoreInstanceState(parent, child, state)

        dropState =
            (state as? Bundle)?.getSerializable(ARG_DROP_STATE) as? DropState ?: DEFAULT_DROP_STATE
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (toolbarId == null && backLayoutId == null) return false

        return when (dependency.id) {
            toolbarId -> true
            backLayoutId -> true
            else -> false
        }
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {

        this.frontLayout = child as? ViewGroup
            ?: throw IllegalArgumentException("BackLayout must extend a ViewGroup")

        when (dependency.id) {
            toolbarId -> toolbar = dependency as? Toolbar
                ?: throw IllegalArgumentException("toolbarId doesn't match Toolbar")

            backLayoutId -> {
                backLayout = dependency as? ViewGroup
                    ?: throw IllegalArgumentException("backLayoutId doesn't match back Layout")

                // TODO (next release): remove this conditional
                if (toolbarId == null && toolbar == null) {
                    toolbar = findToolbar(backLayout!!)
                        ?: throw IllegalArgumentException("AppBarLayout mast contain a Toolbar!")
                }
            }
        }

        if (toolbar != null && frontLayout != null && backLayout != null && needToInitializing) {
            initViews(parent, frontLayout!!, toolbar!!, backLayout!!)
        }

        return super.onDependentViewChanged(parent, child, dependency)
    }

    fun findToolbar(viewGroup: ViewGroup): Toolbar? {
        if (toolbar != null)
            return toolbar
        for (chileId in 0..viewGroup.childCount) {
            val childView = viewGroup.getChildAt(chileId)
            if (childView is Toolbar) {
                return childView
            }
        }

        return null
    }

    fun setOpenedIcon(@IdRes iconRes: Int) {
        this.openedIconRes = iconRes
    }

    fun setClosedIcon(@IdRes iconRes: Int) {
        this.closedIconId = iconRes
    }

    /**
     * Attach back layout to Backdrop.
     * BackDropLayout must contain a [Toolbar]
     */
    fun attachBackLayout(@IdRes appBarLayoutId: Int) {
        this.backLayoutId = appBarLayoutId
    }

    fun attachToolbar(@IdRes toolbarId: Int) {
        this.toolbarId = toolbarId
    }

    fun attachToolbar(t: Toolbar) {
        this.toolbar = t
    }


    fun addOnDropListener(listener: OnDropListener) {
        dropListeners = listener
    }

    private fun initViews(
        parent: CoordinatorLayout,
        frontLayout: View,
        toolbar: Toolbar,
        backLayout: View
    ) {

//        if (toolbarId != null) {
//            backLayout.y = toolbar.y + toolbar.height
//        }
        //ackLayout.y = toolbar.y + toolbar.height
        frontLayout.layoutParams.height =
            parent.height - calculateTopPosition(backLayout, toolbar).toInt()
        updateState(frontLayout, toolbar, backLayout, false)

//        with(toolbar) {
//            setNavigationOnClickListener {
//                dropState = when (dropState) {
//                    DropState.CLOSE -> DropState.OPEN
//                    DropState.OPEN -> DropState.CLOSE
//                }
//                updateState(frontLayout, toolbar, backLayout)
//                notifyListeners(true)
//            }
//        }

        needToInitializing = false
    }

    private fun updateState(
        frontLayout: View,
        toolbar: Toolbar,
        backContainer: View,
        withAnimation: Boolean = true
    ) {
        when (dropState) {
            DropState.CLOSE -> {
                close(frontLayout, backContainer, toolbar, withAnimation)
                //toolbar.setNavigationIcon(closedIconId)
            }
            DropState.OPEN -> {
                open(frontLayout, backContainer, withAnimation)
                //toolbar.setNavigationIcon(openedIconRes)
            }
        }
    }

    fun toggle() {
        if (dropState == DropState.OPEN)
            close(true)
        else open(true)
    }

    fun open(withAnimation: Boolean = true): Boolean = if (dropState == DropState.OPEN) {
        false
    } else {
        dropState = DropState.OPEN
        if (backLayout != null && toolbar != null && frontLayout != null) {
            updateState(frontLayout!!, toolbar!!, backLayout!!, withAnimation)
        } else {
           //null
        }
        notifyListeners(false)
        true
    }

    fun close(withAnimation: Boolean = true): Boolean = if (dropState == DropState.CLOSE) {
        false
    } else {
        dropState = DropState.CLOSE
        if (backLayout != null && toolbar != null && frontLayout != null) {
            updateState(frontLayout!!, toolbar!!, backLayout!!, withAnimation)
        } else {
           // null
        }
        notifyListeners(false)
        true
    }

    private fun close(
        frontLayout: View,
        backLayout: View,
        toolbar: Toolbar,
        withAnimation: Boolean = true
    ) {
        val position = calculateTopPosition(backLayout, toolbar)
        val duration = if (withAnimation) DEFAULT_DURATION else WITHOUT_DURATION

        frontLayout.animate().y(position).setDuration(duration).start()
    }

    private fun open(
        frontLayout: View,
        backLayout: View,
        withAnimation: Boolean = true
    ) {
        val position = calculateBottomPosition(backLayout)
        val duration = if (withAnimation) DEFAULT_DURATION else WITHOUT_DURATION

        frontLayout.animate().y(position).setDuration(duration).start()
    }

    private fun calculateTopPosition(backLayout: View, toolbar: Toolbar): Float {
        return if (toolbar != null) {
            backLayout.y
        } else {
            (backLayout.y + toolbar.y + toolbar.height)
        }
    }

    private fun calculateBottomPosition(backLayout: View): Float {
        return backLayout.y + backLayout.height
    }

    private fun notifyListeners(fromUser: Boolean) {
        dropListeners?.onDrop(dropState, fromUser)
    }

    fun dispose() {
        toolbar = null
        backLayout = null
        frontLayout = null
        dropListeners = null
    }
}