package life.mibo.hexa.ui.main

interface Navigator {
    companion object {
        const val HOME_POPUP = 200
        const val CONNECT = 101;
        const val DISCONNECT = 102;
        const val SCAN = 103;
        const val HOME = 104;
        const val HOME_VIEW = 105;
        const val SELECT_PROGRAM = 106;

        const  val SESSION = 107;
        const val SESSION_POP = 207;
        const val CLEAR_HOME = 108;

        const val RXL_HOME = 110;
        const val RXL_EXERCISE = 111;
        const val RXL_COURSE_SELECT = 112;
        const val RXL_COURSE_CREATE = 113;
        const val RXL_DETAILS = 114;
        const val POST = 201;

        //var SELECT_REFLEX = 106;
    }

    fun navigateTo(type: Int, data: Any?)
}