package life.mibo.hexa.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;

public class Utils {

    public static int getColor(@NonNull Bitmap bitmap) {
        long red = 0;
        long green = 0;
        long blue = 0;
        long count = 0;

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int c = bitmap.getPixel(x, y);

                count++;
                red += Color.red(c);
                green += Color.green(c);
                blue += Color.blue(c);
            }
        }
        return Color.rgb((int) (red / count), (int) (green / count), (int) (blue / count));
    }
}
