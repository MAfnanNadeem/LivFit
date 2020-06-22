/*
 *  Created by Sumeet Kumar on 2/5/20 10:02 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/5/20 10:02 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.core;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;
import java.util.Arrays;

import life.mibo.hardware.rxl.program.RxlColor;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT16;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT32;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT32;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

public class Utils {
    public static String getBytes(byte[] message) {
        return "byte: " + Arrays.toString(message);
    }

    public static String getChars(byte[] message) {
        return "char: " + Arrays.toString(new String(message).toCharArray());
    }

    public static String getUid(String name) {
        try {
            if (name.startsWith("MBRXL"))
                return name.replace("MBRXL-", "");
            if (name.startsWith("MIBO-"))
                return name.replace("MIBO-", "");
            if (name.startsWith("MIBO-RXL"))
                return name.replace("MIBO-RXL-", "");
            if (name.startsWith("RXL"))
                return name.replace("RXL-", "");
        } catch (Exception e) {
            e.printStackTrace();

        }
        return name;
    }

    public static ArrayList<RxlColor> getColors() {
        ArrayList<RxlColor> list = new ArrayList<>();
        list.add(new RxlColor(0xFFFF0000));
        list.add(new RxlColor(0xFF00FF00));
        list.add(new RxlColor(0xFF0000FF));
        list.add(new RxlColor(0xFFFFFF00));
        list.add(new RxlColor(0xFFFF00FF));
        list.add(new RxlColor(0xFF00FFFF));
        list.add(new RxlColor(0xFF00b75b));
        list.add(new RxlColor(0xFF800000));
        list.add(new RxlColor(0xFF808000));
        list.add(new RxlColor(0xFF000080));
        list.add(new RxlColor(0xFF800080));
        list.add(new RxlColor(0xFF008080));
        list.add(new RxlColor(0xFFa7d129));
        list.add(new RxlColor(0xFF111111));
        list.add(new RxlColor(0xFFfa8072));
        list.add(new RxlColor(0xFFFFFFFF));
        return list;
    }

    private static int extractHeartRate(BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;
        // Heart rate bit number format
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        return characteristic.getIntValue(format, 1);
    }

    private static int getBleFormat(int property) {

        int format = -1;
        // Heart rate bit number format
        if ((property & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }
        return format;
    }

    public static int getHeartRate(byte[] hr, int property) {
        return getIntValue(hr, getBleFormat(property), 1);
    }

    public static int getIntValue(byte[] mValue, int formatType, int offset) {
        if ((offset + getTypeLen(formatType)) > mValue.length) return 0;

        switch (formatType) {
            case FORMAT_UINT8:
                return unsignedByteToInt(mValue[offset]);

            case FORMAT_UINT16:
                return unsignedBytesToInt(mValue[offset], mValue[offset + 1]);

            case FORMAT_UINT32:
                return unsignedBytesToInt(mValue[offset], mValue[offset + 1],
                        mValue[offset + 2], mValue[offset + 3]);
            case FORMAT_SINT8:
                return unsignedToSigned(unsignedByteToInt(mValue[offset]), 8);

            case FORMAT_SINT16:
                return unsignedToSigned(unsignedBytesToInt(mValue[offset],
                        mValue[offset + 1]), 16);

            case FORMAT_SINT32:
                return unsignedToSigned(unsignedBytesToInt(mValue[offset],
                        mValue[offset + 1], mValue[offset + 2], mValue[offset + 3]), 32);
        }

        return 0;
    }

    private static int unsignedToSigned(int unsigned, int size) {
        if ((unsigned & (1 << size - 1)) != 0) {
            unsigned = -1 * ((1 << size - 1) - (unsigned & ((1 << size - 1) - 1)));
        }
        return unsigned;
    }

    private static int getTypeLen(int formatType) {
        return formatType & 0xF;
    }

    /**
     * Convert a signed byte to an unsigned int.
     */
    private static int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    /**
     * Convert signed bytes to a 16-bit unsigned int.
     */
    private static int unsignedBytesToInt(byte b0, byte b1) {
        return (unsignedByteToInt(b0) + (unsignedByteToInt(b1) << 8));
    }

    private static int unsignedBytesToInt(byte b0, byte b1, byte b2, byte b3) {
        return (unsignedByteToInt(b0) + (unsignedByteToInt(b1) << 8))
                + (unsignedByteToInt(b2) << 16) + (unsignedByteToInt(b3) << 24);
    }

}
