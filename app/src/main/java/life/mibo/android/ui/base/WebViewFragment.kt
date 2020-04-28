/*
 *  Created by Sumeet Kumar on 4/27/20 3:28 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/27/20 2:29 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.base

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_webview.*
import life.mibo.android.R
import life.mibo.views.AdvancedWebView


class WebViewFragment : BaseFragment(), AdvancedWebView.Listener {

    companion object {
        fun create(url: String?): WebViewFragment {
            val frg = WebViewFragment()
            val arg = Bundle()
            arg.putString("url_url", url)
            frg.arguments = arg
            return frg
        }

        fun bundle(url: String?): Bundle {
            val arg = Bundle()
            arg.putString("url_url", url)
            return arg
        }
    }

    private var url = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        url = arguments?.getString("url_url", "") ?: ""
        webView?.setListener(this, this)
        webView?.loadUrl(url)

    }

    override fun onPause() {
        webView?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onDestroy() {
        webView?.onDestroy()
        super.onDestroy()
    }

    override fun onPageFinished(url: String?) {
        progressBar?.visibility = View.GONE

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