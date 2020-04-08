/*
 *  Created by Sumeet Kumar on 3/10/20 9:41 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/10/20 9:41 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.impl

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.rxl.adapter.ScoreAdapter

class ScoreDialog(
    c: Context,
    var programName: String?,
    val list: ArrayList<ScoreAdapter.ScoreItem>
) : AlertDialog(c) {

    private var recyclerView: RecyclerView? = null
    private var dialogAdapter: ScoreAdapter? = null
    var listener: ItemClickListener<ScoreAdapter.ScoreItem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rxl_score_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        window?.attributes?.windowAnimations = R.style.DialogBounceAnimation;

        recyclerView = findViewById(R.id.recyclerView)
        val close: View? = findViewById(R.id.iv_cancel)
        val program: TextView? = findViewById(R.id.programName)
        //val completed: ImageView? = findViewById(R.id.iv_completed)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        dialogAdapter = ScoreAdapter(list)
        program?.text = programName
        setCancelable(false)
        val height = context.resources.displayMetrics.heightPixels
//            if (dialogHeight > height.times(0.7)) {
//                window?.setLayout(dialogWidth, height.times(0.7).toInt())
//            }

        Logger.e("height $height")
        recyclerView?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                Logger.e("addOnLayoutChangeListener ${recyclerView?.height}")
                if (recyclerView?.height!! > height.times(0.8).toInt()) {
                    recyclerView?.layoutParams?.height = height.times(0.8).toInt()
                    Logger.e("addOnLayoutChangeListener changed.........")
                }
                recyclerView?.removeOnLayoutChangeListener(this)
            }

        })

        recyclerView?.adapter = dialogAdapter

        if (recyclerView?.layoutParams?.height!! > height.times(0.6).toInt())
            recyclerView?.layoutParams?.height = height.times(0.6).toInt()
        close?.setOnClickListener {
            dismiss()
            listener?.onItemClicked(null, 0)
        }
    }

}