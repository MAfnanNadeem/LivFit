/*
 *  Created by Sumeet Kumar on 1/26/20 8:39 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/26/20 8:29 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.rxl.impl.CreateCourseAdapter

class ReflexCourseSelectionFragment : BaseFragment() {


    var recyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postponeEnterTransition()
        val transition =
            TransitionInflater.from(this.activity).inflateTransition(R.transition.transition_course)

        sharedElementReturnTransition = androidx.transition.ChangeScroll().apply {
            duration = 750
        }
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition


//        sharedElementEnterTransition =
//            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
//        sharedElementEnterTransition = ChangeBounds().apply {
//            duration = 750
//        }
//        sharedElementReturnTransition = ChangeBounds().apply {
//            duration = 750
//        }

        log("onCreateView")
        val root = inflater.inflate(R.layout.fragment_rxl_select_course, container, false)
        recyclerView = root.findViewById(R.id.recyclerView)
        initCourses()
        return root
    }

    val list = ArrayList<CreateCourseAdapter.Course>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
        //recyclerView?.startViewTransition()
        startPostponedEnterTransition()
        navigate(Navigator.HOME_VIEW, true)
        //initCourses()
        //recyclerView.startViewTransition(view)

    }

    private fun initCourses() {
        list.clear()

        list.add(
            CreateCourseAdapter.Course(
                5,
                "Sequence",
                "The Reaction Lights turns on in sequence.",
                R.drawable.ic_reflex_sequence
            )
        )
        list.add(
            CreateCourseAdapter.Course(
                1,
                "Random",
                "The Rxl light randomly.",
                R.drawable.ic_reflex_random_icon
            )
        )
        list.add(
            CreateCourseAdapter.Course(
                2,
                "All at once",
                "Multiple Rxl light at once.",
                R.drawable.ic_reflex_all_at_once
            )
        )
        list.add(
            CreateCourseAdapter.Course(
                3,
                "Focus",
                "Focus only on your color, not the distracting ones.",
                R.drawable.ic_reflex_focus_only
            )
        )
        list.add(
            CreateCourseAdapter.Course(
                4,
                "Focus Return",
                "Return to home after each random Rxl.",
                R.drawable.ic_reflex_focus_return
            )
        )


        val adapter =
            CreateCourseAdapter(
                list,
                object : ItemClickListener<CreateCourseAdapter.Course> {
                    override fun onItemClicked(
                        item: CreateCourseAdapter.Course?,
                        position: Int
                    ) {
                        lastId = item?.id ?: -1
                        navigate(Navigator.RXL_COURSE_CREATE, item)
                    }

                },
                lastId
            )
        val manager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = manager
        recyclerView?.adapter = adapter
        log("onViewCreated")
    }

    override fun getReenterTransition(): Any? {
        log("getReenterTransition")
        return super.getReenterTransition()
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onPause() {
        super.onPause()
        log("onPause")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    companion object {
        var lastId = -1
    }


}
