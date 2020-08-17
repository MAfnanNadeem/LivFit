package life.mibo.android.ui.rxt.parser

data class Event(val id: Int, val actionTime: Int, val tapTime: Int, var isFocus: Boolean = true)