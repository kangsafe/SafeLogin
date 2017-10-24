package com.ks.safe.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.safe.login.faceprint.CameraActivty;
import com.ks.safe.login.faceprint.CameraBDActivty;
import com.ks.safe.login.fingerprint.FingerPrintDialog;
import com.ks.safe.login.fingerprint.FingerPrintUtil;
import com.ks.safe.login.fingerprint.FingerprintAlertDialog;
import com.ks.safe.login.hepai.HePaiLvActivity;
import com.ks.safe.login.lockpattern.PatternLockActivity;
import com.ks.safe.login.view.WaveDynamicAppBar;
import com.ks.safe.login.voiceprint.VoicePrintActivity;

import static com.ks.safe.login.voiceprint.VoicePrintActivity.PWD_TYPE_TEXT;

public class MainActivity extends AppCompatActivity {
    Switch vfinger;
    Switch vface;
    Switch vvoice;
    Switch vgen;
    Switch vface_bd;
    ImageView hepai;
    SharedPreferences sp;
    TextView fullTv;
    TextView singleTv;
    public static String FINGER = "finger";
    public static String FACE = "face";
    public static String VOICE = "voice";
    public static String GESTURE = "gesture";
    public static String SAFE_LOGIN_TYPE = "safe_login_type";
    public static final int REQUEST_FACE = 0;
    public static final int REQUEST_FACE_BD = 4;
    public static final int REQUEST_VOICE = 1;
    public static final int REQUEST_GESTURE = 3;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        vfinger = (Switch) findViewById(R.id.vfinger);
        vface = (Switch) findViewById(R.id.vface);
        vvoice = (Switch) findViewById(R.id.vvoice);
        vgen = (Switch) findViewById(R.id.vgensture);
        vface_bd = (Switch) findViewById(R.id.vface_bd);
        hepai = (ImageView) findViewById(R.id.hepai);
        fullTv = (TextView) findViewById(R.id.full);
        singleTv = (TextView) findViewById(R.id.letter);
        toolbar = (Toolbar) findViewById(R.id.third_activity_toolbar);
        setSupportActionBar(toolbar);
        final WaveDynamicAppBar bar = (WaveDynamicAppBar) findViewById(R.id.vappbar);
        bar.setMax(500);
        bar.setProgressSync(100);
        bar.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bar.stop();
            }
        }, 5000);
        findViewById(R.id.layout_ripple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) v.getParent()).removeView(v);
            }
        });
        vfinger.setChecked(sp.getBoolean(FINGER, false));
        vface.setChecked(sp.getBoolean(FACE, false));
        vvoice.setChecked(sp.getBoolean(VOICE, false));
        vgen.setChecked(sp.getBoolean(GESTURE, false));
        fullTv.setText("全拼：" + ChineseUtils.getFullPinYin("中华人民共和国"));
        singleTv.setText("简拼：" + ChineseUtils.getFirstPinYin("中华人民共和国"));
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
                if (isChecked) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, CameraActivty.class);
                    intent.putExtra("isreg", isChecked);
                    intent.putExtra("authid", "abc123");
                    startActivityForResult(intent, REQUEST_FACE);
                } else {
                    putSetting(FACE, false);
                }
            }
        });
        vvoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, VoicePrintActivity.class);
                    intent.putExtra("isreg", isChecked);
                    intent.putExtra("authid", "abc12345678");
                    intent.putExtra("text", "芝麻开门");
                    intent.putExtra("type", PWD_TYPE_TEXT);
                    startActivityForResult(intent, REQUEST_FACE);
                } else {
                    putSetting(VOICE, false);
                }
            }
        });

        vgen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, PatternLockActivity.class);
                    intent.putExtra("type", "setting");
                    startActivityForResult(intent, REQUEST_GESTURE);
                } else {
                    putSetting(VOICE, false);
                }
            }
        });
        vface_bd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, CameraBDActivty.class);
                    intent.putExtra("authid", "370101199901011234");
                    startActivityForResult(intent, REQUEST_FACE_BD);
                }
            }
        });

        //合拍率
        hepai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HePaiLvActivity.class));
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
        } else if (sp.getString(SAFE_LOGIN_TYPE, GESTURE).equals(GESTURE) && sp.getBoolean(GESTURE, false)) {
            Intent intent = new Intent();
            intent.setClass(this, PatternLockActivity.class);
            intent.putExtra("type", "open");
            startActivityForResult(intent, REQUEST_GESTURE);
        }
    }

    private void putSetting(String name, boolean val) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, val);
        editor.commit();
    }

    private void putSettingString(String name, String val) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, val);
        editor.commit();
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FACE:
                if (resultCode == RESULT_OK) {
                    //注册
                    if (data.getBooleanExtra("isreg", true)) {
                        putSetting(FACE, true);
                        putSettingString(SAFE_LOGIN_TYPE, FACE);
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
            case REQUEST_FACE_BD:
                if (resultCode == RESULT_OK) {
                    //注册
                    if (data.getBooleanExtra("isreg", true)) {
                        putSetting(FACE, true);
                        putSettingString(SAFE_LOGIN_TYPE, FACE);
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
            case REQUEST_VOICE:
                if (resultCode == RESULT_OK) {
                    //注册
                    if (data.getBooleanExtra("isreg", true)) {
                        putSetting(VOICE, true);
                        putSettingString(SAFE_LOGIN_TYPE, VOICE);
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
            case REQUEST_GESTURE:
                if (resultCode == RESULT_OK) {
                    //注册
                    if (data != null && data.getStringExtra("type").equals("setting")) {
                        putSetting(GESTURE, true);
                        putSettingString(SAFE_LOGIN_TYPE, GESTURE);
                    } else {
                        Toast.makeText(MainActivity.this, "欢迎回来", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
