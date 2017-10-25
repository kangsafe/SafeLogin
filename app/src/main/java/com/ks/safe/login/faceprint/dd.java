//package com.ks.safe.login.faceprint;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Point;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.ks.safe.login.R;
//import com.ks.safe.login.faceprint.view.CameraSurfaceView;
//
///**
// * Created by Admin on 2017/10/25 0025 09:07.
// * Author: kang
// * Email: kangsafe@163.com
// */
//
//public class CameraPreviewActivity extends Activity implements CameraSurfaceView.OnFaceCollectListener, CameraInterface.CamOpenOverCallback {
//    private static final String TAG = "CameraActivity";
//
//    private float recLen = 0;
//    long te;
//    float previewRate = -1f;
//    DetecteSDK detecteSDK;
//    DetecteSeeta detecteSeeta;
//
//    Bitmap bmp;
//    Bitmap bitmapfianl;
//
//    private ImageButton shutterBtn;
//    private TextView textView;
//    private CameraSurfaceView surfaceView = null;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Thread openThread = new Thread() {
//            @Override
//            public void run() {
//                CameraInterface.getInstance().doOpenCamera(CameraPreviewActivity.this);
//            }
//        };
//        openThread.start();
//        te = System.currentTimeMillis();
//        setContentView(R.layout.activity_camera);
//        initView();
//        initViewParams();
//        surfaceView.setOnFaceCollectListener(this);
//
//        shutterBtn.setOnClickListener(new BtnListeners());
//        TimeHandler.postDelayed(TimeRunnable, 500);
//    }
//
//    /**
//     * 回调方法
//     * activity中的视图和 interface中的代码实现绑定
//     */
//    @Override
//    public void cameraHasOpened() {
//        SurfaceHolder holder = surfaceView.getSurfaceHolder();
//        CameraInterface.getInstance().doStartPreview(holder, previewRate, surfaceView);
//    }
//
//    @Override
//    protected void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//        CameraInterface.getInstance().doStopCamera();
//        Log.d("RunTime", "onDestroy:time:" + te);
//        TimeHandler.removeCallbacks(TimeRunnable);
//    }
//
//    @Override
//    protected void onPause() {
//        // TODO Auto-generated method stub
//        super.onPause();
//        CameraInterface.getInstance().doStopCamera();
//        te = System.currentTimeMillis() - te;
//        Log.d("RunTime", "onPause:time:" + te);
//        TimeHandler.removeCallbacks(TimeRunnable);
//    }
//
//    Handler TimeHandler = new Handler();
//    Runnable TimeRunnable = new Runnable() {
//        @Override
//        public void run() {
//            recLen += 500;
//            textView.setText("检测时间：" + recLen / 1000 + "秒");
//            TimeHandler.postDelayed(this, 500);
//        }
//    };
//
//
//    private void initView() {
//        surfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
//        shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
//        textView = (TextView) findViewById(R.id.time);
//    }
//
//    private void initViewParams() {
//        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
//        Point p = DisplayUtil.getScreenMetrics(this);
//        params.width = p.x;
//        params.height = p.y;
//        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
//        surfaceView.setLayoutParams(params);
//        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
//        ViewGroup.LayoutParams p2 = shutterBtn.getLayoutParams();
//        p2.width = DisplayUtil.dip2px(this, 80);
//        p2.height = DisplayUtil.dip2px(this, 80);
//        shutterBtn.setLayoutParams(p2);
//
//    }
//
//
//    /**
//     * 自建
//     *
//     * @param bitmaps 返回的五张脸的数组
//     */
//    @Override
//    public void OnFaceCollected(Bitmap[] bitmaps) {
//
//    }
//
//    /**
//     * 自建
//     *
//     * @param isStart 是否重新收集
//     */
//    @Override
//    public void OnFaceCollectStart(boolean isStart) {
//        if (isStart) {
//            Log.d("CameraActivity", "开始收集");
//        }
//
//    }
//
//    private class BtnListeners implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            switch (v.getId()) {
//                case R.id.btn_shutter:
//                    CameraInterface.getInstance().doTakePicture();
//                    Intent intent = new Intent(CameraPreviewActivity.this, ShowPic.class);
//                    // 默认一个暂存的路径 /FaceDetection/useso/Pictures/Tmp/tmp.png
//                    String FolderPath = "/FaceDetection/useso/Pictures/Tmp/tmp.png";
//                    String path = Environment.getExternalStorageDirectory() + FolderPath;
//                    intent.putExtra("picpath", path);
//                    startActivity(intent);
//                    finish();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//}
