/*
 *  Created by Sumeet Kumar on 4/29/20 3:08 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/13/20 3:23 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.login

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import life.mibo.android.R
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.views.AdvancedWebView

class TermsDialog(c: Context, var listener: ItemClickListener<Int>? = null) : AlertDialog(c), AdvancedWebView.Listener {

    var progressBar: ProgressBar? = null
    var accept: Button? = null
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_dialog_terms, container, false)
//    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        accept = view?.findViewById(R.id.button_change)
//        val web: AdvancedWebView? = view?.findViewById(R.id.webView)
//        progressBar = view?.findViewById(R.id.progressBar)
//
//        accept?.setOnClickListener {
//            listener?.onItemClicked(2, 2);
//        }
//
//        //window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
//
//        web?.setListener(this, this)
//        web?.loadUrl("http://test.mibo.life/privacy-policy-mobile/")
//        accept?.isEnabled = false
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
//        dialog?.window?.attributes?.windowAnimations = R.style.DialogBounceAnimation;
//        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//    }

    fun show(manager: FragmentManager, tag: String?) {
        // super.show(manager, tag)
        show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dialog_terms)
        accept = findViewById(R.id.button_change)
        val web: AdvancedWebView? = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        accept?.setOnClickListener {
            listener?.onItemClicked(2, 2);
            dismiss()
        }

        //window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        web?.setListener(ownerActivity, this)
        //web?.loadUrl("http://test.mibo.life/terms-condition-mobile/")
        web?.loadUrl("http://test.mibo.life/privacy-policy-mobile/")
        accept?.isEnabled = false
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        window?.attributes?.windowAnimations = R.style.DialogBounceAnimation;
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
//        window?.attributes?.windowAnimations = R.style.DialogBounceAnimation;
//        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    override fun onPageFinished(url: String?) {
        progressBar?.visibility = View.GONE
        accept?.isEnabled = true
        accept?.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        progressBar?.visibility = View.GONE
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {

    }

    override fun onExternalPageRequest(url: String?) {

    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        progressBar?.visibility = View.VISIBLE
    }
}