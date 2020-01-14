package life.mibo.hexa.ui.main

public interface Navigator {
    companion object {
        var CONNECT = 101;
        var DISCONNECT = 102;
        var SCAN = 103;
        var HOME = 104;
        var HOME_VIEW = 105;
    }

    fun navigateTo(type: Int, data: Any?)
}