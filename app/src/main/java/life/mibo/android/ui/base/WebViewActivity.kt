/*
 *  Created by Sumeet Kumar on 4/27/20 3:28 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/27/20 2:29 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.base

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_webview.*
import life.mibo.android.R
import life.mibo.android.ui.fit.fitbit.Fitbit
import life.mibo.views.AdvancedWebView


class WebViewActivity : BaseActivity(), AdvancedWebView.Listener {

    companion object {


        fun launch(context: androidx.fragment.app.Fragment, url: String?, type: Int) {
            val intent = Intent(context.requireContext(), WebViewActivity::class.java)
            intent.putExtra("url_url", url)
            intent.putExtra("url_type", type)
            context.startActivityForResult(intent, Fitbit.REQUEST_CODE)
        }

        fun launch(context: Context, url: String?, type: Int) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url_url", url)
            intent.putExtra("url_type", type)
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_webview)
        baseUrl = intent?.getStringExtra("url_url") ?: ""
        baseType = intent?.getIntExtra("url_type", Fitbit.ANY) ?: Fitbit.ANY
        log("onCreate baseUrl $baseUrl")
        webView?.setListener(this, this)
        webView?.loadUrl(baseUrl)
    }

    private var baseUrl = ""
    private var baseType = 0

    fun fitbitSuccess(result: String?) {
        log("fitbitSuccess result $result")
        if (result == null)
            return
        Fitbit().fitbitSuccess(result, this)

        // onPageStarted https://test.mibolivfit.club/androidappcallback#access_token=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkJOSjYiLCJzdWIiOiI4TVZWRlkiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd251dCB3c2V0IHdhY3Qgd3NvYyIsImV4cCI6MTU5NDUyOTA0MCwiaWF0IjoxNTkzOTI0MjQwfQ.eZ7yB3DMHuV2M0s_5tYZ_EJ-UDpT_-hniStuabYytUc&user_id=8MVVFY&scope=nutrition+settings+activity+social+heartrate&token_type=Bearer&expires_in=604800
        // onPageFinished https://test.mibolivfit.club/androidappcallback#access_token=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkJOSjYiLCJzdWIiOiI4TVZWRlkiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd251dCB3c2V0IHdhY3Qgd3NvYyIsImV4cCI6MTU5NDUyOTA0MCwiaWF0IjoxNTkzOTI0MjQwfQ.eZ7yB3DMHuV2M0s_5tYZ_EJ-UDpT_-hniStuabYytUc&user_id=8MVVFY&scope=nutrition+settings+activity+social+heartrate&token_type=Bearer&expires_in=604800
        //var data = ""
        // if (result.startsWith(callback))
        //     data = result.substring(0, callback.length)


    }


    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onDestroy() {
        webView?.onDestroy()
        super.onDestroy()
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        progressBar?.visibility = View.VISIBLE
        log("onPageStarted $url")
        if (baseType == Fitbit.FITBIT) {
            fitbitSuccess(url)
        }

    }

    override fun onPageFinished(url: String?) {
        progressBar?.visibility = View.GONE
        log("onPageFinished $url")
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        progressBar?.visibility = View.GONE
        log("onPageError $errorCode :: $description >> $failingUrl")

    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
        log("onDownloadRequested $url :: $suggestedFilename")
    }

    override fun onExternalPageRequest(url: String?) {
        log("onExternalPageRequest $url")
    }


    override fun onBackPressed() {
        log("onBackPressed $baseUrl")
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        return super.onBackPressed()
    }

}