package com.ks.safe.login.faceprint.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Tianchaoxiong on 2017/1/16.
 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "CameraSurfaceView";
    //预览帧数
    private static final int FACENUM = 5;
    Context mContext;
    SurfaceHolder mSurfaceHolder;

    //传输五个bitmap数组
    Bitmap[] bitmaps = new Bitmap[5];

    int number = 0;//作为计数器用

    OnFaceCollectListener onFaceCollectListener = null;

    private void init(Context context) {
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    public CameraSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


//    @Override
//    public void draw(Canvas canvas) {
//        Log.e("onDraw", "draw: height:" + this.getHeight() + ",width:" + this.getWidth() + ",x:" + this.getX() + ",y:" + this.getY() + ",left:" + getLeft() + ",top:" + getTop());
//        Path path = new Path();
//        int r = this.getWidth() > this.getHeight() ? this.getHeight() / 2 : this.getWidth() / 2;
//
//        //设置裁剪的圆心，半径
//        path.addCircle(getLeft() + r, getTop() + r, r, Path.Direction.CCW);
//        //裁剪画布，并设置其填充方式
//        canvas.clipPath(path, Region.Op.REPLACE);
//        super.draw(canvas);
//    }

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
    public interface OnFaceCollectListener {

        /**
         * 收集五张face
         *
         * @param bitmaps 返回的五张脸的数组
         */
        void OnFaceCollected(Bitmap[] bitmaps);

        void OnFaceCollectedRate(byte[] data);

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
                    onFaceCollectListener.OnFaceCollectedRate(bytes);
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