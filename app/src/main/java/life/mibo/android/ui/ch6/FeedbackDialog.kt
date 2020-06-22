/*
 *  Created by Sumeet Kumar on 5/21/20 11:30 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/8/20 12:08 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.ch6

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.views.rating.BaseRating
import life.mibo.views.rating.SmileRating

class FeedbackDialog(
    c: Context,
    var listener: ItemClickListener<Feedback>? = null,
    var isCancel: Boolean = false,
    var title: String? = "",
    var msg: String? = "",
    var time: Int = 0,
    var calories: String? = ""
) :
    AlertDialog(c) {


    data class Feedback(var id: Int, var rating: Int?, var feedback: String?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rxl_feedback_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        window?.attributes?.windowAnimations = R.style.DialogBounceAnimation;
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //setCancelable(false)

        val save = findViewById<View?>(R.id.btn_save)
        val cancel = findViewById<View?>(R.id.btn_cancel)
        val rating = findViewById<SmileRating?>(R.id.smile_rating)
        val feedback = findViewById<EditText?>(R.id.et_feedback)
        val program = findViewById<TextView?>(R.id.tv_program)
        val message = findViewById<TextView?>(R.id.tv_message)
        val tvTime = findViewById<TextView?>(R.id.tv_name1)
        val value1 = findViewById<TextView?>(R.id.tv_value1)
        val value2 = findViewById<TextView?>(R.id.tv_value2)

        program?.text = title
        message?.text = msg
        value2?.text = calories

        if (time > 60) {
            tvTime?.setText(R.string.total_time)
            value1?.text =String.format("%02d:%02d", time / 60, time % 60)
        } else {
            tvTime?.setText(R.string.total_time_sec)
            value1?.text = "$time"
        }

        save?.setOnClickListener {
            listener?.onItemClicked(
                Feedback(1, rating?.selectedSmile, feedback?.text?.toString()),
                2
            )
            dismiss()
        }

        cancel?.setOnClickListener {
            listener?.onItemClicked(
                Feedback(0, rating?.selectedSmile, feedback?.text?.toString()),
                2
            )
            dismiss()
        }

        if (isCancel) {
            setCancelable(false)
            cancel?.visibility = View.GONE
        }

        rating!!.setSelectedSmile(BaseRating.GOOD, false)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    }

}