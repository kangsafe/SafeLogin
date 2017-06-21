package com.iflytek.isvdemo;

import com.iflytek.cloud.SpeechUtility;

import android.app.Application;

public class DemoApp extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
		// 设置你申请的应用appid
		SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
	}
}
