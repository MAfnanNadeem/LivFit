/*
 *  Created by Sumeet Kumar on 2/3/20 11:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/3/20 11:05 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import life.mibo.hardware.models.Device

class ReflexLiveModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    lateinit var devices: LiveData<List<Device>>


    fun setDevices(list: List<Device>) {
        devices = MutableLiveData<List<Device>>().apply {
            value = list
        }
    }

}