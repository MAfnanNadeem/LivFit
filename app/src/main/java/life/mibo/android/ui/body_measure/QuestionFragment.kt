/*
 *  Created by Sumeet Kumar on 4/14/20 11:12 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 11:12 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_body_questions.*
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.body_measure.adapter.BodyBaseFragment
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.ui.body_measure.adapter.QuestionsAdapter


class QuestionFragment : BodyBaseFragment() {

    companion object {
        fun create(type: Int): QuestionFragment {
            val frg = QuestionFragment()
            val arg = Bundle()
            arg.putInt("ques_type", type)
            frg.arguments = arg
            return frg
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_questions, container, false)
    }

    var selected = -1
    private var type_ = -1
    var adapter: QuestionsAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type_ = arguments?.getInt("ques_type", 1) ?: 1

        if (type_ == 2)
            tv_goal?.setText(R.string.what_is_physical)
        else tv_goal?.setText(R.string.what_is_goal)

    }

    fun setupAdapters() {
        adapter = QuestionsAdapter(type_,
            getQues(type_),
            object :
                ItemClickListener<QuestionsAdapter.Item> {
                override fun onItemClicked(
                    item: QuestionsAdapter.Item?,
                    position: Int
                ) {
                    adapter?.select(item)
                    selected = position
                    if (type_ == 2)
                        updateNextButton(true, "Finish")
                    else updateNextButton(true)
                    Calculate.getMeasureData().question(type_, item?.id ?: 1)
                    // Calculate.addValue("Ques_Type$type_", "$selected")

                }

            })
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_from_bottom);
        recyclerView?.adapter = adapter
    }

    override fun resumed() {
        super.resumed()
    }
    override fun onResume() {
        super.onResume()
        // updateNextButton(false)
        // if (type_ == 2)
        //    updateNextButton(true, "Finish")
        //else
        updateNextButton(false)
        updateSkipButton(true)
        setupAdapters()
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        log("setUserVisibleHint $isVisibleToUser")
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//            updateNextButton(false)
//            updateSkipButton(true)
//            setupAdapters()
//        }
//    }


    private fun getQues(type: Int): ArrayList<QuestionsAdapter.Item> {
        if (type == 2)
            return getPhysicalActivity()
        val list = ArrayList<QuestionsAdapter.Item>()
        list.add(
            QuestionsAdapter.Item(
                1,
                getString(R.string.goal_que_1_title),
                getString(R.string.goal_que_1_desc),
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                2,
                getString(R.string.goal_que_2_title),
                getString(R.string.goal_que_2_desc),
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                3,
                getString(R.string.goal_que_3_title),
                getString(R.string.goal_que_3_desc),
                false
            )
        )
        return list
    }


    private fun getPhysicalActivity(): ArrayList<QuestionsAdapter.Item> {
        val list = ArrayList<QuestionsAdapter.Item>()
        val male = Calculate.getMeasureData().isMale()

        list.add(
            QuestionsAdapter.Item(
                1,
                getString(R.string.que_activity_1_title),
                getString(R.string.que_activity_1_desc),
                false, male
            )
        )
        list.add(
            QuestionsAdapter.Item(
                2,
                getString(R.string.que_activity_2_title),
                getString(R.string.que_activity_2_desc),
                false, male
            )
        )
        list.add(
            QuestionsAdapter.Item(
                3,
                getString(R.string.que_activity_3_title),
                getString(R.string.que_activity_3_desc),
                false, male
            )
        )
        list.add(
            QuestionsAdapter.Item(
                4,
                getString(R.string.que_activity_4_title),
                getString(R.string.que_activity_4_desc),
                false, male
            )
        )
        list.add(
            QuestionsAdapter.Item(
                5,
                getString(R.string.que_activity_5_title),
                getString(R.string.que_activity_5_desc),
                false, male
            )
        )
        list.add(
            QuestionsAdapter.Item(
                6,
                getString(R.string.que_activity_6_title),
                getString(R.string.que_activity_6_desc),
                false, male
            )
        )
        return list
    }

    fun getFactor(): Double {

        if (type_ == 2) {

        }
        return 1.0
    }
}