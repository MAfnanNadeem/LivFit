/*
 *  Created by Sumeet Kumar on 3/11/20 11:37 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/11/20 11:35 AM
 *  Mibo Hexa - app
 */

package life.mibo.views.loadingbutton.utils

import android.animation.Animator
import android.view.View

internal fun Animator.disposeAnimator() {
    end()
    removeAllListeners()
    cancel()
}

internal fun View.updateWidth(width: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = width
    this.layoutParams = layoutParams
}

internal fun View.updateHeight(height: Int) {
    val layoutParams = this.layoutParams
    layoutParams.height = height
    this.layoutParams = layoutParams
}
