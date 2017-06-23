package com.ks.safe.login.faceprint.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.ks.safe.login.faceprint.util.CameraUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tianchaoxiong on 2017/1/16.
 */

public class CameraInterface {
    private static final String TAG = "CameraInterface";
    private Camera mCamera;
    private boolean isPreviewing = false;
    private static CameraInterface mCameraInterface;
    private int mCameraId = 1;

    public interface CamOpenOverCallback {
        void cameraHasOpened();
    }

    private CameraInterface() {

    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * 打开Camera
     *
     * @param callback
     */
    public void doOpenCamera(int mCameraId, CamOpenOverCallback callback) {
        this.mCameraId = mCameraId;
        Log.i(TAG, "Camera open....");
        mCamera = Camera.open(mCameraId);
        Log.i(TAG, "Camera open over....");
        callback.cameraHasOpened();
    }

    /**
     * 开启预览
     *
     * @param holder
     */
    public void doStartPreview(Activity activty, SurfaceHolder holder, Camera.PreviewCallback previewCallback) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            setupCameraParameters(mCamera);
            try {
                //亲测的一个方法 基本覆盖所有手机 将预览矫正
                CameraUtil.getInstance().setCameraDisplayOrientation(activty, mCameraId, mCamera);
                mCamera.setPreviewDisplay(holder);
                mCamera.setPreviewCallback(previewCallback);
                mCamera.startPreview();//开启预览
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPreviewing = true;
        }
    }

    /**
     * 设置
     */
    private void setupCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
        //设置聚焦模式
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPreviewSizes(), 800);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictrueSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPictureSizes(), 800);
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);

        camera.setParameters(parameters);
    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     */
    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            Log.d("FileUtils", "程序运行到这里了111");
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    /*  Camera.PictureCallback mRawCallback = new Camera.PictureCallback()
          // 拍摄的未压缩原数据的回调,可以为null
      {

        public void onPictureTaken(byte[] data, Camera camera) {
          Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
      };*/
    //对jpeg图像数据的回调,最重要的一个回调
    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("FileUtils", "程序运行到这里了222");
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
                Log.d("FileUtils", "程序运行到这里了333");
            }
            //保存图片到sdcard
            if (null != b) {
                Log.d("FileUtils", "程序运行到这里了444");
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
                Log.d("FileUtils", "程序运行到这里了");
//                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, -90.0f);
//                FileUtils.savePaiZhaoBitmap(rotaBitmap);
            }
        }
    };

}