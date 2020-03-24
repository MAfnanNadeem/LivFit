/*
 *  Created by Sumeet Kumar on 3/1/20 11:56 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/1/20 11:56 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.slider

import android.content.Context
import android.graphics.drawable.Drawable
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
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
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

    fun onDestroy() {

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
        var tutorial: View? = null
        var progress: View? = null
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

        private var data: Any? = null

        // var type = 0;
        var url = ""

        //var isImage = false;
        var isVideo = false

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            super.onCreateView(inflater, container, savedInstanceState)
            val root = inflater.inflate(R.layout.layout_video_giff_view, container, false)

            // type = arguments?.getInt("type_") ?: 1
            url = arguments?.getString("type_url") ?: ""

            if (url.isEmpty())
                return root
            val ext = url.substring(url.lastIndexOf("."))
            //isImage = ext.contains("gif")
            isVideo = ext.contains("mp4")

            if (isVideo) {
                root?.findViewById<View>(R.id.videoContainer)?.visibility = View.VISIBLE
                video = root?.findViewById(R.id.videoView)
            } else {
                image = root?.findViewById(R.id.imageView)
                image?.visibility = View.VISIBLE
            }
            play = root?.findViewById(R.id.playView)
            tutorial = root?.findViewById(R.id.tutorialView)
            progress = root?.findViewById(R.id.progressView)

            return root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            log("onViewCreated url $url")

            play?.setOnClickListener {
                playClicked()
            }
        }

        override fun onPause() {
            video?.pause()
            super.onPause()
        }

        override fun onResume() {
            super.onResume()
            video?.resume()
        }

        override fun onStop() {
            video?.stopPlayback()
            //video?.suspend()
            image?.setImageDrawable(null)
            super.onStop()
        }

        private fun playClicked() {
            log("onViewCreated playClicked url $url")
            if (url.isEmpty()) {
                image = view?.findViewById(R.id.imageView)
                //image?.visibility = View.VISIBLE
                image?.let {
                    loadGlide("", it)
                }

                return
            }


            if (isVideo) {
                log("onViewCreated VIDEO url $url")
                if (url.isEmpty()) {
                    return
                }
                video?.setVideoPath(url)

                video?.setOnPreparedListener {
                    progress?.visibility = View.GONE
                    // it.data
                }

                video?.setOnCompletionListener {
                    updatePlayButton(true)
                }

                video?.setOnErrorListener { mp, what, extra ->

                    false
                }

                video?.start()
                progress?.visibility = View.VISIBLE
            } else {
                image?.let {
                    loadGlide(url, it)
                }
            }

            progress?.visibility = View.VISIBLE
            updatePlayButton(false)
        }

        private fun updatePlayButton(visible: Boolean) {
            if (visible) {
                play?.visibility = View.VISIBLE
                tutorial?.visibility = View.VISIBLE
                progress?.visibility = View.GONE
            } else {
                play?.visibility = View.INVISIBLE
                tutorial?.visibility = View.INVISIBLE

            }
        }

        fun loadImage() {
            if (url.isEmpty()) {
                image = view?.findViewById(R.id.imageView)
                //image?.visibility = View.VISIBLE
                image?.let {
                    loadGlide("", it)
                }

                return
            }

//            if (isImage) {
//                image?.let {
//                    loadGlide(url, it)
//                }
//            }
        }

        fun loadVideo() {
            if (url.isNotEmpty()) {
                video?.setVideoPath(url)
                video?.start()
            }
        }


        private fun loadGlide(url: String?, view: ImageView) {
            val request = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(500, 500)
            view?.visibility = View.VISIBLE
            if (url.isNullOrEmpty()) {
                Glide.with(this).asGif().load(R.drawable.rxl_agility_test_1)
                    .listener(glideListener)
                    .apply(request)
                    .into(view)
                return
            }

            val type = url.substring(url.lastIndexOf("."))
            if (type.contains("gif")) {

                Glide.with(this).asGif().load(url)
                    .listener(glideListener)
                    .apply(request)
                    .placeholder(R.drawable.dialog_spinner)
                    .into(view)
                //.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

            } else  {
                Glide.with(this).load(url)
                    .apply(request)
                    .placeholder(R.drawable.dialog_spinner)
                    .into(view)
            }

        }

        fun log(msg: String) {
            Logger.e("VideoGiffFragment: $msg")
        }


        private class GlideListener<A> : RequestListener<A> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<A>?,
                isFirstResource: Boolean
            ): Boolean {

                return true
            }

            override fun onResourceReady(
                resource: A,
                model: Any?,
                target: Target<A>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                if (resource is GifDrawable) {
                    resource.setLoopCount(1)
                    resource.registerAnimationCallback(object :
                        Animatable2Compat.AnimationCallback() {
                        override fun onAnimationStart(drawable: Drawable?) {
                            super.onAnimationStart(drawable)
                            //log("Glide giff onAnimationStart registerAnimationCallback")
                        }

                        override fun onAnimationEnd(drawable: Drawable?) {
                            super.onAnimationEnd(drawable)
                            // log("Glide giff onAnimationEnd registerAnimationCallback")
                            // updatePlayButton(true)
                        }
                    })
                }
                return false
            }

        }

        private var glideListener = object : RequestListener<GifDrawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<GifDrawable>?,
                isFirstResource: Boolean
            ): Boolean {
                log("Glide onLoadFailed")
                progress?.visibility = View.GONE
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
                //data = resource
                progress?.visibility = View.GONE
                resource?.setLoopCount(1)
                resource?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                    override fun onAnimationStart(drawable: Drawable?) {
                        super.onAnimationStart(drawable)
                        log("Glide giff onAnimationStart registerAnimationCallback")
                    }

                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)
                        log("Glide giff onAnimationEnd registerAnimationCallback")
                        updatePlayButton(true)
                    }
                })
                return false
            }

        }
    }


}