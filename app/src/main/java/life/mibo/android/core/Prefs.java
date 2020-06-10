/**
 * @Author: Sumeet Gehi
 * ********************
 * Copyright (c) 2013
 * All Rights Reserved.
 **********************/
package life.mibo.android.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import life.mibo.android.core.security.EncryptedPrefs;
import life.mibo.android.models.login.Member;
import life.mibo.hardware.core.Logger;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Prefs {

    private SharedPreferences preferences;
    private static Prefs instance;
    public static final String PREFS_NAME = "miboPrefs";
    public static final String USER = "user_member";
    public static final String MEMBER = "user_member_";
    public static final String SESSION = "user_session_";

    /**
     * initialize default SharedPreferences
     */
    public Prefs(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @name SharedPreferences name
     */
    public Prefs(Context context, String name) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static Prefs get(Context context) {
        if (instance == null)
            instance = new Prefs(context);
        return instance;
    }

    public static Prefs getTemp(Context context) {
        return new Prefs(context, "miboPrefs");
    }

    public static EncryptedPrefs getEncrypted(Context context) {
        return new EncryptedPrefs(context);
    }


    public SharedPreferences getPreferences() {
        if (preferences == null)
            throw new RuntimeException("Oops you have forget to initialize Prefs class.. Prefs prefs = new Prefs(context)");
        return preferences;
    }

    public SharedPreferences.Editor getEditor() {
        return getPreferences().edit();
    }

    public int get(String key, int defaultValue) {
        return getPreferences().getInt(key, defaultValue);
    }

    public long get(String key, long defaultValue) {
        return getPreferences().getLong(key, defaultValue);
    }

    public float get(String key, float defaultValue) {
        return getPreferences().getFloat(key, defaultValue);
    }

    public double get(String key, double defaultValue) {
        String number = get(key);

        try {
            return Double.parseDouble(number);

        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String get(String key) {
        return getPreferences().getString(key, "");
    }

    public ArrayList<String> getList(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(
                getPreferences().getString(key, ""), "‚‗‚")));
    }

    public boolean get(String key, boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    public void set(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    public void set(String key, long value) {
        getEditor().putLong(key, value).apply();
    }

    public void set(String key, float value) {
        getEditor().putFloat(key, value).apply();
    }

    public void set(String key, double value) {
        set(key, String.valueOf(value));
    }

    public void set(String key, String value) {
        getEditor().putString(key, value).apply();
        Logger.e("Prefs Saved " + key + " : " + value);
    }

    public void setList(String key, ArrayList<String> stringList) {
        String[] myStringList = stringList
                .toArray(new String[stringList.size()]);
        getEditor().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    public void set(String key, boolean value) {
        getEditor().putBoolean(key, value).apply();
    }

    public boolean contains(String key) {
        return getPreferences().contains(key);
    }

    public void remove(String key) {
        getEditor().remove(key).apply();
    }

    public void clear() {
        getEditor().clear().apply();
    }

    public Map<String, ?> getAll() {
        return getPreferences().getAll();
    }

    public void registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {

        getPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {

        getPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void settSerialize(String key, Serializable serializable) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(serializable);
            getEditor().putString(key, json).apply();

        } catch (Exception e) {

        }
    }

    public <T> T getSerialize(String key, Class<T> t) {
        try {
            Gson gson = new Gson();
            String str = get(key);
            return gson.fromJson(str, t);

        } catch (Exception e) {
            return null;
        }

    }

    public <T> void setJson(String key, T o) {
        try {
            String json = new Gson().toJson(o);
            getEditor().putString(key, json).apply();
            Logger.e("Prefs setJson saved " + key);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("Prefs setJson error " + e.getMessage());
        }
    }

    public <T> T getJson(String key, Type t) {
        try {
            String str = get(key);
            Logger.e("Prefs getJson saved " + str);
            return new Gson().fromJson(str, t);

        } catch (Exception e) {
            return null;
        }
    }

    public <T> List<T> getJsonList(String key, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            String jsonString = get(key);
            Gson gson = new Gson();
            JsonArray arry = JsonParser.parseString(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void setMember(Member member) {
        try {
            String json = new Gson().toJson(member, member.getClass());
            getEditor().putString(USER, json).apply();
            setMemberId(member.id());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public @Nullable
    Member getMember() {
        try {
            String str = get(USER);
            return new Gson().fromJson(str, Member.class);

        } catch (Exception e) {
            return null;
        }
    }

    public void setMemberId(Member member) {
        setMemberId(member.id());
    }

    public void setMemberId(String id) {
        set("member_id", id);
    }

    public String getMemberId() {
        return get("member_id");
    }

    public void setMemberToken(String id) {
        set("member_token_auth", id);
    }

    public String getMemberToken() {
        return get("member_token_auth");
    }
}