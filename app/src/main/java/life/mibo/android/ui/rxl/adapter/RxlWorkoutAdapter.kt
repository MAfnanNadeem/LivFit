package life.mibo.android.ui.rxl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.models.rxl.RxlProgram
import life.mibo.android.models.workout.RXL
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.hardware.core.Logger

class RxlWorkoutAdapter(var list: java.util.ArrayList<RXL>?) : RecyclerView.Adapter<ReflexHolder>() {

        //var list: ArrayList<Item>? = null
        private var listener: ItemClickListener<RXL>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReflexHolder {
            return ReflexHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_reflex,
                    parent,
                    false
                )
            )
        }

        fun setListener(listener: ItemClickListener<RXL>) {
            this.listener = listener
        }

        override fun getItemCount(): Int {
            if (list != null)
                return list?.size!!
            return 0
        }

        private fun getItem(position: Int): RXL? {
            return list?.get(position)
        }

        override fun onBindViewHolder(holder: ReflexHolder, position: Int) {
            Logger.e("ReflexAdapter: onBindViewHolder $position")

            holder.bind(getItem(position), listener)
        }


        fun notify(id: Int) {

            list?.forEachIndexed { index, item ->
                if (item.id == id) {
                    notifyItemChanged(index)
                    return@forEachIndexed
                }
            }
        }

        fun delete(program: RxlProgram?) {
            if (program == null)
                return
            var pos = -1
            list?.forEachIndexed { index, item ->
                if (item.id == program.id) {
                    pos = index
                }
            }
            if (pos != -1) {
                list?.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }


        fun update(newList: java.util.ArrayList<RXL>) {
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

        fun filterUpdate(newList: java.util.ArrayList<RXL>) {
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
    }