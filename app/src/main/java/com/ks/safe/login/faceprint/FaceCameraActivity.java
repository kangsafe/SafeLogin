package com.ks.safe.login.faceprint;

/**
 * Created by Admin on 2017/6/23 0023 10:06.
 * Author: kang
 * Email: kangsafe@163.com
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.safe.login.R;
import com.ks.safe.login.faceprint.view.CameraInterface;
import com.ks.safe.login.faceprint.view.CameraSurfaceView;

public class FaceCameraActivity extends AppCompatActivity implements CameraSurfaceView.OnFaceCollectListener, CameraInterface.CamOpenOverCallback, View.OnClickListener {
    private static final String TAG = "FaceCameraActivity";

    private ImageView camera_close;
    private CameraSurfaceView surfaceView = null;
    private int mCameraId = 1;
    private SurfaceHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_face);
        initView();
        surfaceView.setOnFaceCollectListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CameraInterface.getInstance().doOpenCamera(mCameraId, FaceCameraActivity.this);
    }

    /**
     * 回调方法
     * activity中的视图和 interface中的代码实现绑定
     */
    @Override
    public void cameraHasOpened() {
        mHolder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance().doStartPreview(this, mHolder, surfaceView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraInterface.getInstance().doStopCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
    }

    private void initView() {
        surfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        //关闭相机界面按钮
        camera_close = (ImageView) findViewById(R.id.camera_close);
        camera_close.setOnClickListener(this);
    }

    /**
     * 自建
     *
     * @param bitmaps 返回的五张脸的数组
     */
    @Override
    public void OnFaceCollected(Bitmap[] bitmaps) {

    }

    //每隔5帧传送一帧
    @Override
    public void OnFaceCollectedRate(byte[] data) {

    }

    /**
     * 自建
     *
     * @param isStart 是否重新收集
     */
    @Override
    public void OnFaceCollectStart(boolean isStart) {
        if (isStart) {
            Log.d("CameraActivity", "开始收集");
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}
