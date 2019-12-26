package life.mibo.hexa.ui.ch6.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import java.util.*


class ChannelAdapterLive(var list: LiveData<ArrayList<Channel6Model>>?, val type: Boolean = false) :
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
            return list?.value?.size!!
        return 0
    }

    private fun getItem(position: Int): Channel6Model? {
        return list?.value?.get(position)
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

        list?.value?.forEachIndexed { index, item ->
            if (item.id == id) {
                notifyItemChanged(index)
                return@forEachIndexed
            }
        }
    }

    fun updateData(newList: ArrayList<Channel6Model>) {
//        list?.observe(this, androidx.lifecycle.Observer {
//            it.clear()
//            it.addAll(newList)
//        })
    }

    fun update(newList: ArrayList<Channel6Model>) {
        if (list?.value == null || list?.value!!.isEmpty()) {
            list = MutableLiveData<ArrayList<Channel6Model>>(newList)
            notifyDataSetChanged()
            return
        }

//        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
//            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
//                return newList[newItem].id == list!![oldItem].id
//            }
//
//            override fun getOldListSize(): Int {
//                return list!!.size
//            }
//
//            override fun getNewListSize(): Int {
//                return newList.size
//            }
//
//            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
//                return newList[newItem].percentMain == list!![oldItem].percentMain && newList[newItem].percentChannel == list!![oldItem].percentChannel
//            }
//
//        })
//        list = newList
        //       result.dispatchUpdatesTo(this)

    }

}