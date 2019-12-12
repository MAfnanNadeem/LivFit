package life.mibo.hexa.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.github.siyamed.shapeimageview.HexagonImageView
import life.mibo.hexa.view.recycler.VerticallyAdaptableHexagonImageView
import java.util.*


class ScanDeviceAdapter(var list: List<ScanItem>, val type: Int = 0) :
    RecyclerView.Adapter<ScanDeviceAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.simple_list_item, parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): ScanItem? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.text1?.text = item?.text1
        holder.text2?.text = item?.text2

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text1: TextView? = itemView.findViewById(R.id.text1)
        var text2: TextView? = itemView.findViewById(R.id.text2)
        var view: View? = itemView.findViewById(R.id.itemView)
    }

    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    data class ScanItem(val text1: String?, val text2: String? = "")
}