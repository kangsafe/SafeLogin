package com.ks.safe.login.faceprint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.ks.safe.login.R;
import com.ks.safe.login.faceprint.util.CameraUtil;
import com.ks.safe.login.faceprint.util.FaceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 2017/6/21 0021 17:59.
 * Author: kang
 * Email: kangsafe@163.com
 */
public class CameraActivty extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private int mCameraId = 1;
    int degree = 0;
    //延迟时间
    private ImageView camera_close;
    // authid为6-18个字符长度，用于唯一标识用户
    private String mAuthid = "";
    private Toast mToast;
    // 进度对话框
    private ProgressDialog mProDialog;
    // FaceRequest对象，集成了人脸识别的各种功能
    private FaceRequest mFaceRequest;
    private boolean isreg = true;
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
        mAuthid = getIntent().getStringExtra("authid");
        isreg = getIntent().getBooleanExtra("isreg", true);
        // 在程序入口处传入appid，初始化SDK
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mProDialog = new ProgressDialog(this);
        mProDialog.setCancelable(true);
        mProDialog.setTitle("请稍后");

        mProDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // cancel进度框时,取消正在进行的操作
                if (null != mFaceRequest) {
                    mFaceRequest.cancel();
                }
                startCamera();
            }
        });


        degree = CameraUtil.getInstance().getCameraOrientation(mCameraId);

        mFaceRequest = new FaceRequest(this);
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Log.i("ch", "sss");
                detectCaptrue();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    public void startRegOrVerify(byte[] mImageData) {
        Log.i("Size", mImageData.length / 1024 + "KB");
        mProDialog.setMessage(isreg ? "注册中..." : "验证中");
        mProDialog.show();
        // 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
        // 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
        mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
        if (isreg) {
            mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
            mFaceRequest.setParameter("property", "del");
        } else {
            mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
        }
        int ret = mFaceRequest.sendRequest(mImageData, mRequestListener);
        if (ErrorCode.SUCCESS != ret) {
            mProDialog.dismiss();
            showTip("出现错误：" + ret);
        }
    }

    public void startDetect(byte[] mImageData) {
        mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
        mFaceRequest.setParameter(SpeechConstant.WFR_SST, "detect");
        mFaceRequest.sendRequest(mImageData, mRequestListener);
    }

    private void register(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            showTip("注册失败");
            return;
        }
        if ("success".equals(obj.get("rst"))) {
            showTip("注册成功");
            setResult();
        } else {
            showTip("注册失败");
        }
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra("isreg", isreg);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void verify(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            showTip("验证失败");
            return;
        }
        if ("success".equals(obj.get("rst"))) {
            if (obj.getBoolean("verf")) {
//                showTip("通过验证，欢迎回来！");
                setResult();
            } else {
                showTip("验证不通过");
            }
        } else {
            showTip("验证失败");
        }
    }

    private void detect(JSONObject obj) throws JSONException {
        int ret = obj.getInt("ret");
        if (ret != 0) {
            showTip("检测失败");
            return;
        }

        if ("success".equals(obj.get("rst"))) {
            captrue();
        } else {
            showTip("检测失败");
        }
    }

    private RequestListener mRequestListener = new RequestListener() {

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }
            try {
                String result = new String(buffer, "utf-8");
                Log.d("FaceDemo", result);

                JSONObject object = new JSONObject(result);
                String type = object.optString("sst");
                if ("reg".equals(type)) {
                    register(object);
                } else if ("verify".equals(type)) {
                    verify(object);
                } else if ("detect".equals(type)) {
                    detect(object);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (null != mProDialog) {
                mProDialog.dismiss();
            }

            if (error != null) {
                switch (error.getErrorCode()) {
                    case ErrorCode.MSP_ERROR_ALREADY_EXIST:
                        showTip("已经被注册，请更换后再试");
                        break;

                    default:
                        showTip(error.getPlainDescription(true));
                        break;
                }
            }
        }
    };

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);

        //关闭相机界面按钮
        camera_close = (ImageView) findViewById(R.id.camera_close);
        camera_close.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //退出相机界面 释放资源
            case R.id.camera_close:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    private void startCamera() {
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
        }
        if (mCamera != null && mHolder != null) {
            startPreview(mCamera, mHolder);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void finish() {
        if (null != mProDialog) {
            mProDialog.dismiss();
        }
        super.finish();
    }

    /**
     * 获取Camera实例
     *
     * @return
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }


    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            //亲测的一个方法 基本覆盖所有手机 将预览矫正
            CameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void detectCaptrue() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.i("Size", data.length / 1024 + "KB");
                    try {
                        Bitmap mImage;
                        // 获取图片的宽和高
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        mImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        // 压缩图片
                        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                                (double) options.outWidth / 1024f,
                                (double) options.outHeight / 1024f)));
                        options.inJustDecodeBounds = false;
                        mImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        // 部分手机会对图片做旋转，这里检测旋转角度
//                        int degree = FaceUtil.readPictureDegree(fileSrc);
                        Log.i("degree", degree + "");
                        if (degree != 0) {
                            // 把图片旋转为正的方向
                            mImage = FaceUtil.rotateImage(degree, mImage);
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //可根据流量及网络状况对图片进行压缩
                        mImage.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        byte[] mImageData = baos.toByteArray();
                        startDetect(mImageData);
//                        ((ImageView) findViewById(R.id.online_img)).setImageBitmap(mImage);
                        if (mImage != null) {
                            mImage.recycle();
                        }
                        if (baos != null) {
                            baos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        startCamera();
                    }
                }
            });
        }
    }

    private void captrue() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i("Size", data.length / 1024 + "KB");
                try {
                    Bitmap mImage;
                    // 获取图片的宽和高
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    mImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                    // 压缩图片
                    options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                            (double) options.outWidth / 1024f,
                            (double) options.outHeight / 1024f)));
                    options.inJustDecodeBounds = false;
                    mImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                    if (degree != 0) {
                        // 把图片旋转为正的方向
                        mImage = FaceUtil.rotateImage(degree, mImage);
                    }
//                    mImage = FaceUtil.rotateImage(90, mImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //可根据流量及网络状况对图片进行压缩
                    mImage.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] mImageData = baos.toByteArray();
                    startRegOrVerify(mImageData);
                    if (mImage != null) {
                        mImage.recycle();
                    }
                    if (baos != null) {
                        baos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    startCamera();
                }
            }
        });
    }

    /**
     * 设置
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

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
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }
}
