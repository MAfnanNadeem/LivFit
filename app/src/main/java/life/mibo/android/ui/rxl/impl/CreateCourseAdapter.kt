/*
 *  Created by Sumeet Kumar on 1/27/20 10:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/27/20 9:27 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl.impl

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.RecyclerView
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import java.io.Serializable

class CreateCourseAdapter(
    val list: ArrayList<Course>,
    var listener: ItemClickListener<Course>? = null, var transationId: Int = -1
) :
    RecyclerView.Adapter<CreateCourseAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_rxl_create_course, parent, false
            )
        )
    }

//    override fun getItemId(position: Int): Long {
//        return getItem(position)?.icon?.toLong() ?: 0L
//    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getItem(position: Int): Course? {
        return list[position]
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position], listener, transationId)
        holder.setIsRecyclable(false)


    }


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = itemView.findViewById(R.id.iv_image)
        var title: TextView? = itemView.findViewById(R.id.tv_title)
        var desc: TextView? = itemView.findViewById(R.id.tv_desc)
        var data: Course? = null

        fun bind(item: Course?, listener: ItemClickListener<Course>?, transationId: Int) {
            if (item == null)
                return
            data = item
            title?.text = "${item.title}"
            desc?.text = "${item.desc}"
            image?.setImageResource(item.icon)
//            ViewCompat.setTransitionName(image!!, "image_$adapterPosition")
//            ViewCompat.setTransitionName(title!!, "title_$adapterPosition")
            //image?.transitionName = "course_icon${item.id}"
            //title?.transitionName = "course_text${item.id}"
            if (Build.VERSION.SDK_INT >= 21) {
                image?.transitionName = item.getTransitionIcon()
                title!!.transitionName = item.getTransitionTitle()
                //image?.transitionName = String.format("icon_%02d", item.id);
                //title!!.transitionName = String.format("title_%02d", item.id);
                //Logger.e("image!!.transitionName - " + String.format("iicon_%02d", item.id))
               // Logger.e("image!!.transitionName " + image?.transitionName)
                //titl?e.setTransitionName(transitionName);
            }
            //ViewCompat.setTransitionName(image!!, "course_icon")
            //ViewCompat.setTransitionName(title!!, "course_title")
//            if (item.id == transationId) {
//                //  Logger.e("transationId found - $item")
//                // image?.transitionName = "course_icon"
//                // title?.transitionName = "course_title"
//            }

            itemView?.setOnClickListener {
                if (Build.VERSION.SDK_INT >= 21) {
                    //image?.transitionName = String.format("iicon_%02d", item.id);
                    // title!!.transitionName = String.format("ttitle_%02d", item.id);
                   // image?.transitionName = "course_icon"
                    //title?.transitionName = "course_title"
                    item.extras =
                        FragmentNavigator.Extras.Builder()
                            .addSharedElement(image!!, image!!.transitionName)
                            .addSharedElement(title!!, title!!.transitionName).build()
                }
                //image?.transitionGroup

                //FragmentNavigatorExtras()
                item.position = adapterPosition
                listener?.onItemClicked(item, adapterPosition)
            }
        }
    }

    class Course(
        var id: Int,
        var title: String,
        var desc: String, @DrawableRes var icon: Int,
        var type: Int = 0
    ) : Serializable {
        var extras: FragmentNavigator.Extras? = null
        var position = 0

        fun getTransitionTitle(): String {
            return String.format("ttitle_%02d", id)
        }

        fun getTransitionIcon(): String {
            return String.format("iicon_%02d", id)
        }

    }
}