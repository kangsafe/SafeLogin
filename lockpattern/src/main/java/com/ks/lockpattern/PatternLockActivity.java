package com.ks.lockpattern;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.lockpattern.view.PatternLockView;
import com.ks.lockpattern.view.PatternViewLintener;

/**
 * 图案解锁
 * Created by sgffsg on 17/4/24.
 */

public class PatternLockActivity extends AppCompatActivity{

    private PatternLockView patternLockView;
    private TextView tvTip;
    private String password;
    private String type;
    private SPUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock);
        patternLockView= (PatternLockView) findViewById(R.id.patternlockview);
        tvTip= (TextView) findViewById(R.id.tv_tip);
        spUtils=new SPUtils(this,"patternPSW");

        type=getIntent().getStringExtra("type");
        if ("open".equals(type)){
            String psw=spUtils.getString("password");
            if (!TextUtils.isEmpty(psw)&&psw.length()>3){
                patternLockView.setPassword(psw);
            }else {
                patternLockView.setIsSetting(true);
            }
        }else {
            patternLockView.setIsSetting(true);
        }
        tvTip.setText("请输入手势");
        patternLockView.setPatternViewListener(new PatternViewLintener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PatternLockActivity.this,"成功",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onSet(String psw) {
                if (!TextUtils.isEmpty(psw)){
                    if (TextUtils.isEmpty(password)){
                        password=psw;
                        tvTip.setText("再次输入手势");
                    }else {
                        if (password.equals(psw)){
                            tvTip.setText("设置成功");
                            spUtils.put("password",password);
                            onBackPressed();
                        }else {
                            password="";
                            tvTip.setText("两次手势不一致");
                        }
                    }
                }
            }

            @Override
            public void onError() {
                Toast.makeText(PatternLockActivity.this,"手势密码错误，还可以输入四次",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
