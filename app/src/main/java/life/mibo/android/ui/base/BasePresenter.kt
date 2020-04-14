/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.base

abstract class BasePresenter {
    abstract fun onStop()
    abstract fun onCreate(any: Any)

    fun sessionExpired(){

    }
}