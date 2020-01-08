package life.mibo.hexa

interface Navigator {
    companion object {
        var CONNECT = 101;
        var DISCONNECT = 102;
        var SCAN = 103;
        var HOME = 104;
    }

    fun navigateTo(type: Int, data: Any?)
}