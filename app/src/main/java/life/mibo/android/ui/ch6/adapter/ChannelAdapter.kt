package life.mibo.android.ui.ch6.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.android.R
import life.mibo.android.models.muscle.Muscle
import java.util.*
import kotlin.collections.ArrayList


class ChannelAdapter(var list: ArrayList<Muscle>?, val type: Boolean = false) :
    RecyclerView.Adapter<Channel6Holder>() {

    //var list: ArrayList<Item>? = null
    private var listener: Channel6Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Channel6Holder {
        val id = if (type) R.layout.list_item_channels_grid else R.layout.list_item_channels
        return Channel6Holder(
            LayoutInflater.from(parent.context).inflate(
                id,
                parent,
                false
            )
        )
    }

    fun setListener(listener: Channel6Listener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Muscle? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Channel6Holder, position: Int) {
        holder.bind(getItem(position), listener)
    }


    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
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

    fun updateData(newList: ArrayList<Muscle>) {
//        list?.observe(this, androidx.lifecycle.Observer {
//            it.clear()
//            it.addAll(newList)
//        })
    }

    fun update(newList: ArrayList<Muscle>) {
        if (list == null || list?.isEmpty()!!) {
            list = newList
            notifyDataSetChanged()
            return
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return newList[newItem].id == list!![oldItem].id
            }

            override fun getOldListSize(): Int {
                return list!!.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return newList[newItem].mainValue == list!![oldItem].mainValue && newList[newItem].channelValue == list!![oldItem].channelValue
            }

        })
        list = newList
               result.dispatchUpdatesTo(this)

    }

    fun updateList(items: ArrayList<Muscle>?) {
        Logger.e("updateList")
        if (items == null || items.isEmpty()!!) {
            return
        }
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return items[newItem].id == list!![oldItem].id
            }

            override fun getOldListSize(): Int {
                return list!!.size
            }

            override fun getNewListSize(): Int {
                return items.size
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                Logger.e("updateList ${items[newItem].channelValue} == ${list!![oldItem].channelValue} && ${items[newItem].mainValue} == ${list!![newItem].mainValue}")
                return items[newItem].channelValue == list!![oldItem].channelValue && items[newItem].mainValue == list!![oldItem].mainValue
            }

        })
        list = items
        result.dispatchUpdatesTo(this)
        Logger.e("updateList dispatchUpdatesTo")
    }

    fun getMainLevel(): Int {
        var level = 0
        list?.get(0)?.let {
            level = it.mainValue
        }

        return level
    }

    fun getChannelLevels(): IntArray {

        val array = IntArray(list?.size ?: 6)

        list?.forEachIndexed { i, j ->
            array[i] = j.channelValue
        }

        //return intArrayOf(0, 0, 0, 0, 0, 0)
        return array
    }

    fun getChannels(): List<Int> {

        val array = ArrayList<Int>()

        list?.forEach {
            array.add(it.channelValue)
        }

        //return intArrayOf(0, 0, 0, 0, 0, 0)
        return array
    }

}