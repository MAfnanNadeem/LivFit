/*
 *  Created by Sumeet Kumar on 2/5/20 10:02 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/5/20 10:02 AM
 *  Mibo Hexa - mibo_hardware
 */

package life.mibo.hardware.core;

import java.util.Arrays;

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
}
