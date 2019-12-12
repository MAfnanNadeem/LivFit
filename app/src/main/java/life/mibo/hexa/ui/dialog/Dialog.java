package life.mibo.hexa.ui.dialog;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

public class Dialog {

    public static KProgressHUD get(Context context) {
        return get(context, "", "", false);
    }

    public static KProgressHUD get(Context context, String title, String msg, boolean cancellable) {
        return KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(msg)
                .setCancellable(cancellable)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }
}
