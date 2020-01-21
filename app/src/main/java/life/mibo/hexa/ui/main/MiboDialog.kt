/*
 *  Created by Sumeet Kumar on 1/19/20 3:44 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/16/20 3:14 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import life.mibo.hexa.R
import life.mibo.hexa.utils.Utils

class MiboDialog(
    c: Context,
    val title: String,
    val message: String,
    private val negativeButton: String?,
    private val positiveButton: String?,
    val listener: Listener?,
    val type: Int = 1
) :
    AlertDialog(c) {

    public interface Listener {
        fun onClick(button: Int)
    }

    companion object {
        val POSITIVE = 2
        val NEGATIVE = 1
    }

    var textView: TextView? = null
    var messageView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_channels_dlialog)
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        textView = findViewById(R.id.tv_title)
        messageView = findViewById(R.id.tv_message)
        val yes: TextView? = findViewById(R.id.tv_yes)
        val no: TextView? = findViewById(R.id.tv_no)

        when (type) {
            1 -> {

            }
        }

        textView?.text = title
        messageView?.text = message

        //if (!Utils.isEmpty(negativeButton))
            no?.text = negativeButton
        //if (!Utils.isEmpty(positiveButton))
            yes?.text = positiveButton

        yes?.setOnClickListener {
            listener?.onClick(POSITIVE)
            dismiss()
        }

        no?.setOnClickListener {
            listener?.onClick(NEGATIVE)
            dismiss()
        }

    }

}