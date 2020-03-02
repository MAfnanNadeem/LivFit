/*
 *  Created by Sumeet Kumar on 2/11/20 2:12 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/11/20 12:44 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.adapter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.models.rxl.RxlExercises
import life.mibo.hexa.models.rxl.RxlProgram
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.views.like.AndroidLikeButton
import java.util.*


class ReflexHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageBg: ImageView? = itemView.findViewById(R.id.imageViewBg)
    var image: ImageView? = itemView.findViewById(R.id.imageView)
    var title: TextView? = itemView.findViewById(R.id.tv_title)
    var type: TextView? = itemView.findViewById(R.id.tv_type)
    var users: TextView? = itemView.findViewById(R.id.tv_users)
    var time: TextView? = itemView.findViewById(R.id.tv_action)
    var likeed: AndroidLikeButton? = itemView.findViewById(R.id.likeButton)
    var devices: TextView? = itemView.findViewById(R.id.tv_pods)

    var data: RxlProgram? = null

    fun bind(item: RxlProgram?, listener: ItemClickListener<RxlProgram>?) {
        Logger.e("ReflexHolder $item")
        if (item == null)
            return
        title?.text = item.name
        type?.text = "${item.logicType()}"
        users?.text = "${item.players}"
        devices?.text = "${item.pods}"
        time?.text = "${item.totalDuration}"

        if (item.avatarBase64.isNullOrEmpty()) {
            image?.setImageResource(R.drawable.ic_rxl_pods_icon)
            image?.scaleType = ImageView.ScaleType.FIT_CENTER
           // image?.setColorFilter(Constants.PRIMARY)
           // setGradient(imageBg, image?.drawable)
        } else {
//            val img = item.avatarBase64!!.split(",")
//            Logger.e("ReflexHolder size: ${img.size}")
//            Logger.e("ReflexHolder list: $img")
//            Logger.e("ReflexHolder 0: ${img[0]}")
//            Logger.e("ReflexHolder ImageLoader start")
//            // image?.load(img[0].replace("[", ""))
//            val url = img[0].replace("[", "")
//            item.urlIcon = url
//
            image?.scaleType = ImageView.ScaleType.CENTER_CROP

            val bitmap = life.mibo.hexa.utils.Utils.convertStringImagetoBitmap(item.avatarBase64)
            image?.load(bitmap) {
                Logger.e("ReflexHolder ImageLoader.. $this")
                target {
                    Logger.e("ReflexHolder ImageLoader loaded.. $it")
                    setGradient(imageBg, it)
                    image?.setImageDrawable(it)
                }

            }

        }
        likeed?.setCurrentlyLiked(item.isFavourite)
        likeed?.setOnLikeEventListener {
            listener?.onItemClicked(item, if (it) 1001 else 1002)
        }

//        if (Build.VERSION.SDK_INT >= 21) {
//            image?.transitionName = item.getTransitionIcon()
//            title!!.transitionName = item.getTransitionTitle()
//        }

        //setBg(imageBg, image)
        itemView?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 21) {
//                item.extras =
//                    FragmentNavigator.Extras.Builder()
//                        .addSharedElement(image!!, image!!.transitionName)
//                        .addSharedElement(title!!, title!!.transitionName).build()
            }
            listener?.onItemClicked(item, adapterPosition)
        }
        itemView?.setOnLongClickListener {
            listener?.onItemClicked(item, 2001)
            return@setOnLongClickListener true
        }

    }

    private fun setBg(view: ImageView?, image: ImageView?) {
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

    private fun setGradient(imageBg: ImageView?, drawable: Drawable?) {
        Logger.e("ReflexHolder setGradient $drawable")
        if (drawable == null || imageBg == null)
            return

        Single.fromCallable {
            Logger.e("ReflexHolder setGradient fromCallable")
            var color = Color.GRAY
            try {
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(-0x9e9d9f, -0xececed)
                )
                gd.cornerRadius = 0f
                Logger.e("ReflexHolder setGradient generating......")
                Palette.from(drawableToBitmap(drawable)!!).generate {
                    Logger.e("ReflexHolder setGradient generated palette")
                    it?.let { palette ->
                        color = palette.getDominantColor(
                            ContextCompat.getColor(image?.context!!, R.color.grey)
                        )
                        Logger.e("ReflexHolder setGradient applied dominantColor $color")
                        imageBg.setBackgroundColor(color)
                        //return@let color
                    }
                }
                //Utils.getColor(view?.drawable)
            } catch (e: Exception) {
                Logger.e("ReflexHolder setGradient Exception $e")
                e.printStackTrace()
            }

            return@fromCallable color

        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
            Logger.e("ReflexHolder setGradient doOnSuccess $it")
            imageBg.setBackgroundColor(it)
        }.subscribe()

    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}