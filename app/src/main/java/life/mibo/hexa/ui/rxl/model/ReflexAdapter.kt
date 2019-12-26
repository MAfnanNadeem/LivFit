package life.mibo.hexa.ui.rxl.model

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import java.util.*


class ReflexAdapter(var list: ArrayList<ReflexModel>?) :
    RecyclerView.Adapter<ReflexHolder>() {

    //var list: ArrayList<Item>? = null
    private var listener: ReflexListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReflexHolder {
        return ReflexHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_reflex,
                parent,
                false
            )
        )
    }

    fun setListener(listener: ReflexListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): ReflexModel? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: ReflexHolder, position: Int) {
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

    fun updateData(newList: ArrayList<ReflexModel>) {
//        list?.observe(this, androidx.lifecycle.Observer {
//            it.clear()
//            it.addAll(newList)
//        })
    }

    fun update(newList: ArrayList<ReflexModel>) {
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
                return true
            }

        })
        list = newList
        result.dispatchUpdatesTo(this)

    }

    fun updateList(items: ArrayList<ReflexModel>?) {
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
                return true
            }

        })
        list = items
        result.dispatchUpdatesTo(this)
        Logger.e("updateList dispatchUpdatesTo")
    }

}