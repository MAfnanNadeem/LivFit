package life.mibo.hexa.ui.ch6

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import life.mibo.hexa.ui.ch6.adapter.Channel6Model

class Channel6ViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    var list: LiveData<ArrayList<Channel6Model>> = MutableLiveData<ArrayList<Channel6Model>>()
}