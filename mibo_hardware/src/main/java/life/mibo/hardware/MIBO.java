package life.mibo.hardware;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import life.mibo.hardware.core.Timber;

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
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {

                }
            });
        }
    }
}
