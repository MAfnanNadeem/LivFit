/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.base

public interface ItemClickListener<A> {
    fun onItemClicked(item: A?, position: Int)
}