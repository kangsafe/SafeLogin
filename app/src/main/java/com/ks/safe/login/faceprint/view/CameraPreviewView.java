package com.ks.safe.login.faceprint.view;

/**
 * Created by Admin on 2017/10/25 0025 09:03.
 * Author: kang
 * Email: kangsafe@163.com
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Tianchaoxiong on 2017/1/16.
 */

public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final int FACENUM = 5;

    private static final String TAG = "yanzi";
    CameraInterface mCameraInterface;
    Context mContext;
    SurfaceHolder mSurfaceHolder;

    private boolean isFinish;
    //传输五个bitmap数组
    Bitmap[] bitmaps = new Bitmap[5];

    int number = 0;//作为计数器用

    OnFaceCollectListener onFaceCollectListener = null;

    public CameraPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceCreated...");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i(TAG, "surfaceChanged...");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i(TAG, "surfaceDestroyed...");
        CameraInterface.getInstance().doStopCamera();
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    /**
     * 收集五张图片 监听器
     */
    public static interface OnFaceCollectListener {

        /**
         * 收集五张face
         *
         * @param bitmaps 返回的五张脸的数组
         */
        void OnFaceCollected(Bitmap[] bitmaps);

        /**
         * face重新绘制
         *
         * @param isStart 是否重新收集
         */
        void OnFaceCollectStart(boolean isStart);
    }

    /**
     * 设置面部的监听器
     *
     * @param onFaceCollectListener
     */
    public void setOnFaceCollectListener(OnFaceCollectListener onFaceCollectListener) {

        if (onFaceCollectListener != null) {
            this.onFaceCollectListener = onFaceCollectListener;
        }
    }

    /***
     * 想在这里做一个监听处理 收五侦 传输出去
     * @param bytes
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (number < FACENUM) {
            //收集
            //判断监听器 开始
            if (onFaceCollectListener != null) {
                onFaceCollectListener.OnFaceCollectStart(true);
                //有byte数组转为bitmap
                bitmaps[number] = byte2bitmap(bytes, camera);
                Log.d("ceshiTian", "********收集了" + number + "个************");
                number = number + 1;
                if (number == 5) {
                    Log.d("ceshiTian", "********收集够5个************");
                    //提交
                    onFaceCollectListener.OnFaceCollected(bitmaps);
                }
            }
        } else {
            //不做操作
            onFaceCollectListener.OnFaceCollectStart(false);
            onFaceCollectListener.OnFaceCollected(null);
        }
    }

    private Bitmap byte2bitmap(byte[] bytes, Camera camera) {
        Bitmap bitmap = null;

        Camera.Size size = camera.getParameters().getPreviewSize(); // 获取预览大小
        final int w = size.width; // 宽度
        final int h = size.height;
        final YuvImage image = new YuvImage(bytes, ImageFormat.NV21, w, h,
                null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
        if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
            return null;
        }
        byte[] tmp = os.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

        Matrix matrix = new Matrix();
        matrix.setRotate(-90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return bitmap;
    }
}