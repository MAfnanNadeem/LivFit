package life.mibo.android.ui.ch6.adapter

import androidx.lifecycle.ViewModel

class Channel6Model() : ViewModel() {
    constructor(
        id: Int, image: Int = 0, percentChannel: Int = 0, percentMuscle: Int = 0, title: String = ""
    ) : this() {
        this.id = id
        this.image = image
        this.percentChannel = percentChannel
        this.percentMain = percentMuscle
        this.title = title
    }

    companion object {
        fun from(chPerc: Int) {

        }
    }

//    fun from(chPerc: Int): Channel6Model {
//        Logger.e("Channel6Model "+toString())
//        val ch6 = Channel6Model(id, image, chPerc, percentMain, title)
//        Logger.e("Channel6Model Copy $ch6")
//        if(chPerc == 0)
//            ch6.isPlay = true
//        return ch6
//    }

    var uid: String = ""
    var id: Int = 0
    var image: Int = 0
    var percentChannel: Int = 0
    var percentMain: Int = 0
    //var percentChannel: LiveData<Int>? = null
    //var percentMain: LiveData<Int>? = null
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

    fun incMainPercent() {
        if (percentMain < 100)
            percentMain++
    }

    fun decMainPercent() {
        if (percentMain > 1)
            percentMain--
    }

    override fun toString(): String {
        return "Channel6Model(uid='$uid', id=$id, image=$image, percentChannel=$percentChannel, percentMain=$percentMain, title='$title', isPlay=$isPlay)"
    }


}