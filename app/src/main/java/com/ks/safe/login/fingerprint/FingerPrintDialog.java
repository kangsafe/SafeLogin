package com.ks.safe.login.fingerprint;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ks.safe.login.R;

import java.io.File;

/**
 * Created by Admin on 2017/6/21 0021 15:55.
 * Author: kang
 * Email: kangsafe@163.com
 */

public class FingerPrintDialog {
    private Dialog dialog;
    private ImageView logo;
    private ImageView print;
    private TextView tv;

    public FingerPrintDialog setListener(View.OnClickListener tvListener) {
        this.tvListener = tvListener;
        return this;
    }

    private View.OnClickListener tvListener;
    private Context context;
    private Display display;

    public FingerPrintDialog build(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        initView();
        return this;
    }

    public FingerPrintDialog setIcon(int resId) {
        logo.setImageResource(resId);
        return this;
    }

    public FingerPrintDialog setIcon(Bitmap bmp) {
        logo.setImageBitmap(bmp);
        return this;
    }

    public FingerPrintDialog setIcon(String path) {
        try {
            logo.setImageBitmap(BitmapFactory.decodeFile(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public FingerPrintDialog show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        } else {
            initView();
            dialog.show();
        }
        onFingerprintClick();
        return this;
    }

    private void initView() {
        LayoutInflater inflaterDl = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.layout_finger_print, null);
        logo = (ImageView) layout.findViewById(R.id.vlogo);
        print = (ImageView) layout.findViewById(R.id.print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFingerprintClick();
            }
        });
        tv = (TextView) layout.findViewById(R.id.other);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvListener != null) {
                    tvListener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        //对话框
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        layout.setLayoutParams(new FrameLayout.LayoutParams(display.getWidth(), display.getHeight()));

    }

    public void onFingerprintClick() {
        FingerPrintUtil.cancel();
        final FingerprintAlertDialog fingerprintAlertDialog = new FingerprintAlertDialog(context).builder();
        fingerprintAlertDialog.setIcon(R.mipmap.icon_finger_print);
        fingerprintAlertDialog.setCancelable(false);
        fingerprintAlertDialog.setNegativeButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerPrintUtil.cancel();
            }
        });
        FingerPrintUtil.callFingerPrint(context, new FingerPrintUtil.OnCallBackListenr() {
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
                fingerprintAlertDialog.setTitle("指纹解锁");
                fingerprintAlertDialog.setMsg("通过指纹键验证已有手机指纹");
                fingerprintAlertDialog.show();
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
//                showToast(errString.toString());
            }

            @Override
            public void onAuthenticationFailed() {
                fingerprintAlertDialog.setTitle("再试一次");
                fingerprintAlertDialog.setMsg("通过指纹键验证已有手机指纹");
                fingerprintAlertDialog.setPositiveButton("其他方式验证", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tvListener != null) {
                            tvListener.onClick(v);
                        }
                        fingerprintAlertDialog.dismiss();
                        dialog.dismiss();
                    }
                });
                fingerprintAlertDialog.show();
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                showToast("解锁成功");
                fingerprintAlertDialog.dismiss();
                dialog.dismiss();
            }
        });
    }

    public void showToast(String name) {
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
    }
}
