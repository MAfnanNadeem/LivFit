package life.mibo.hexa.ui.rxl.impl.model

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.ItemClickListener


class ReflexHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageBg: ImageView? = itemView.findViewById(R.id.imageViewBg)
    var image: ImageView? = itemView.findViewById(R.id.imageView)
    // var text: TextView? = itemView.findViewById(R.id.test_text)
    //var percentChannel: TextView? = itemView.findViewById(R.id.tv_perc_main_channel)
    var data: ReflexModel? = null

    fun bind(item: ReflexModel?, listener: ItemClickListener<ReflexModel>?) {
        setBg(imageBg, image)
        itemView?.setOnClickListener {
            listener?.onItemClicked(item, adapterPosition)
        }

    }

    fun setBg(view: ImageView?, image: ImageView?) {
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(-0x9e9d9f, -0xececed)
        )
        gd.cornerRadius = 0f
        Palette.from((image?.drawable as BitmapDrawable).bitmap).generate {
            it?.let { palette ->
                val dominantColor = palette.getDominantColor(
                    ContextCompat.getColor(image.context!!, R.color.grey)
                )
                Logger.e("ReflexHolder dominantColor $dominantColor")
                view?.setBackgroundColor(dominantColor)
            }
        }
        //Utils.getColor(view?.drawable)
    }
}