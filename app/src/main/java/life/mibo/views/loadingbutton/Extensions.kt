/*
 *  Created by Sumeet Kumar on 2/20/20 10:19 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/20/20 10:16 AM
 *  Mibo Hexa - app
 */

package life.mibo.views.loadingbutton

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
