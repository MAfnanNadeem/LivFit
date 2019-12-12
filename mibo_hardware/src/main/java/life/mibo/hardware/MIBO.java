package life.mibo.hardware;

import android.content.Context;

public class MIBO {

    private static MIBO instance;
    private static Context context;

    private MIBO() {

    }

    public static Context getContext() {
        return context;
    }

    public static MIBO getInstance() {
        if (instance == null)
            instance = new MIBO();
        return instance;
    }

    public static void init(Context c) {
        MIBO.context = c;
    }
}
