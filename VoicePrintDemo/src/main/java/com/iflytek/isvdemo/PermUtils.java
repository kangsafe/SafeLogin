package com.iflytek.isvdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Admin on 2017/2/15 0015 13:45.
 * Author: kang
 * Email: kangsafe@163.com
 */

public class PermUtils {
    //相机权限码
    public static final int REQUEST_CAMERA = 1;
    //sdcrad读权限码
    public static final int REQUEST_EXTERNAL_READ = 2;
    //sdcard写权限码
    public static final int REQUEST_EXTERNAL_WRITE = 3;
    //录音权限码
    public static int REQUEST_RECORD = 4;

    //拍照权限组
    public static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //存储写权限组
    public static final String[] PERMISSIONS_EXTERNAL_WRITE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //存储读权限组
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static final String[] PERMISSIONS_EXTERNAL_READ = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    //录音权限组
    public static final String[] PERMISSION_RECORD = {
            Manifest.permission.RECORD_AUDIO
    };

    /**
     * 检测存储读权限
     *
     * @param activity
     * @return
     */
    public static boolean checkReadStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        if (!readStoragePermissionGranted) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_EXTERNAL_READ, REQUEST_EXTERNAL_READ);
        }
        return readStoragePermissionGranted;
    }

    /**
     * 写权限检测
     *
     * @param fragment
     * @return
     */
    public static boolean checkWriteStoragePermission(Fragment fragment) {

        int writeStoragePermissionState =
                ContextCompat.checkSelfPermission(fragment.getContext(), WRITE_EXTERNAL_STORAGE);

        boolean writeStoragePermissionGranted = writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        if (!writeStoragePermissionGranted) {
            fragment.requestPermissions(PERMISSIONS_EXTERNAL_WRITE, REQUEST_EXTERNAL_WRITE);
        }
        return writeStoragePermissionGranted;
    }

    /**
     * 写权限检测
     *
     * @param activity
     * @return
     */
    public static boolean checkWriteStoragePermission(Activity activity) {

        int writeStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        boolean writeStoragePermissionGranted = writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        if (!writeStoragePermissionGranted) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_EXTERNAL_WRITE, REQUEST_EXTERNAL_WRITE);
        }
        return writeStoragePermissionGranted;
    }

    /**
     * 写权限检测
     *
     * @param context
     * @return
     */
    public static boolean checkWriteStoragePermission(Object context) {

        if (context instanceof Activity) {
            return checkWriteStoragePermission((Activity) context);
        } else if (context instanceof Fragment) {
            return checkWriteStoragePermission((Fragment) context);
        } else {
            return false;
        }
    }

    /**
     * 相机权限检测
     *
     * @param fragment
     * @return
     */
    public static boolean checkCameraPermission(Fragment fragment) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(fragment.getContext(), CAMERA);

        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionGranted) {
            fragment.requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
        }
        return cameraPermissionGranted;
    }

    /**
     * 相机权限检测
     *
     * @param activity
     * @return
     */
    public static boolean checkCameraPermission(Activity activity) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(activity, CAMERA);

        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionGranted) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CAMERA, REQUEST_CAMERA);
        }
        return cameraPermissionGranted;
    }

    /**
     * 相机权限检测
     *
     * @param context
     * @return
     */
    public static boolean checkCameraPermission(Object context) {
        if (context instanceof Activity) {
            return checkCameraPermission((Activity) context);
        } else {
            return checkCameraPermission((Fragment) context);
        }
    }

    /**
     * 录音权限检测
     *
     * @param activity
     * @return
     */
    public static boolean checkRecordPermission(Activity activity) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(activity, RECORD_AUDIO);

        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionGranted) {
            ActivityCompat.requestPermissions(activity, PERMISSION_RECORD, REQUEST_RECORD);
        }
        return cameraPermissionGranted;
    }
}
