/*
 *  Created by Sumeet Kumar on 1/26/20 8:55 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/26/20 8:29 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.create

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionInflater
import kotlinx.android.synthetic.main.fragment_rxl_create.*
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.rxl.impl.CourseCreateImpl
import life.mibo.hexa.ui.rxl.impl.CreateCourseAdapter
import life.mibo.hexa.ui.rxl.impl.ReflexDialog
import life.mibo.hexa.utils.Utils

class ReflexCourseCreateFragment : BaseFragment(), CourseCreateImpl.Listener {

    companion object {
        const val DATA = "course_data"
    }

    lateinit var viewImpl: CourseCreateImpl
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //val transition = TransitionInflater.from(this.activity).inflateTransition(android.R.transition.move)
        val transition =
            TransitionInflater.from(this.activity).inflateTransition(R.transition.transition_course)

        sharedElementEnterTransition = androidx.transition.ChangeScroll().apply {
            duration = 750
        }
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = null
        //androidx.transition.ChangeImageTransform
//        sharedElementEnterTransition = ChangeBounds().apply {
//            duration = 750
//            enterTransition = transition
//            exitTransition = transition
//        }

//        sharedElementReturnTransition = ChangeBounds().apply {
//            duration = 750
//        }
        postponeEnterTransition()
        val root = inflater.inflate(R.layout.fragment_rxl_create, container, false)
        if (Build.VERSION.SDK_INT >= 21) {
            val item = arguments?.getSerializable(DATA)
            if (item != null && item is CreateCourseAdapter.Course) {
                root.findViewById<View?>(R.id.iv_icon)?.transitionName = item.getTransitionIcon()
                root.findViewById<View?>(R.id.tv_title)?.transitionName = item.getTransitionTitle()
            }
        }

        startPostponedEnterTransition()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arguments?.getSerializable(DATA)
        if (data is CreateCourseAdapter.Course) {
            tv_title?.text = data?.title
            iv_icon?.setImageResource(data.icon)
//            ViewCompat.setTransitionName(tv_title!!, data?.transitionTitle)
//            ViewCompat.setTransitionName(tv_title!!, data?.transitionTitle)
//            ViewCompat.setTransitionName(title!!, "title_$adapterPosition")
        }

        viewImpl = CourseCreateImpl(this)

        tv_select_stations?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.STATIONS)
        }
        tv_select_cycles?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.CYCLES)
        }
        tv_select_pods?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.PODS)
        }
        tv_select_lights?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.LIGHT_LOGIC)
        }
        tv_select_players?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.PLAYERS)
        }
        tv_select_delay?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.DELAY)
        }
        tv_select_time?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.DURATION)
        }
        viewImpl.listener = this

        initTitles()
    }

    private fun initTitles() {
        tv_select_stations?.text = viewImpl.getTitle(CourseCreateImpl.Type.STATIONS)
        tv_select_cycles?.text = viewImpl.getTitle(CourseCreateImpl.Type.CYCLES)
        tv_select_pods?.text = viewImpl.getTitle(CourseCreateImpl.Type.PODS)
        tv_select_lights?.text = viewImpl.getTitle(CourseCreateImpl.Type.LIGHT_LOGIC)
        tv_select_delay?.text = viewImpl.getTitle(CourseCreateImpl.Type.DELAY)
        tv_select_time?.text = viewImpl.getTitle(CourseCreateImpl.Type.DURATION)
        tv_select_players?.text = viewImpl.getTitle(CourseCreateImpl.Type.PLAYERS)

        radio_group?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_start_sensor -> {
                    //fluidSlider?.visibility = View.VISIBLE
                    Utils.slideUp(fluidSlider)
                    nestedScrollView?.post {
                        nestedScrollView?.fullScroll(View.FOCUS_DOWN)
                    }
                }
                R.id.radio_start_now -> {
                    Utils.slideDown(fluidSlider)
                    //fluidSlider?.visibility = View.GONE
                }
                R.id.radio_start_tap -> {
                    //fluidSlider?.visibility = View.GONE
                    Utils.slideDown(fluidSlider)
                }

            }

        }
    }

    override fun onDialogItemSelected(item: ReflexDialog.Item, type: Int) {
        dialogItemSelected(item.title, type)
    }

    private fun dialogItemSelected(title: String, type: Int) {
        when (type) {
            CourseCreateImpl.Type.STATIONS.type -> {
                tv_select_stations?.text = title
            }
            CourseCreateImpl.Type.CYCLES.type -> {
                tv_select_cycles?.text = title
            }
            CourseCreateImpl.Type.PODS.type -> {
                tv_select_pods?.text = title
            }
            CourseCreateImpl.Type.LIGHT_LOGIC.type -> {
                tv_select_lights?.text = title
            }
            CourseCreateImpl.Type.PLAYERS.type -> {
                tv_select_players?.text = title
            }
            CourseCreateImpl.Type.DELAY.type -> {
                tv_select_delay?.text = title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.DURATION.type -> {
                tv_select_time?.text = title?.replace("seconds", "sec")
            }
        }
    }

    fun type(type: CourseCreateImpl.Type) {
        when (type) {
            CourseCreateImpl.Type.STATIONS -> {

            }
            CourseCreateImpl.Type.CYCLES -> {

            }
            CourseCreateImpl.Type.PODS -> {

            }
            CourseCreateImpl.Type.LIGHT_LOGIC -> {

            }
            CourseCreateImpl.Type.PLAYERS -> {

            }
            CourseCreateImpl.Type.DELAY -> {

            }
            CourseCreateImpl.Type.DURATION -> {

            }
        }
    }

    fun getType(type: CourseCreateImpl.Type) {
        when (type) {
            CourseCreateImpl.Type.STATIONS -> {

            }
            CourseCreateImpl.Type.CYCLES -> {

            }
            CourseCreateImpl.Type.PODS -> {

            }
            CourseCreateImpl.Type.LIGHT_LOGIC -> {

            }
            CourseCreateImpl.Type.PLAYERS -> {

            }
            CourseCreateImpl.Type.DELAY -> {

            }
            CourseCreateImpl.Type.DURATION -> {

            }
        }
    }

    override fun onStop() {
        super.onStop()
    }
}
