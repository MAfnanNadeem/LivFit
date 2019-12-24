package life.mibo.hexa.ui.ch6.adapter

import androidx.lifecycle.ViewModel

class Channel6Model() : ViewModel() {
    constructor(
        id: Int, image: Int = 0, percentChannel: Int = 0, percentMuscle: Int = 0, title: String = ""
    ) : this() {
        this.id = id
        this.image = image
        this.percentChannel = percentChannel
        this.percentMuscle = percentMuscle
        this.title = title
    }

    var uid: String = ""
    var id: Int = 0
    var image: Int = 0
    var percentChannel: Int = 0
    var percentMuscle: Int = 0
    //var percentChannel: LiveData<Int>? = null
    //var percentMuscle: LiveData<Int>? = null
    var title: String = ""


    var isPlay = false


    fun incChannelPercent() {
        if (percentChannel < 100)
            percentChannel++
    }

    fun decChannelPercent() {
        if (percentChannel > 1)
            percentChannel--
    }

    fun incMuslePercent() {
        if (percentMuscle < 100)
            percentMuscle++
    }

    fun decMusclePercent() {
        if (percentMuscle > 1)
            percentMuscle--
    }

}