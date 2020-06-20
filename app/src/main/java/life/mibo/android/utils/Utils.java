package life.mibo.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import life.mibo.android.R;
import life.mibo.android.models.program.Program;
import life.mibo.android.ui.main.MiboApplication;
import life.mibo.android.ui.main.MiboEvent;
import life.mibo.hardware.core.Logger;

public class Utils {

    public static void toastDebug(String msg) {
        if (MiboApplication.Companion.getDEBUG()) {
            try {
                Toasty.info(MiboApplication.Companion.getContext(), msg).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


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

    public static Bitmap convertStringImagetoBitmap(String image) {

        if (image == null || image.isEmpty())
            image = "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";

        byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    public static String convertSecondsToHourMinSec(int seconds) {

        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public static boolean checkLimitValues(int level) {
        if (level < 100)
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
        if ((level % 5) == 0) {
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
        return (startA + (int) (fraction * (endA - startA))) << 24 |
                (startR + (int) (fraction * (endR - startR))) << 16 |
                (startG + (int) (fraction * (endG - startG))) << 8 |
                (startB + (int) (fraction * (endB - startB)));
    }

    public static void success(Context context, int resId) {
        success(context, resId, Toasty.LENGTH_SHORT);
    }

    public static void success(Context context, int resId, int length) {
        try {
            Toasty.custom(context, context.getString(resId), AppCompatResources.getDrawable(context, R.drawable.ic_check_white_24dp),
                    ContextCompat.getColor(context, R.color.successColor), ContextCompat.getColor(context, R.color.defaultTextColor),
                    length, true, true).show();
        } catch (Exception e) {

        }
    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return true;
                    }
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    public static int getWifiSignalLevel(int rssi, int level) {
        int MIN_RSSI = -100;
        int MAX_RSSI = -55;

        if (rssi <= MIN_RSSI) {
            return 0;
        } else if (rssi >= MAX_RSSI) {
            return level - 1;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (level - 1);
            return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String bitmapToBase64(Drawable drawable) {
        try {
            Bitmap bitmap = drawableToBitmap(drawable);
            Logger.e("bitmapToBase64 Drawable Bitmap size: " + bitmap.getByteCount() / 1024);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            MiboEvent.INSTANCE.log(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        try {
            Logger.e("bitmapToBase64 Bitmap size: " + bitmap.getByteCount() / 1024);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            MiboEvent.INSTANCE.log(e);
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable
    Bitmap base64ToBitmap(String bitmap) {
        try {
            Logger.e("bitmapToBase64 String length: " + bitmap.length());
            Logger.e("bitmapToBase64 String size: " + bitmap.length() / 1024);
            byte[] imageBytes = Base64.decode(bitmap, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            MiboEvent.INSTANCE.log(e);
            e.printStackTrace();
            return null;
        }
    }

    public static void loadBase64Image(ImageView image, String thumbnail, int defaultRes) {
        if (image == null)
            return;
        try {
            Maybe.fromCallable(() -> {
                if (isEmpty(thumbnail))
                    return null;
                else
                    return base64ToBitmap(thumbnail);
            }).doOnSuccess(bitmap -> {
                if (bitmap != null) {
                    image.setImageBitmap(bitmap);
                } else {
                    image.setImageResource(defaultRes);
                }
            }).doOnError(throwable -> {
                image.setImageResource(defaultRes);
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

        } catch (Exception e) {
            MiboEvent.INSTANCE.log(e);
            e.printStackTrace();
            image.setImageResource(defaultRes);
        }
    }


    public static String testUserImage() {
        return "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";
    }


    public static void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight(), 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    public static void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    public static int getInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return -1;
        }

    }

    public static int getColorAt(String index) {
        try {
            return getColors().get(getInt(index)).getId();
        } catch (Exception e) {
            return Color.RED;
        }

    }

    public static int getColorAt(int index) {
        try {
            return getColors().get(index).getId();
        } catch (Exception e) {
            return Color.RED;
        }

    }

    public static ArrayList<Program> getColors() {
        ArrayList<Program> list = new ArrayList<>();
        list.add(new Program(0xFFFF0000));
        list.add(new Program(0xFF00FF00));
        list.add(new Program(0xFFFFFF00));
        list.add(new Program(0xFF0000FF));
        list.add(new Program(0xFFFF00FF));
        list.add(new Program(0xFF00FFFF));
        list.add(new Program(0xFF00b75b));
        list.add(new Program(0xFF800000));
        list.add(new Program(0xFF808000));
        list.add(new Program(0xFF000080));
        list.add(new Program(0xFF800080));
        list.add(new Program(0xFF008080));
        list.add(new Program(0xFFa7d129));
        list.add(new Program(0xFF111111));
        list.add(new Program(0xFFfa8072));
        list.add(new Program(0xFFFFFFFF));
        return list;
    }

    public final static Drawable getColorFilterDrawable(Context context, @DrawableRes int drawable, @ColorInt int color) {
        try {
            Drawable d = ContextCompat.getDrawable(context, drawable).getConstantState().newDrawable().mutate();
            d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception ee) {
            MiboEvent.INSTANCE.log(ee);
        }
    }

    public static void hideKeyboard(View view, Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception ee) {
            MiboEvent.INSTANCE.log(ee);
        }
    }

    public static Bitmap decodeSampledBitmap(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int stretch_width = Math.round((float) width / (float) reqWidth);
        int stretch_height = Math.round((float) height / (float) reqHeight);

        if (stretch_width <= stretch_height)
            return stretch_height;
        else
            return stretch_width;
    }


    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int sp2px(Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    public static void loadImage(ImageView imageView, String url, boolean genderMale) {
        if (imageView == null)
            return;
        try {
            int def = genderMale ? R.drawable.ic_user_male : R.drawable.ic_user_female;
            if (url != null && (url.endsWith("jpg") || url.endsWith("png"))) {
                Glide.with(imageView).load(url).fitCenter().error(def).fallback(def).into(imageView);
            } else {
                Glide.with(imageView).load(def).fitCenter().error(def).fallback(def).into(imageView);
                //imageView.setImageResource(def);
            }
        } catch (Exception e) {

        }

    }

    public static void expand(final View v) {
        if (v == null)
            return;
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        if (v == null)
            return;

        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


}
