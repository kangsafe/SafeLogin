package com.iflytek.isvdemo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 用户名输入页面
 *
 * @author iFlytek &nbsp;&nbsp;&nbsp;<a href="http://http://www.xfyun.cn/">讯飞语音云</a>
 */
public class LoginActivity extends Activity implements OnClickListener{
	private Toast mToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
	
		findViewById(R.id.btn_confirm).setOnClickListener(LoginActivity.this);
		mToast = Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			// 过滤掉不合法的用户名
			String uname = ((EditText) findViewById(R.id.edt_uname)).getText().toString();
			if (TextUtils.isEmpty(uname)) {
				showTip("用户名不能为空");
				return;
			} else {
				Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
				Matcher m = p.matcher(uname);
				if (m.find()) {
					showTip("不支持中文字符");
					return;
				} else if (uname.contains(" ")) {
					showTip("不能包含空格");
					return;
				} else if (!uname.matches("^[a-zA-Z][a-zA-Z0-9_]{5,17}")) {
					showTip("6-18个字母、数字或下划线的组合，以字母开头");
					return;
				} 
			}
			
			Intent intent = new Intent(LoginActivity.this, IsvDemo.class);
			intent.putExtra("uname", uname);
			startActivity(intent);
			break;
			
		default:
			break;
		}
	}	
	
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
	
}
