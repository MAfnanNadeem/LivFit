/*
 *  Created by Sumeet Kumar on 3/1/20 11:56 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/1/20 11:56 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.slider

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R

class GiffVideoSlider(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : LinearLayout(context, attrs, defStyleAttr) {

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?) : this(context, null, 0)

    var viewPager: ViewPager2? = null
    var tabs: TabLayout? = null

    init {
        inflate(getContext(), R.layout.rxl_slider_view, this)
        viewPager = findViewById(R.id.viewPager)
        tabs = findViewById(R.id.tabDots)


    }

    fun attach(fragment: FragmentActivity, urls: ArrayList<String>?) {
        Logger.e("GiffVideoSlider attach urls $urls")
        val list = ArrayList<VideoGiffFragment>()

        if (urls.isNullOrEmpty()) {
            list.add(VideoGiffFragment.get(0, ""))
            //return
        } else {
            urls.forEachIndexed { index, s ->
                list.add(VideoGiffFragment.get(index, s))
            }
        }

        Logger.e("GiffVideoSlider attach list size() ${list.size}")
        val adapter = PagerAdapter(fragment, list)
        viewPager?.adapter = adapter
        TabLayoutMediator(tabs!!, viewPager!!) { tab, position ->
            tab.text = ""
        }.attach()
    }

    class PagerAdapter(activity: FragmentActivity, val list: ArrayList<VideoGiffFragment>) :
        FragmentStateAdapter(activity) {
        override fun createFragment(position: Int): Fragment {
            return list[position]
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    class VideoGiffFragment : Fragment() {
        var video: VideoView? = null
        var play: View? = null
        var image: AppCompatImageView? = null

        companion object {
            fun get(type: Int, url: String): VideoGiffFragment {
                Logger.e("VideoGiffFragment companion type $type url $url")
                val frg = VideoGiffFragment()
                val bundle = Bundle()
                bundle.putInt("type_", type)
                bundle.putString("type_url", url)
                frg.arguments = bundle
                return frg
            }
        }

        var type = 0;
        var url = ""
        var isImage = false;
        var isVideo = false

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            super.onCreateView(inflater, container, savedInstanceState)
            val root = inflater.inflate(R.layout.layout_video_giff_view, container, false)

            type = arguments?.getInt("type_") ?: 1
            url = arguments?.getString("type_url") ?: ""

            if (url.isEmpty())
                return root
            val ext = url.substring(url.lastIndexOf("."))
            isImage = ext.contains("gif")
            isVideo = ext.contains("mp4")

            if (isImage) {
                image = root?.findViewById(R.id.imageView)
                image?.visibility = View.VISIBLE
            } else if (isVideo) {
                root?.findViewById<View>(R.id.videoContainer)?.visibility = View.VISIBLE
                video = root?.findViewById(R.id.videoView)
                play = root?.findViewById(R.id.playView)
            }
            return root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            Logger.e("VideoGiffFragment onViewCreated type $type url $url")
            if (url.isEmpty()) {
                image = view?.findViewById(R.id.imageView)
                //image?.visibility = View.VISIBLE
                image?.let {
                    loadGlide("", it)
                }

                return
            }

            if (isImage) {
                image?.let {
                    loadGlide(url, it)
                }
            } else if (isVideo) {
                Logger.e("VideoGiffFragment onViewCreated VIDEO type $type url $url")
                play?.setOnClickListener {
                    video?.setVideoPath(url)
                    play?.visibility = View.INVISIBLE
                    video?.start()
                }

                video?.setOnCompletionListener {
                    play?.visibility = View.VISIBLE
                }

                video?.setOnErrorListener { mp, what, extra ->

                    false
                }
            }
        }

        private fun loadGlide(url: String?, view: ImageView) {
            val request = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(500, 500)
            view?.visibility = View.VISIBLE
            if (url.isNullOrEmpty()) {
                Glide.with(this).asGif().load(R.drawable.rxl_agility_test_1)
                    .apply(request)
                    .into(view)
                return
            }

            val type = url.substring(url.lastIndexOf("."))
            if (type.contains("gif")) {

                Glide.with(this).asGif().load(url)
                    .addListener(object : RequestListener<GifDrawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<GifDrawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            log("Glide onLoadFailed")
                            return true
                        }

                        override fun onResourceReady(
                            resource: GifDrawable?,
                            model: Any?,
                            target: Target<GifDrawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            log("Glide onResourceReady")

                            return false
                        }

                    })
                    //.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .apply(request)
                    .placeholder(R.drawable.dialog_spinner)
                    .into(view)

            } else if (type.contains("mp4")) {
                Glide.with(this).asGif().load(R.drawable.rxl_agility_test_1)
                    .apply(request)
                    .into(view)
            }

        }

        fun log(msg: String) {
            Logger.e("msg $msg")
        }
    }


}