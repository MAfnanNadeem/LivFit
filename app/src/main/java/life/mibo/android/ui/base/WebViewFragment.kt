/*
 *  Created by Sumeet Kumar on 4/27/20 3:28 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/27/20 2:29 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.base

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_webview.*
import life.mibo.android.R
import life.mibo.android.ui.main.Navigator
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

        fun bundle(url: String?, title: String = ""): Bundle {
            val arg = Bundle()
            arg.putString("url_url", url)
            arg.putString("url_title", title)
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
        val title = arguments?.getString("url_title", "") ?: ""
        if (title.isNotEmpty())
            activity?.title = title
        webView?.setListener(this, this)
        webView?.loadUrl(url)
        //webView?.addPermittedHostname("https://accounts.google.com")
        setHasOptionsMenu(true)
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
        log("onPageFinished url $url")
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        progressBar?.visibility = View.GONE
        log("onPageError url $url  : $errorCode")

    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
        log("onDownloadRequested url $url")
    }

    override fun onExternalPageRequest(url: String?): Boolean {
        log("onExternalPageRequest url $url")
        if (url?.startsWith("https://accounts.google.com") == true) {
            log("onExternalPageRequest startsWith https://accounts.google.com")
            return false
        }
        return true
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        progressBar?.visibility = View.VISIBLE
        log("onPageStarted url $url")
    }

    override fun onBackPressed(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return false
        }
        return super.onBackPressed()
    }

    override fun onNavBackPressed(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return false
        }
        return super.onNavBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_webview, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.action_notifications) {
            navigate(Navigator.CLEAR_HOME, null)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}