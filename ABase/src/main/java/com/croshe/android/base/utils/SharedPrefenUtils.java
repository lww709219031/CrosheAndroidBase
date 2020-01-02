package com.croshe.android.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

/**
 * sharedPrefenences
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by 刘文武 on 2017/8/30.
 */
public class SharedPrefenUtils {

    public static boolean getBoolPreferences(Context context, String key,
                                             boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, defValue);
    }

    public static int getIntPreferences(Context context, String key,
                                        int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                key, defValue);
    }

    public static long getLongPreferences(Context context, String key,
                                          long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                key, defValue);
    }

    public static String getStringPreferences(Context context, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
    }

    public static void saveBoolPreferences(Context context, String key,
                                           boolean defValue) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, defValue);
        editor.commit();
    }

    public static void saveIntPreferences(Context context, String key,
                                          int defValue) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putInt(key, defValue);
        editor.commit();

    }

    public static void saveLongPreferences(Context context, String key,
                                           long defValue) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putLong(key, defValue);
        editor.commit();
    }

    public static void saveStringPreferences(Context context, String key,
                                             String defValue) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putString(key, defValue);
        editor.commit();
    }

    public static void clearForKeyData(Context context, String key) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clearAllData(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear();
        editor.commit();
    }

}
