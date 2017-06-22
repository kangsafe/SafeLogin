package com.ks.safe.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.ks.safe.login.faceprint.CameraActivty;
import com.ks.safe.login.fingerprint.FingerPrintDialog;
import com.ks.safe.login.fingerprint.FingerPrintUtil;
import com.ks.safe.login.fingerprint.FingerprintAlertDialog;

public class MainActivity extends AppCompatActivity {
    Switch vfinger;
    Switch vface;
    Switch vvoice;
    Switch vgen;
    SharedPreferences sp;
    public static String FINGER = "finger";
    public static String FACE = "face";
    public static String VOICE = "voice";
    public static String GENITUE = "genisture";
    public static String SAFE_LOGIN_TYPE = "safe_login_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        vfinger = (Switch) findViewById(R.id.vfinger);
        vface = (Switch) findViewById(R.id.vface);
        vvoice = (Switch) findViewById(R.id.vvoice);
        vgen = (Switch) findViewById(R.id.vgensture);
        vfinger.setChecked(sp.getBoolean(FINGER, false));
        vface.setChecked(sp.getBoolean(FACE, false));
        vvoice.setChecked(sp.getBoolean(VOICE, false));
        vgen.setChecked(sp.getBoolean(GENITUE, false));
        vfinger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                putSetting(FINGER, isChecked);
                if (isChecked) {
                    FingerPrintUtil.callFingerPrint(MainActivity.this, new FingerPrintUtil.OnCallBackListenr() {
                        @Override
                        public void onSupportFailed() {
                            showMsg("您的手机咱不支持指纹");
                        }

                        @Override
                        public void onInsecurity() {

                        }

                        @Override
                        public void onEnrollFailed() {

                        }

                        @Override
                        public void onAuthenticationStart() {

                        }

                        @Override
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {

                        }

                        @Override
                        public void onAuthenticationFailed() {

                        }

                        @Override
                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

                        }

                        @Override
                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {

                        }
                    });
                }
            }
        });
        vface.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                putSetting(FACE, isChecked);
                if (isChecked) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, CameraActivty.class);
                    intent.putExtra("isreg", isChecked);
                    intent.putExtra("authid", "abc123");
                    startActivityForResult(intent, REQUEST_FACE);
                }
            }
        });
        //指纹识别
        if (sp.getString(SAFE_LOGIN_TYPE, FINGER).equals(FINGER) && sp.getBoolean(FINGER, false)) {
            new FingerPrintDialog().build(this).setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }).show();
        } else if (sp.getString(SAFE_LOGIN_TYPE, FACE).equals(FACE) && sp.getBoolean(FACE, false)) {
            Intent intent = new Intent();
            intent.setClass(this, CameraActivty.class);
            intent.putExtra("isreg", false);
            intent.putExtra("authid", "abc123");
            startActivityForResult(intent, REQUEST_FACE);
        }
    }

    private void putSetting(String name, boolean val) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, val);
        editor.commit();
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public static final int REQUEST_FACE = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FACE:
                if (resultCode == RESULT_OK) {
                    //注册
                    if (data.getBooleanExtra("isreg", true)) {

                    } else {
                        new FingerprintAlertDialog(this)
                                .builder()
                                .setTitle("刷脸成功")
                                .setMsg("欢迎主人，宝宝给你请安了^_^")
                                .setCancelable(false)
                                .setNegativeButton("我知道了", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .show();
                    }
                }
                break;
        }
    }
}
