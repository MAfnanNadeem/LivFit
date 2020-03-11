/*
 *  Created by Sumeet Kumar on 1/20/20 2:44 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/20/20 2:39 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.barcode

import com.google.zxing.Result

interface BarcodeResultListener {

    fun onBarcodeResult(result: Result): Boolean

    fun onBarcodeScanCancelled()

}