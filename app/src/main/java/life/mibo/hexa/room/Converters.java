/*
 *  Created by Sumeet Kumar on 1/28/20 8:32 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:32 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.room;

import androidx.room.TypeConverter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import life.mibo.hardware.core.Logger;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static InetAddress toInetAddress(String value) {
        try {
            return value == null ? null : InetAddress.getByName(value);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TypeConverter
    public static String fromInetAddress(InetAddress value) {
        return value == null ? null : value.getHostAddress();
    }

    @TypeConverter
    public List<String> stringToList(String list) {
       // Logger.e("Converters : stringToList " + list);
        try {
            if (list != null && list.length() > 0) {
                return Arrays.asList(list.split("\\s*,\\s*"));
            }
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    @TypeConverter
    public String listToString(List<String> list) {
       // Logger.e("Converters : listToString " + list);
        String value = "";
        try {
            if (list != null && list.size() > 0) {
                for (String lang : list)
                    value += lang + ",";
            }
        } catch (Exception e) {

        }

      //  Logger.e("Converters : languagesToStoredString return > " + value);

        return value;
    }

//    @TypeConverter
//    public static FragmentNavigator.Extras toExtras(String value) {
//        try {
//            return value == null ? null : InetAddress.getByName(value);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @TypeConverter
//    public static String fromInetAddress(FragmentNavigator.Extras value) {
//        return value == null ? null : value.getSharedElements().entrySet().toString()
//    }

//    @TypeConverter
//    public static <A> List<A> fromObject(String value) {
//        Type type = new TypeToken<List<A>>() {
//        }.getType();
//        return new Gson().fromJson(value, type);
//    }
//
//    @TypeConverter
//    public static <A> String fromArrayLisr(List<A> list) {
//        Gson gson = new Gson();
//        return gson.toJson(list);
//    }
//
//    @TypeConverter
//    public static List<Object> fromObject(String value) {
//        Type type = new TypeToken<List<Object>>() {
//        }.getType();
//        return new Gson().fromJson(value, type);
//    }
//
//    @TypeConverter
//    public static String fromArrayLisr(List<Object> list) {
//        Gson gson = new Gson();
//        return gson.toJson(list);
//    }
}