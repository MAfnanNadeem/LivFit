/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.home

interface HomeObserver {
    fun onDataReceived(list: ArrayList<HomeItem>)
    fun onItemClicked(item: HomeItem?)
}