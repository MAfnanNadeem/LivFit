package life.mibo.hexa.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Random;

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

    public static Bitmap getBitmap(String image) {
        return convertStringImagetoBitmap(image);
    }
    public static Bitmap convertStringImagetoBitmap (String image) {

        if(image == null || image.isEmpty())
            image ="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";

        byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    public static String convertSecondsToHourMinSec(int seconds){

        return String.format("%02d:%02d", seconds/60, seconds % 60);
    }

    public static boolean checkLimitValues(int level) {
        if (level < 50)
            return true;
        else return false;
//        switch (level){
//            case 20:
//                return true;
//            case 40:
//                return true;
//            case 50:
//                return true;
//            case 60:
//                return true;
//            case 70:
//                return true;
//            case 80:
//                return true;
//            case 90:
//                return false;
//
//            default:
//                return false;
//        }

    }
    public static boolean checkLimitChannelsValues(int level) {
        if((level % 5) == 0){
            return true;
        } else {
            return false;
        }

    }
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    private static float scale;

    public static float dpToPixel(float dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (dp * scale);
    }

    public static int dpToPixel(int dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }

    public static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public static int ofArgb(float fraction, int startColor, int endColor) {
        int startA = (startColor >> 24) & 0xff;
        int startR = (startColor >> 16) & 0xff;
        int startG = (startColor >> 8) & 0xff;
        int startB = startColor & 0xff;
        int endA = (endColor >> 24) & 0xff;
        int endR = (endColor >> 16) & 0xff;
        int endG = (endColor >> 8) & 0xff;
        int endB = endColor & 0xff;
        return (startA + (int)(fraction * (endA - startA))) << 24 |
                (startR + (int)(fraction * (endR - startR))) << 16 |
                (startG + (int)(fraction * (endG - startG))) << 8 |
                (startB + (int)(fraction * (endB - startB)));
    }
}
