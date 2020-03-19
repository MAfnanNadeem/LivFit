/*
 *  Created by Sumeet Kumar on 2/5/20 10:02 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/5/20 10:02 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.core;

import java.util.ArrayList;
import java.util.Arrays;

import life.mibo.hardware.rxl.program.RxlColor;

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
}
