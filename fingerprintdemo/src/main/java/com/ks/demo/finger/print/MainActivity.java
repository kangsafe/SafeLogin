package com.ks.demo.finger.print;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageView print;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        print = (ImageView) findViewById(R.id.print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFingerprintClick();
            }
        });
    }

    private void showDialog() {
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_fingerprint, null);
            initView(view);
            builder.setView(view);
            builder.setCancelable(false);
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FingerPrintUtil.cancel();
                }
            });
            builder.setPositiveButton("验证其他方式", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog = builder.create();
        }
        dialog.show();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        onFingerprintClick();
    }

    public void onFingerprintClick() {
        FingerPrintUtil.callFingerPrint(this, new FingerPrintUtil.OnCallBackListenr() {
            @Override
            public void onSupportFailed() {
                showToast("当前设备不支持指纹");
            }

            @Override
            public void onInsecurity() {
                showToast("当前设备未处于安全保护中");
            }

            @Override
            public void onEnrollFailed() {
                showToast("请到设置中设置指纹");
            }

            @Override
            public void onAuthenticationStart() {
                showDialog();
                tv1.setText("“xxx”的指纹解锁");
                tv2.setText("通过指纹键验证已有手机指纹");
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                showToast(errString.toString());
                hideDialog();
            }

            @Override
            public void onAuthenticationFailed() {
                tv1.setText("再试一次");
                tv2.setText("通过指纹键验证已有手机指纹");
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                showToast(helpString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                showToast("解锁成功");
                hideDialog();
            }
        });
    }

    TextView tv1;
    TextView tv2;

    private void initView(View view) {
        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv2 = (TextView) view.findViewById(R.id.tv2);
    }

    public void showToast(String name) {
        Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
    }
}
