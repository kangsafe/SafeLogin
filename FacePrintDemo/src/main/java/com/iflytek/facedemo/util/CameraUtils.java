package com.iflytek.facedemo.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by Admin on 2017/6/21 0021 09:43.
 * Author: kang
 * Email: kangsafe@163.com
 */

public class CameraUtils {

    public static File startCamera(Activity context, int REQUEST_CAMERA_IMAGE) {
        File mPictureFile = null;
        if (PermUtils.checkWriteStoragePermission(context)) {
            // 设置相机拍照后照片保存路径
            mPictureFile = new File(Environment.getExternalStorageDirectory(),
                    "/msc/picture" + System.currentTimeMillis() / 1000 + ".jpg");
            if (!mPictureFile.exists()) {
                if (!mPictureFile.getParentFile().exists()) {
                    mPictureFile.getParentFile().mkdirs();
                }
                try {
                    mPictureFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (PermUtils.checkCameraPermission(context)) {
            // 启动拍照,并保存到临时文件
            Intent mIntent = new Intent();
            mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            mIntent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(context, mPictureFile));
            context.startActivityForResult(mIntent, REQUEST_CAMERA_IMAGE);
        }
        return mPictureFile;
    }

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
