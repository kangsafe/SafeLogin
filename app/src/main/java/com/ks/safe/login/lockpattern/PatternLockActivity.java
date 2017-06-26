package com.ks.safe.login.lockpattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.safe.login.R;
import com.ks.safe.login.lockpattern.view.PatternLockView;
import com.ks.safe.login.lockpattern.view.PatternViewLintener;


/**
 * 图案解锁
 * Created by sgffsg on 17/4/24.
 */

public class PatternLockActivity extends AppCompatActivity implements Runnable {

    private PatternLockView patternLockView;
    private TextView tvTip;
    private String password;
    private String type;
    private SPUtils spUtils;
    private int num = 1;
    private int maxNum = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock);
        patternLockView = (PatternLockView) findViewById(R.id.patternlockview);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        spUtils = new SPUtils(this, "patternPSW");

        type = getIntent().getStringExtra("type");
        if ("open".equals(type)) {
            String psw = spUtils.getString("password");
            if (!TextUtils.isEmpty(psw) && psw.length() > 3) {
                patternLockView.setPassword(psw);
            } else {
                patternLockView.setIsSetting(true);
            }
        } else {
            patternLockView.setIsSetting(true);
        }
        tvTip.setText("请输入手势");
        patternLockView.setPatternViewListener(new PatternViewLintener() {
            @Override
            public void onSuccess() {
                if (num <= maxNum) {
                    onBackPressed();
                }
            }

            @Override
            public void onSet(String psw) {
                if (!TextUtils.isEmpty(psw)) {
                    if (TextUtils.isEmpty(password)) {
                        password = psw;
                        tvTip.setText("再次输入手势");
                    } else {
                        if (password.equals(psw)) {
                            tvTip.setText("设置成功");
                            spUtils.put("password", password);
                            onBackPressed();
                        } else {
                            password = "";
                            tvTip.setText("两次手势不一致");
                        }
                    }
                }
            }

            @Override
            public void onError() {
                if (num < maxNum) {
                    tvTip.setText("手势密码错误，还可以输入" + (maxNum - num) + "次");
                    num++;
                } else {
                    tvTip.setText("锁定30秒");
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            tvTip.setText("请输入手势");
//                            num = 1;
//                        }
//                    }, 30000);
//
                }
            }
        });
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        setResult(RESULT_OK, intent);
        finish();
    }

    int timer = 30;

    @Override
    public void run() {
        if (num >= maxNum) {
            tvTip.setText("锁定" + timer + "秒");
            timer--;
        } else {
            timer = 30;
        }
    }
}
