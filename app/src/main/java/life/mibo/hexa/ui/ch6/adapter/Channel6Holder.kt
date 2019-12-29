package life.mibo.hexa.ui.ch6.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import life.mibo.views.PlayButton

class Channel6Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // var text: TextView? = itemView.findViewById(R.id.test_text)
    var percentChannel: TextView? = itemView.findViewById(R.id.tv_perc_main_channel)
    var percent: TextView? = itemView.findViewById(R.id.tv_perc)
    var view: View? = itemView.findViewById(R.id.view)
    var image: View? = itemView.findViewById(R.id.iv_device)
    var plus: View? = itemView.findViewById(R.id.button_plus)
    var minus: View? = itemView.findViewById(R.id.button_minus)
    var play: PlayButton? = itemView.findViewById(R.id.button_start)
    // var hexa: HexagonImageView? = itemView.findViewById(R.id.test_image_hexa)
    var data: Channel6Model? = null

    fun bind(item: Channel6Model?, listener: Channel6Listener?) {
        if (item == null)
            return
        data = item
        percent?.text = "${item.percentMain} %"
        percentChannel?.text = "${item.percentChannel} %"
        image?.setBackgroundResource(item.image)
        play?.isChecked = item.isPlay

        play?.setOnClickListener {
            listener?.onPlayPauseClicked(item, (it as PlayButton).isPlay)
            //item.isPlay = !item.isPlay
        }

        minus?.setOnClickListener {
            listener?.onMinusClicked(item)
        }

        plus?.setOnClickListener {
            listener?.onPlusClicked(item)
        }
    }
}