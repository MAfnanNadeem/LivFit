/*
 *  Created by Sumeet Kumar on 2/3/20 3:18 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/3/20 3:08 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import life.mibo.android.models.program.Block;
import life.mibo.android.models.program.Duration;

public class ProgramConverter {

    @TypeConverter
    public static List<Block> toBlockList(String value) {
        Type type = new TypeToken<List<Block>>() {
        }.getType();
        return new Gson().fromJson(value, type);
    }

    @TypeConverter
    public static String fromBlockLis(List<Block> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static Duration toDuration(String value) {
        Type type = new TypeToken<Duration>() {
        }.getType();
        return new Gson().fromJson(value, type);
    }

    @TypeConverter
    public static String fromDuration(Duration list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

}