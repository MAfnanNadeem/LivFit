/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.home

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.views.HexagonMaskView
import java.util.*

class HomeAdapter(var list: ArrayList<HomeItem>) :
    RecyclerView.Adapter<HomeAdapter.HomeHolder>() {

    private var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        //val id =
        return HomeHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_home_row, parent, false
            )
        )
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): HomeItem? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }


    fun notify(id: Int) {
//        Observable.fromArray(list).flatMapIterable { x -> x }.subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread()).subscribe {
//                if (it.id == id) {
//                }
//            }

        list?.forEachIndexed { index, item ->
            if (item.id == id) {
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    fun update(newList: ArrayList<HomeItem>) {
        list.clear()
        list.addAll(newList)
    }


    interface Listener {
        fun onItemClick(position: Int, item: HomeItem)
    }

    class HomeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView? = itemView.findViewById(R.id.iv_dashboard_1_text)
        var view: View? = itemView.findViewById(R.id.itemView)
        var image: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_1)
        var image2: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_2)
        var image3: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_3)
        var image4: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_4)
        var image5: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_5)
        var data: HomeItem? = null

        fun bind(item: HomeItem?, listener: Listener?) {
            if (item == null)
                return
            //updateParams(adapterPosition)
            data = item
            title?.text = "${item.title}"
            image!!.setGradient(intArrayOf(Color.RED, Color.GREEN))
            image2!!.setGradient(intArrayOf(Color.BLUE, Color.DKGRAY))
            image3?.setGradient(intArrayOf(Color.BLUE, Color.DKGRAY))
            image4?.setGradient(intArrayOf(Color.BLUE, Color.DKGRAY))
            image5?.setGradient(intArrayOf(Color.BLUE, Color.DKGRAY))
            // image?.setBackgroundResource(R.drawable.login_bg)
        }

        private fun updateParams(position: Int) {
            val pos = position + 1
            val topMargin = getDp(view?.context, -17)
            val leftMargin = getDp(view?.context, 51) //3 times of 17
            val param = view?.layoutParams as GridLayoutManager.LayoutParams
            if (pos < 4) {
                param.setMargins(0, 0, 0, 0)
            } else if ((pos + 1) % 5 == 0 || pos % 5 == 0) {
                param.setMargins(leftMargin, topMargin, 0, 0)
            } else {
                param.setMargins(0, topMargin, 0, 0)
            }
            view?.layoutParams = param;
        }

        fun getRandomColor(): Int {
            val rnd = Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }

        private fun getDp(context: Context?, dp: Int): Int {
            if (context == null)
                return dp
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics
            ).toInt()
        }
    }
}