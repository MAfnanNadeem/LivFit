/*
 *  Created by Sumeet Kumar on 5/11/20 11:21 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/11/20 10:25 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import life.mibo.android.R
import life.mibo.android.core.Prefs


class RememberMeDialog(var type_: Int = 0) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (type_ == 300)
            return inflater.inflate(R.layout.fragment_ip_profile, container, false)
        return inflater.inflate(R.layout.fragment_remeber_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var save: Button? = view?.findViewById(R.id.btn_remember)
        var skip: View? = view?.findViewById(R.id.btn_skip)
        var tv: TextView? = view?.findViewById(R.id.user_name)
        var text: TextView? = view?.findViewById(R.id.profile_text)
        isCancelable = false

        if (type_ == 200) {
            tv?.setText(getString(R.string.coming_soon))
            text?.setText(getString(R.string.coming_soon))
            //text?.visibility = View.INVISIBLE
            save?.setText(R.string.close)
            skip?.visibility = View.INVISIBLE
        }

        save?.setOnClickListener {
            val prefs = Prefs.getEncrypted(context)
            prefs.set("login_enable", "true", true)
            dismiss()
        }

        skip?.setOnClickListener {
            Prefs.get(context).set("skip_pwd_", "true")
            dismiss()
        }
    }


    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog?.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}