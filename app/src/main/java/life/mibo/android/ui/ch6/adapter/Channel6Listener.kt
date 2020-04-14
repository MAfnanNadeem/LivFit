package life.mibo.android.ui.ch6.adapter

import life.mibo.android.models.muscle.Muscle


interface Channel6Listener {
    fun onClick(data: Muscle)
    fun onPlusClicked(data: Muscle)
    fun onMinusClicked(data: Muscle)
    fun onPlayPauseClicked(data: Muscle, isPlay: Boolean)
    fun exerciseCompleted(userStopped : Boolean)
    fun onBackPressed(): Boolean
}