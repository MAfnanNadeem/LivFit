package life.mibo.hexa.ui.rxl.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import java.util.*


class ReflexFilterAdapter(var list: ArrayList<ReflexFilterModel>?) :
    RecyclerView.Adapter<ReflexFilterAdapter.Holder>() {

    private var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_reflex_filters,
                parent,
                false
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

    private fun getItem(position: Int): ReflexFilterModel? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), listener)
    }


    // public classes
    interface Listener {
        fun onClick(data: ReflexFilterModel?)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.text_filter)
        var view: View? = itemView.findViewById(R.id.item_view)
        var data: ReflexModel? = null

        fun bind(item: ReflexFilterModel?, listener: Listener?) {
            if (item == null)
                return
            text?.text = item.title
            if (item.isSelected)
                view?.setBackgroundResource(R.drawable.item_rxl_filters_selected)
            else
                view?.setBackgroundResource(R.drawable.item_rxl_filters)
            view?.setOnClickListener {
                item.isSelected = !item.isSelected
                listener?.onClick(item)
            }
        }
    }

    data class ReflexFilterModel(
        val id: Int,
        val title: String,
        val image: Int = 0,
        val type: Int = 0
    ) {
        var isSelected = false
    }
}