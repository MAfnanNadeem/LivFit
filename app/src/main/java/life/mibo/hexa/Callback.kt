package life.mibo.hexa

interface Callback {
    companion object {
        var CONNECT = 101;
        var DISCONNECT = 102;
        var SCAN = 103;
    }

    fun onCall(type: Int, data: Any?)
}