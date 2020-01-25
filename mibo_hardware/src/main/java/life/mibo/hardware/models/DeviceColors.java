package life.mibo.hardware.models;


import android.graphics.Color;
import android.graphics.ColorSpace;

import androidx.annotation.ColorInt;

import java.util.Random;

import life.mibo.hardware.R;

/**
 * Created by Fer on 03/04/2019.
 */

public class DeviceColors implements BaseModel{
    public static final int LIME = 0;
    public static final int ORANGE = 1;
    public static final int PINK = 2;
    public static final int DEEPPURPLE = 3;
    public static final int BROWN = 4;
    public static final int VIOLET = 5;
    public static final int AMBER = 6;
    public static final int PURPLE = 7;
    public static final int CYAN = 8;
    public static final int YELLOW = 9;
    public static final int WHITE = 10;
    public static final int TEAL = 11;

    public static final byte[] LIME_BYTES = new byte[] {(byte)0xCC,(byte)0xDB,(byte)0x38};
    public static final byte[] ORANGE_BYTES = new byte[] {(byte)0xFE,(byte)0x56,(byte)0x21};
    public static final byte[] PINK_BYTES = new byte[] {(byte)0xFF,(byte)0x69,(byte)0xB4};
    public static final byte[] DEEPPURPLE_BYTES = new byte[] {(byte)0x66,(byte)0x39,(byte)0xB6};
    public static final byte[] BROWN_BYTES = new byte[] {(byte)0xA5,(byte)0x2A,(byte)0x2A};
    public static final byte[] VIOLET_BYTES = new byte[] {(byte)0xE7,(byte)0x20,(byte)0xED};
    public static final byte[] AMBER_BYTES = new byte[] {(byte)0xFE,(byte)0xC0,(byte)0x06};
    public static final byte[] PURPLE_BYTES = new byte[] {(byte)0x80,(byte)0x00,(byte)0x80};
    public static final byte[] CYAN_BYTES = new byte[] {(byte)0x00,(byte)0xBB,(byte)0xD3};
    public static final byte[] YELLOW_BYTES = new byte[] {(byte)0xFF,(byte)0xFF,(byte)0x00};
    public static final byte[] WHITE_BYTES = new byte[] {(byte)0xFF,(byte)0xFF,(byte)0xFF};
    public static final byte[] TEAL_BYTES = new byte[] {(byte)0x00,(byte)0x95,(byte)0x87};

    public static byte[] getColorPaleteToByte(int color){
        switch(color){
            case LIME :
                return LIME_BYTES;
            case ORANGE :
                return ORANGE_BYTES;
            case PINK :
                return PINK_BYTES;
            case DEEPPURPLE :
                return DEEPPURPLE_BYTES;
            case BROWN :
                return BROWN_BYTES;
            case VIOLET :
                return VIOLET_BYTES;
            case AMBER :
                return AMBER_BYTES;
            case PURPLE :
                return PURPLE_BYTES;
            case CYAN :
                return CYAN_BYTES;
            case YELLOW :
                return YELLOW_BYTES;
            case WHITE :
                return WHITE_BYTES;
            case TEAL :
                return TEAL_BYTES;
        }
        return LIME_BYTES;
    }

    public static byte[] getRandomColor() {
        try {
            Random rnd = new Random();
            return new byte[]{(byte) rnd.nextInt(256), (byte) rnd.nextInt(256), (byte) rnd.nextInt(256)};
        } catch (Exception e) {

        }
        return VIOLET_BYTES;
    }

    public static byte[] getColor(int color) {
        try {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;
            return new byte[]{(byte) r, (byte) g, (byte) b};
        } catch (Exception e) {
           // Color c = Color.valueOf(color);
        }
        return VIOLET_BYTES;
    }

    public static byte[] getRxlColor(int color, int time) {
        try {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;
            int t1 = (time >> 8) & 0xFF;
            int t2 = (time) & 0xFF;
            return new byte[]{(byte) r, (byte) g, (byte) b, (byte) t1, (byte) t2};
        } catch (Exception e) {
            // Color c = Color.valueOf(color);
        }
        return DeviceColors.VIOLET_BYTES;
    }

//    public static Color valueOf(@ColorInt int color) {
//        float r = ((color >> 16) & 0xff) / 255.0f;
//        float g = ((color >>  8) & 0xff) / 255.0f;
//        float b = ((color      ) & 0xff) / 255.0f;
//        float a = ((color >> 24) & 0xff) / 255.0f;
//        return new Color.convert(a, r, g, b);
//    }

    public static int getColorResourceById(int id) {

        switch (id) {
            case DeviceColors.LIME:
                return  R.color.colorDeviceColorLime;
            case DeviceColors.ORANGE:
                return R.color.colorDeviceColorOrange;
            case DeviceColors.PINK:
                return R.color.colorDeviceColorPink;
            case DeviceColors.DEEPPURPLE:
                return R.color.colorDeviceColorDeepPurple;
            case DeviceColors.BROWN:
                return R.color.colorDeviceColorBrown;
            case DeviceColors.VIOLET:
                return R.color.colorDeviceColorViolet;
            case DeviceColors.AMBER:
                return R.color.colorDeviceColorAmber;
            case DeviceColors.PURPLE:
                return R.color.colorDeviceColorPurple;
            case DeviceColors.CYAN:
                return R.color.colorDeviceColorCyan;
            case DeviceColors.YELLOW:
                return R.color.colorDeviceColorYellow;
            case DeviceColors.WHITE:
                return R.color.colorDeviceColorWhite;
            case DeviceColors.TEAL:
                return R.color.colorDeviceColorTeal;
        }
        return R.color.colorDeviceColorGreen;
    }

    public static int getColorTextById(int id) {

        switch (id) {
            case DeviceColors.LIME:
                return  R.string.txt_color_lime;
            case DeviceColors.ORANGE:
                return R.string.txt_color_orange;
            case DeviceColors.PINK:
                return R.string.txt_color_pink;
            case DeviceColors.DEEPPURPLE:
                return R.string.txt_color_deeppurple;
            case DeviceColors.BROWN:
                return R.string.txt_color_brown;
            case DeviceColors.VIOLET:
                return R.string.txt_color_violet;
            case DeviceColors.AMBER:
                return R.string.txt_color_amber;
            case DeviceColors.PURPLE:
                return R.string.txt_color_purple;
            case DeviceColors.CYAN:
                return R.string.txt_color_cyan;
            case DeviceColors.YELLOW:
                return R.string.txt_color_yellow;
            case DeviceColors.WHITE:
                return R.string.txt_color_white;
            case DeviceColors.TEAL:
                return R.string.txt_color_teal;
        }
        return R.string.txt_color_lime;
    }

}
