package life.mibo.hexa.ui.main

public interface Navigator {
    companion object {
        var CONNECT = 101;
        var DISCONNECT = 102;
        var SCAN = 103;
        var HOME = 104;
        var HOME_VIEW = 105;
        var SELECT_PROGRAM = 106;
        var SESSION = 107;
        var CLEAR_HOME = 108;

        var RXL_HOME = 110;
        var RXL_EXERCISE = 111;
        var RXL_COURSE_SELECT = 112;
        var RXL_COURSE_CREATE = 113;
        var RXL_DETAILS = 114;
    }

    fun navigateTo(type: Int, data: Any?)
}