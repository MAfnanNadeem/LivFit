/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.base

import android.view.View

abstract class BasePresenter {
    abstract fun onStop()
    abstract fun onCreate(view: View)
}