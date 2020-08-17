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
        const val RESCHEDULE = 123;
        const val SCHEDULE = 124;
        const val SELECT_TRAINER_SESSION = 125;
        const val INVOICES = 126;
        const val ORDERS = 127;
        const val HOME_DRAWER = 128;
        const val PROFILE_UPDATE = 129;
        const val HOME_START = 131;
        const val HOME_STOP = 132;
        const val GOOGLE_FIT = 133;
        const val SALES = 134;
        const val SETTINGS_UNIT = 135;
        const val MY_SERVICES = 137;
        const val MY_CLIENTS = 138;
        const val MY_SALES = 139;
        const val VIEW_MEASUREMENT = 140;
        const val VIEW_SESSIONS = 141;
        const val UPDATE_DATA = 142;
        const val RXT_START_WORKOUT = 144;
        const val RXT_SELECT_WORKOUT = 145;
        const val RXT_CONFIGURE = 146;


        const val POST = 201;
        const val LOGOUT = 202;
        const val PIC_UPLOADED = 203;
        const val DRAWER_LOCK = 301;
        const val DRAWER_UNLOCK = 302;
        const val FAB_UPDATE = 303;


        const val WEBVIEW = 400;

        //var SELECT_REFLEX = 106;
    }

    fun navigateTo(type: Int, data: Any?)
}