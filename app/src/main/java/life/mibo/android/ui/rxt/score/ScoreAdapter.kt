package life.mibo.android.ui.rxt.score

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.utils.Utils

class ScoreAdapter(var list: ArrayList<ScoreItem>) : RecyclerView.Adapter<ScoreAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_rxl_score, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun getItem(position: Int): ScoreItem {
        return list[position]
    }

    fun update(items: ArrayList<ScoreItem>) {
        if (items.isNotEmpty()) {
            list.clear()
            list.addAll(items)
            notifyDataSetChanged()
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        //var view1: View? = view.findViewById(R.id.view1)
        //var view2: View? = view.findViewById(R.id.view2)


        // View2
        var imageViewBg: ImageView? = view.findViewById(R.id.imageViewBg)

        //var img_user_bg: ImageView = view.findViewById(R.id.img_user_bg)
        //var img_user: ImageView = view.findViewById(R.id.img_user)
        //var image_hits: ImageView = view.findViewById(R.id.image_hits)
        var playerName: TextView? = view.findViewById(R.id.tv_player_name)
        var hits: TextView? = view.findViewById(R.id.tv_hits_count)
        var missed: TextView? = view.findViewById(R.id.tv_missed_count)


        fun bind(item: ScoreItem?) {
            if (item == null)
                return
            // data = item
            if (item.color != 0)
                imageViewBg?.background = getDrawable(
                        item.color, Color.DKGRAY,
                        Utils.dpToPixel(2, imageViewBg?.context),
                        Utils.dpToPixel(12, imageViewBg?.context)
                )
            playerName?.text = item?.name
            hits?.text = item.hits
            missed?.text = item.missed

        }

        fun getDrawable(
                color: Int,
                strokeColor: Int,
                strokeWidth: Int,
                corner: Int
        ): GradientDrawable {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(color)
            gradientDrawable.cornerRadius = corner.toFloat()
            gradientDrawable.setStroke(strokeWidth, strokeColor)
            return gradientDrawable
        }

    }
}