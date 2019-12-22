package life.mibo.hexa.ui.ch6.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.hexa.ui.devices.adapter.ChannelAdapter
import life.mibo.views.PlayButton
import java.util.*


class ChannelAdapter(var list: List<Item>?, val type: Boolean = false) :
    RecyclerView.Adapter<ChannelAdapter.Holder>() {

    //var list: ArrayList<Item>? = null
    private var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelAdapter.Holder {
        val id = if (type) R.layout.list_item_channels_grid else R.layout.list_item_channels
        return ChannelAdapter.Holder(LayoutInflater.from(parent.context).inflate(id, parent, false))
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Item? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: ChannelAdapter.Holder, position: Int) {
        val item = getItem(position)
        holder.percent?.text = "${item?.percentMuscle}"
        holder.percentChannel?.text = "${item?.percentChannel}"
        holder.image?.setBackgroundResource(item?.image!!)
        holder.play?.isChecked = item?.isPlay!!

        holder.play?.setOnClickListener {
            listener?.onPlayPauseClicked(
                getItem(holder.adapterPosition)!!.id,
                (it as PlayButton).isPlay
            )
        }

        holder.minus?.setOnClickListener {
            listener?.onMinusClicked(getItem(holder.adapterPosition)!!.id)
        }

        holder.plus?.setOnClickListener {
            listener?.onPlusClicked(getItem(holder.adapterPosition)!!.id)
        }

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var text: TextView? = itemView.findViewById(R.id.test_text)
        var percentChannel: TextView? = itemView.findViewById(R.id.tv_perc_main_channel)
        var percent: TextView? = itemView.findViewById(R.id.tv_perc)
        var view: View? = itemView.findViewById(R.id.view)
        var image: View? = itemView.findViewById(R.id.iv_device)
        var plus: View = itemView.findViewById(R.id.button_plus)
        var minus: View = itemView.findViewById(R.id.button_minus)
        var play: PlayButton = itemView.findViewById(R.id.button_start)
        // var hexa: HexagonImageView? = itemView.findViewById(R.id.test_image_hexa)
    }

    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    fun update(newList: ArrayList<Item>) {
        if (list == null) {
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
                return newList[newItem].percentMuscle == list!![oldItem].percentMuscle && newList[newItem].percentChannel == list!![oldItem].percentChannel
            }

        })
        list = newList
        result.dispatchUpdatesTo(this)
    }

    interface Listener {
        fun onClick(id: Int)
        fun onPlusClicked(id: Int)
        fun onMinusClicked(id: Int)
        fun onPlayPauseClicked(id: Int, isPlay: Boolean)
    }

    data class Item(
        val id: Int,
        var image: Int = 0,
        var percentChannel: Int = 0,
        var percentMuscle: Int = 0,
        val title: String = ""
    ) {

        var isPlay = false


        fun incChannelPercent() {
            if (percentChannel < 100)
                percentChannel++
        }

        fun decChannelPercent() {
            if (percentChannel > 1)
                percentChannel--
        }

        fun incMuslePercent() {
            if (percentMuscle < 100)
                percentMuscle++
        }

        fun decMusclePercent() {
            if (percentMuscle > 1)
                percentMuscle--
        }
    }
}