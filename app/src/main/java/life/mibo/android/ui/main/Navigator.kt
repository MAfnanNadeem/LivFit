package life.mibo.android.ui.main

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
        const val RXL_TABS = 115;
        const val RXL_TABS_2 = 116;
        const val RXL_QUICKPLAY_DETAILS = 117;
        const val RXL_QUICKPLAY_DETAILS_PLAY = 118;
        const val SELECT_MUSCLES = 119;
        const val SELECT_SUITS= 120;
        const val BODY_MEASURE= 121;
        const val BODY_MEASURE_SUMMARY = 122;
        const val POST = 201;
        const val LOGOUT = 202;
        const val PIC_UPLOADED = 203;
        const val DRAWER_LOCK = 301;
        const val DRAWER_UNLOCK = 302;

        //var SELECT_REFLEX = 106;
    }

    fun navigateTo(type: Int, data: Any?)
}