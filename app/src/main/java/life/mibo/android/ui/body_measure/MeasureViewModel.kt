/*
 *  Created by Sumeet Kumar on 4/14/20 3:25 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 3:25 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import life.mibo.hardware.core.Logger

class MeasureViewModel : ViewModel() {
    val text = MutableLiveData<String>()
    var nextButton = MutableLiveData<Boolean>(true)

    fun setText(item: String) {
        text.value = item
    }

    fun updateNext(enable: Boolean) {
        Logger.e("updateNext nextButton $enable")
        nextButton.value = enable
    }


}