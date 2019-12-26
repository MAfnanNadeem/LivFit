package life.mibo.hexa.ui.ch6.adapter


interface Channel6Listener {
        fun onClick(data: Channel6Model)
        fun onPlusClicked(data: Channel6Model)
        fun onMinusClicked(data: Channel6Model)
        fun onPlayPauseClicked(data: Channel6Model, isPlay: Boolean)
    }