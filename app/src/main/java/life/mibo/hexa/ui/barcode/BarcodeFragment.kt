/*
 *  Created by Sumeet Kumar on 1/9/20 5:37 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/9/20 5:37 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.barcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.BarcodeFormat
import com.kroegerama.kaiteki.bcode.BarcodeResultListener
import kotlinx.android.synthetic.main.fragment_barcode.*
import life.mibo.hexa.R
import life.mibo.hexa.core.hasCameraPermission
import life.mibo.hexa.core.isPermissionGranted
import life.mibo.hexa.core.requestCameraPermission
import life.mibo.hexa.ui.base.BaseFragment

class BarcodeFragment : BaseFragment(), BarcodeResultListener {

    private var formats: List<BarcodeFormat> = listOf(BarcodeFormat.QR_CODE)

    private val barcodeInverted by lazy {
        arguments?.getBoolean(KEY_INVERTED, false) ?: false
    }

    // val bcode = BarcodeView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_barcode, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        formats?.let {
//            val bcoe = it[0]
//        }
        if (formats.isNullOrEmpty())
            formats = listOf(BarcodeFormat.QR_CODE)
        formats.let(bcode::setFormats)
        bcode.setBarcodeInverted(barcodeInverted)
        bcode.setBarcodeResultListener(this)

        if (requireContext().hasCameraPermission) {
            bcode.bindToLifecycle(this)
        } else {
            requestCameraPermission(REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA ->
                if (grantResults.isPermissionGranted)
                    bcode.bindToLifecycle(this)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStop() {
        super.onStop()
        bcode.unbind()
    }

    override fun onBarcodeResult(result: com.google.zxing.Result): Boolean {
        if ((parentFragment as? BarcodeResultListener)?.onBarcodeResult(result) == true) {
            return true
        } else if ((activity as? BarcodeResultListener)?.onBarcodeResult(result) == true) {
            return true
        }
        return false
    }

    override fun onBarcodeScanCancelled() {
        //Ignore: BarcodeView will never emit this event
    }

    companion object {
        private const val KEY_FORMATS = "formats"
        private const val KEY_INVERTED = "inverted"
        private const val REQUEST_CAMERA = 0xbf_ca
    }
}