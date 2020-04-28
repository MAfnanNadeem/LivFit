/*
 *  Created by Sumeet Kumar on 4/15/20 2:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/15/20 2:24 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.body.picker;

import android.content.Context;

import androidx.annotation.NonNull;

public final class RulerViewUtils {
    public static int sp2px(@NonNull final Context context,
                            final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
