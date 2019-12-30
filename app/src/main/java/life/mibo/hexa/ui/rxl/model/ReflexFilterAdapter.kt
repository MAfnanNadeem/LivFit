package life.mibo.hexa.ui.rxl.model

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hexa.R
import java.util.*


class ReflexFilterAdapter(var list: ArrayList<ReflexFilterModel>?, val type: Int = 0) :
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

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.type
        //return super.getItemViewType(position)
    }

    private fun getItem(position: Int): ReflexFilterModel? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), listener, type)
    }


    // public classes
    interface Listener {
        fun onClick(data: ReflexFilterModel?)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView? = itemView.findViewById(R.id.text_filter)
        var view: View? = itemView.findViewById(R.id.item_view)
        var heart: View? = itemView.findViewById(R.id.image_filter)
        var arrow: View? = itemView.findViewById(R.id.image_arrow)
        var switch_: View? = itemView.findViewById(R.id.switch_filter)
        var data: ReflexModel? = null

        fun bind(item: ReflexFilterModel?, listener: Listener?, type: Int = 0) {
            if (item == null)
                return

            when (item.type) {
                2 -> {
                    heart?.visibility = View.VISIBLE
                    switch_?.visibility = View.GONE
                    view?.visibility = View.GONE
                }

                3 -> {
                    heart?.visibility = View.GONE
                    switch_?.visibility = View.VISIBLE
                    view?.visibility = View.GONE

                }
                else -> {
                    heart?.visibility = View.GONE
                    switch_?.visibility = View.GONE
                    view?.visibility = View.VISIBLE
                    text?.text = item.title
                    if (type == 3) {
                        arrow?.visibility = View.GONE
                        if (item.isSelected) {
                            view?.setBackgroundResource(R.drawable.item_rxl_filters_selected2)
                            text?.setTextColor(Color.DKGRAY)
                        } else {
                            view?.setBackgroundResource(R.drawable.item_rxl_filters)
                            text?.setTextColor(Color.WHITE)
                        }
                    } else {
                        if (item.isSelected)
                            view?.setBackgroundResource(R.drawable.item_rxl_filters_selected)
                        else
                            view?.setBackgroundResource(R.drawable.item_rxl_filters)
                    }
                    view?.setOnClickListener {
                        item.isSelected = !item.isSelected
                        listener?.onClick(item)
                        updateView(item, type)

                    }

                }
            }
        }

        fun updateView(item: ReflexFilterModel, type: Int) {
            if (type == 3) {
                arrow?.visibility = View.GONE
                if (item.isSelected) {
                    view?.setBackgroundResource(R.drawable.item_rxl_filters_selected2)
                    text?.setTextColor(Color.DKGRAY)
                } else {
                    view?.setBackgroundResource(R.drawable.item_rxl_filters)
                    text?.setTextColor(Color.WHITE)
                }
            } else {
                if (item.isSelected)
                    view?.setBackgroundResource(R.drawable.item_rxl_filters_selected)
                else
                    view?.setBackgroundResource(R.drawable.item_rxl_filters)
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

        override fun equals(other: Any?): Boolean {
            if (other != null && other is ReflexFilterModel) {
                return other.id == id
            }
            return false
        }

        override fun hashCode(): Int {
            return id
        }
    }
}