package life.mibo.android.ui.home

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.hardware.core.Logger
import life.mibo.views.DashboardItem
import java.util.*


class HomeAdapter(var list: ArrayList<Array<HomeItem>>, val size: Int = 0) :
    RecyclerView.Adapter<HomeAdapter.HomeHolder>() {
    private var listener: ItemClickListener<HomeItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        //val id =
        return HomeHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_hexa_home, parent, false
            )
        )
    }

    fun setListener(listener: ItemClickListener<HomeItem>) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Array<HomeItem>? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
//        val params = LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        params.height = size
//        params.width = size
//        holder.itemView.layoutParams = params
        holder.bind(getItem(position), listener, size, !isOdd(position))
    }

    private fun isOdd(i: Int): Boolean {
        return i and 0x01 != 0
    }


    fun update(newList: ArrayList<Array<HomeItem>>) {
        list.clear()
        list.addAll(newList)
    }

    fun updateWeight(weight: String) {
        var notify = false
        for (l in list) {
            for (i in l) {
                if (i.title.toLowerCase().contains("weight")) {
                    i.headerText = weight
                    notify = true
                    break
                }
            }
        }
        if (notify)
            notifyDataSetChanged()
    }

    fun updateWeather(weight: String?) {
        Logger.e("updateWeather $weight ${weight?.length}")
        if (weight == null)
            return
        var notify = false
        for (l in list) {
            for (i in l) {
                if (i.type == HomeItem.Type.WEATHER) {
                    Logger.e("updateWeather HomeItem.Type.WEATHER found $weight")
                    //i.title = weight.trim() + 0x00B0.toChar()
                    i.headerText = weight.trim() + 0x00B0.toChar()
                    i.updateHeader = true
                    notify = true
                    break
                }
            }
        }
        if (notify)
            notifyDataSetChanged()
        Logger.e("updateWeather notify $notify")
    }

    class HomeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item1: DashboardItem? = itemView.findViewById(R.id.item1)
        var item2: DashboardItem? = itemView.findViewById(R.id.item2)
        var item3: DashboardItem? = itemView.findViewById(R.id.item3)
        //        var view: View? = itemView.findViewById(R.id.itemView)
//        var image: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_1)
//        var image2: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_2)
//        var image3: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_3)
//        var image4: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_4)
//        var image5: HexagonMaskView? = itemView.findViewById(R.id.iv_dashboard_5)
        var data: Array<HomeItem>? = null

        fun bind(
            items: Array<HomeItem>?,
            listener: ItemClickListener<HomeItem>?,
            size: Int,
            odd: Boolean
        ) {
            if (items == null)
                return
            //updateParams(adapterPosition)
            data = items

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.height = size
            params.width = size
            item1!!.layoutParams = params
            item2!!.layoutParams = params
            item3!!.layoutParams = params
            var id = 0
            if (odd) {
               item2?.visibility = View.GONE



                if (items.isNotEmpty()) {
                    id = 0
                    if (items[id].updateHeader) {
                        item1?.updateHeader(items[id].headerText)
                        items[id].updateHeader = false
                    } else {
                        item1?.addViews(
                            items[id].imageRes, items[id].iconRes, items[id].headerText, items[id].title
                        )
                    }
                    item1?.visibility = View.VISIBLE
                }
                if (items.size > 1) {
                    id = 1
                    if (items[id].updateHeader) {
                        item3?.updateHeader(items[id].headerText)
                        items[id].updateHeader = false
                    } else {
                        item3?.addViews(
                            items[id].imageRes, items[id].iconRes, items[id].headerText, items[id].title
                        )
                    }
                    item3?.visibility = View.VISIBLE
                }
            } else {
                if (items.isNotEmpty()) {
                    id = 0
                    if (items[id].updateHeader) {
                        item1?.updateHeader(items[id].headerText)
                        items[id].updateHeader = false
                    } else {
                        item1?.addViews(
                            items[id].imageRes, items[id].iconRes, items[id].headerText, items[id].title
                        )
                    }
                    item1?.visibility = View.VISIBLE
                }
                if (items.size > 1) {
                    id = 1
                    if (items[id].updateHeader) {
                        item2?.updateHeader(items[id].headerText)
                        items[id].updateHeader = false
                    } else {
                        item2?.addViews(
                            items[id].imageRes, items[id].iconRes, items[id].headerText, items[id].title
                        )
                    }
                    item2?.visibility = View.VISIBLE
                }
                if (items.size > 2) {
                    id = 2
                    if (items[id].updateHeader) {
                        item3?.updateHeader(items[id].headerText)
                        items[id].updateHeader = false
                    } else {
                        item3?.addViews(
                            items[id].imageRes, items[id].iconRes, items[id].headerText, items[id].title
                        )
                    }
                    item3?.visibility = View.VISIBLE
                }
                //item2?.visibility = View.VISIBLE
                //item2!!.layoutParams = params
            }

            item1?.setOnClickListener {
                if (items.isNotEmpty())
                    listener?.onItemClicked(items[0], adapterPosition)
            }
            item2?.setOnClickListener {
                if (items.size > 1)
                    listener?.onItemClicked(items[1], adapterPosition)

            }
            item3?.setOnClickListener {
                if (items.isNotEmpty())
                    listener?.onItemClicked(items[items.size - 1], adapterPosition)
            }

//            item1!!.addViews(R.drawable.dashboard_item_bg_1,R.drawable.ic_nfc_black_24dp, "text1", "text2")
//            item2!!.addViews(R.drawable.dashboard_item_bg_3,R.drawable.ic_plus_main, "text3", "text4")
//            item3!!.addViews(R.drawable.dashboard_item_bg_6,R.drawable.ic_rxl_all, "text5", "text6")

//            title?.text = "${items.title}"
//            image!!.setGradient(intArrayOf(Color.RED, Color.GREEN))
//            image2!!.setGradient(intArrayOf(Color.BLUE, Color.DKGRAY))
//            image3?.setGradient(intArrayOf(Color.BLUE, Color.DKGRAY))
//            image4?.setGradient(intArrayOf(Color.BLUE, Color.DKGRAY))
//            image5?.setGradient(intArrayOf(Color.BLUE, Color.DKGRAY))
            // image?.setBackgroundResource(R.drawable.login_bg)
        }

        private fun updateParams(position: Int) {
//            val pos = position + 1
//            val topMargin = getDp(view?.context, -17)
//            val leftMargin = getDp(view?.context, 51) //3 times of 17
//            val param = view?.layoutParams as GridLayoutManager.LayoutParams
//            if (pos < 4) {
//                param.setMargins(0, 0, 0, 0)
//            } else if ((pos + 1) % 5 == 0 || pos % 5 == 0) {
//                param.setMargins(leftMargin, topMargin, 0, 0)
//            } else {
//                param.setMargins(0, topMargin, 0, 0)
//            }
//            view?.layoutParams = param;
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