package com.iflytek.facedemo.util;

/**
 * Created by Administrator on 2017/6/23.
 */

import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by liruiyuan on 2015/12/21.
 */
public class SettingUtil {

    static public int getScreenWidthPixels(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(dm);
        return dm.widthPixels;
    }

    static public int dipToPx(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }

    static public float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }

    public static void putString(Context context, String key, String val) {
        Settings.System.putString(context.getContentResolver(), key, val);
    }

    public static String getString(Context context, String key) {
        try {
            String msg = Settings.System.getString(context.getContentResolver(), key);
            return msg == null ? "" : msg;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}