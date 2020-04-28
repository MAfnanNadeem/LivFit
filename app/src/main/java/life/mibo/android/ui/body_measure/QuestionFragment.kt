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
                    updateNextButton(true)
                    Calculate.getMeasureData().question(type_, item?.id ?: 1)
                    // Calculate.addValue("Ques_Type$type_", "$selected")

                }

            })
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // updateNextButton(false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        log("setUserVisibleHint $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            updateNextButton(false)
            updateSkipButton(true)
        }
    }


    private fun getQues(type: Int): ArrayList<QuestionsAdapter.Item> {
        if (type == 2)
            return getPhysicalActivity()
        val list = ArrayList<QuestionsAdapter.Item>()
        list.add(
            QuestionsAdapter.Item(
                1,
                "Be healthier",
                "Eat and Train for optimum health",
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                2,
                "Lose weight",
                "Get leaner and increase your stamina",
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                3,
                "Gain weight",
                "Build muscle strength and flexibility",
                false
            )
        )
        return list
    }


    private fun getPhysicalActivity(): ArrayList<QuestionsAdapter.Item> {
        val list = ArrayList<QuestionsAdapter.Item>()

        list.add(
            QuestionsAdapter.Item(
                1,
                "Sedentary lifestyle",
                "Little or no exercise",
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                2,
                "Slightly active lifestyle",
                "Light exercise or sports 1-2 days/week",
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                3,
                "Moderately active lifestyle ",
                "Moderate exercise or sports 2-3 days/week",
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                4,
                "Very active lifestyle",
                "Hard exercise or sports 4-5 days/week",
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                5,
                "Extra active lifestyle",
                "Very hard exercise, physical job or sports 6-7 days/week",
                false
            )
        )
        list.add(
            QuestionsAdapter.Item(
                6,
                "Professional athlete",
                "Moderate exercise or sports 2-3 days/week",
                false
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