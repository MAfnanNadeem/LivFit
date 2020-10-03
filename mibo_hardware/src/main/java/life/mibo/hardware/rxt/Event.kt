package life.mibo.hardware.rxt

data class Event(val id: Int, val actionTime: Int, val tapTime: Int, var isFocus: Boolean = true)