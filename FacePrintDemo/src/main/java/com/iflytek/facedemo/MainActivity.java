package com.iflytek.facedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.iflytek.cloud.Setting;
import com.iflytek.facedemo.util.SettingUtil;

/**
 * 人脸识别示例
 */
public class MainActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn_online_demo).setOnClickListener(MainActivity.this);
        findViewById(R.id.btn_offline_demo).setOnClickListener(MainActivity.this);
        findViewById(R.id.btn_video_demo).setOnClickListener(MainActivity.this);
        Log.i("UUID", SettingUtil.getString(this, "com.ks.safe.login.DM"));
        Setting.setShowLog(true);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_online_demo:
                intent = new Intent(MainActivity.this, OnlineFaceDemo.class);
                startActivity(intent);
                break;
            case R.id.btn_offline_demo:
                intent = new Intent(MainActivity.this, OfflineFaceDemo.class);
                startActivity(intent);
                break;
            case R.id.btn_video_demo:
                intent = new Intent(MainActivity.this, VideoDemo.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
