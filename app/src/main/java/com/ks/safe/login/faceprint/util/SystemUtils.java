package com.ks.safe.login.faceprint.util;

/**
 * Created by Admin on 2017/6/21 0021 18:03.
 * Author: kang
 * Email: kangsafe@163.com
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by yue on 15/10/29.
 * 系统工具
 */
@SuppressWarnings("deprecation")
public class SystemUtils {
    private SystemUtils() {
    }

    /**
     * 根据输入法的状态显示和隐藏输入法
     *
     * @param context
     */
    public static void autoInputmethod(Context context) {
        @SuppressWarnings("static-access")
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示输入法
     *
     * @param view
     */
    public static void showInputmethod(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏输入法
     *
     * @param view
     */
    public static void hideInputmethod(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 使UI适配输入法
     *
     * @param activity Activity
     */
    public static void adjustSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2sp(Context context, float dpValue){
        float px=dp2px(context,dpValue);
        return px2sp(context,px);
    }




    /**
     * ip转16进制
     *
     * @param ips
     * @return
     */
    public static String ipToHex(String ips) {
        StringBuffer result = new StringBuffer();
        if (ips != null) {
            StringTokenizer st = new StringTokenizer(ips, ".");
            while (st.hasMoreTokens()) {
                String token = Integer.toHexString(Integer.parseInt(st
                        .nextToken()));
                if (token.length() == 1)
                    token = "0" + token;
                result.append(token);
            }
        }
        return result.toString();
    }

    /**
     * 16进制转IP
     *
     * @param ips
     * @return
     */
    public static String texToIp(String ips) {
        try {
            StringBuffer result = new StringBuffer();
            if (ips != null && ips.length() == 8) {
                for (int i = 0; i < 8; i += 2) {
                    if (i != 0)
                        result.append('.');
                    result.append(Integer.parseInt(ips.substring(i, i + 2), 16));
                }
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 从assets 文件夹中读取文本数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getTextFromAssets(final Context context,
                                           String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "UTF-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从assets 文件夹中读取图片
     *
     * @param fileName
     * @return
     */
    public static Drawable loadImageFromAsserts(final Context ctx,
                                                String fileName) {
        try {
            InputStream is = ctx.getResources().getAssets().open(fileName);
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 返回所有Activity
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getActivities(Context context) {
        ArrayList<String> result = new ArrayList<String>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setPackage(context.getPackageName());
        for (ResolveInfo info : context.getPackageManager().queryIntentActivities(
                intent, 0)) {
            result.add(info.activityInfo.name);
        }
        return result;
    }

    /**
     * 返回META-DATA (activity)
     *
     * @param activity
     * @return
     */

    public static String getMetaDataForActivity(Activity activity, String key) {
        ActivityInfo info = null;
        try {
            info = activity.getPackageManager().getActivityInfo(
                    activity.getComponentName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.metaData.getString(key);
    }

    /**
     * 返回META-DATA (application)
     *
     * @param application
     * @return
     */
    public static String getMetaDataForApplication(Application application,
                                                   String key) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = application.getPackageManager().getApplicationInfo(
                    application.getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo.metaData.getString(key);
    }

    /**
     * 判断缓存中这个数据库是否存在
     *
     * @param context
     * @param dbName
     * @return boolean
     */

    @SuppressLint("SdCardPath")
    public static boolean isCacheDBExist(Context context, String dbName) {
        String dbPath = "/data/data/" + context.getPackageName()
                + "/databases/" + dbName;
        File dbFile = new File(dbPath);
        return dbFile.exists();
    }

    /**
     * 通过外部浏览器打开页面
     *
     * @param context
     * @param urlText
     */
    public static void openBrowser(Context context, String urlText) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse(urlText);
        intent.setData(url);
        context.startActivity(intent);
    }

    /**
     * 切换全屏状态。
     *
     * @param activity Activity
     * @param isFull   设置为true则全屏，否则非全屏
     */
    public static void toggleFullScreen(Activity activity, boolean isFull) {
        hideTitleBar(activity);
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (isFull) {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 设置为全屏
     *
     * @param activity Activity
     */
    public static void setFullScreen(Activity activity) {
        toggleFullScreen(activity, true);
    }

    /**
     * 获取系统状态栏高度
     *
     * @param activity Activity
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int dpHeight = Integer.parseInt(field.get(object).toString());
            return activity.getResources().getDimensionPixelSize(dpHeight);
        } catch (Exception e1) {
            e1.printStackTrace();
            return 0;
        }
    }

    /**
     * 隐藏Activity的系统默认标题栏
     *
     * @param activity Activity
     */
    public static void hideTitleBar(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 强制设置Actiity的显示方向为垂直方向。
     *
     * @param activity Activity
     */
    public static void setScreenVertical(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 强制设置Activity的显示方向为横向。
     *
     * @param activity Activity
     */
    public static void setScreenHorizontal(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 安装一个APK文件
     *
     * @param file
     */
    public static void installAPK(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void installAPK(Context context, String path) {
        File apkfile = new File(path);
        if (!apkfile.exists())
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    private static Signature[] getRawSignature(Context context,
                                               String packageName) {
        if ((packageName == null) || (packageName.length() == 0)) {
            return null;
        }
        PackageManager pkgMgr = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pkgMgr.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            return null;
        }
        if (info == null) {
            return null;
        }
        return info.signatures;
    }

    /**
     * 手动创建快捷方式
     *
     * @param context  上下文
     * @param activity 点击启动的activity
     * @param app_name 应用名的ID
     * @param app_icon 图标ID
     */
    public static void CreateShut(Context context, Class<?> activity,
                                  int app_name, int app_icon) {
        Intent addIntent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);
        String title = context.getResources().getString(app_name);
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
                app_icon);
        Intent myIntent = new Intent(context, activity);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);
        context.sendBroadcast(addIntent);
    }

    /**
     * 手机震动
     *
     * @param context
     * @param pattern
     */
    public static void vibrator(Context context, long[] pattern) {
        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, -1);
    }


}
