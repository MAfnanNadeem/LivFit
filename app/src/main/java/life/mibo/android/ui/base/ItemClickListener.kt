/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.base

public interface ItemClickListener<Item> {
    fun onItemClicked(item: Item?, position: Int)
}