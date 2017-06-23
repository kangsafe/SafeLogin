package com.ks.safe.login.voiceprint;

import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import com.ks.safe.login.R;
import com.ks.safe.login.faceprint.util.PermUtils;
import com.ks.safe.login.voiceprint.view.CircleProgressView;
import com.ks.safe.login.voiceprint.view.RippleVoiceView;

public class VoicePrintActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, InitListener {
    TextView vclose;
    TextView vtext;
    ImageView vvoice;
    CircleProgressView vpbar;
    RippleVoiceView rippleIntroView;
    private String text;
    private String mauthid;
    private boolean isreg;
    public static final int PWD_TYPE_TEXT = 1;
    // 自由说由于效果问题，暂不开放
    public static final int PWD_TYPE_FREE = 2;
    public static final int PWD_TYPE_NUM = 3;
    // 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
    private int mPwdType = PWD_TYPE_TEXT;
    // 声纹识别对象
    private SpeakerVerifier mVerifier;
    private Toast mToast;
    // 数字声纹密码段，默认有5段
    private String[] mNumPwdSegs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPwdType = getIntent().getIntExtra("type", PWD_TYPE_TEXT);
        text = getIntent().getStringExtra("text");
        mauthid = getIntent().getStringExtra("authid");
        isreg = getIntent().getBooleanExtra("isreg", true);
        setContentView(R.layout.activity_voice_print);
        initView();
        initParam();
    }

    private void initParam() {
        if (PermUtils.checkWriteStoragePermission(this) && PermUtils.checkRecordPermission(this)) {
            if (isreg) {
                // 清空参数
                mVerifier.setParameter(SpeechConstant.PARAMS, null);
                mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");
                // 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
                mVerifier.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
                mVerifier.setParameter(SpeechConstant.ISV_PWD, text);
                // 设置auth_id，不能设置为空
                mVerifier.setParameter(SpeechConstant.AUTH_ID, mauthid);
                // 设置业务类型为注册
                mVerifier.setParameter(SpeechConstant.ISV_SST, "train");
                // 设置声纹密码类型
                mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
            } else {
                // 清空参数
                mVerifier.setParameter(SpeechConstant.PARAMS, null);
                mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
                mVerifier = SpeakerVerifier.getVerifier();
                // 设置业务类型为验证
                mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
                // 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
                mVerifier.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
                mVerifier.setParameter(SpeechConstant.ISV_PWD, text);
                // 设置auth_id，不能设置为空
                mVerifier.setParameter(SpeechConstant.AUTH_ID, mauthid);
                mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
            }
        }
    }

    private void initView() {
        vpbar = (CircleProgressView) findViewById(R.id.pbar);
        vclose = (TextView) findViewById(R.id.voice_close);
        vclose.setOnClickListener(this);
        vtext = (TextView) findViewById(R.id.voice_txt);
        vvoice = (ImageView) findViewById(R.id.vvoice);
//        vvoice.setOnTouchListener(this);
        vtext.setText(text);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);

        // 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
        mVerifier = SpeakerVerifier.createVerifier(this, this);
        vpbar.setMaxProgress(5);
        vpbar.setProgress(0);
        if (!isreg) {
            vpbar.setVisibility(View.GONE);
        }
        rippleIntroView = (RippleVoiceView) findViewById(R.id.layout_ripple);
        rippleIntroView.setOnTouchListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voice_close:
                finish();
                break;
        }
    }

    private VerifierListener mVerifyListener = new VerifierListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            showTip("当前正在说话，音量大小：" + volume);
//            Log.d("TAG", "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(VerifierResult result) {
            if (result.ret == 0) {
                // 验证通过
                showTip("验证通过");
            } else {
                // 验证不通过
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        showTip("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        showTip("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        showTip("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        showTip("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        showTip("验证不通过，您所读的文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        showTip("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        showTip("音频长达不到自由说的要求");
                        break;
                    default:
                        showTip("验证不通过");
                        break;
                }
            }
        }

        // 保留方法，暂不用
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

        @Override
        public void onError(SpeechError error) {

            switch (error.getErrorCode()) {
                case ErrorCode.MSP_ERROR_NOT_FOUND:
                    showTip("模型不存在，请先注册");
                    break;

                default:
                    showTip("onError Code：" + error.getPlainDescription(true));
                    break;
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }
    };

    private VerifierListener mRegisterListener = new VerifierListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            rippleIntroView.setProgress(volume);
//            showTip("当前正在说话，音量大小：" + volume);
//            Log.d("TAG", "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(VerifierResult result) {
            if (result.ret == ErrorCode.SUCCESS) {
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        showTip("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
                        showTip("训练达到最大次数");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        showTip("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        showTip("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        showTip("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        showTip("训练失败，您所读的文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        showTip("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        showTip("音频长达不到自由说的要求");
                    default:
//                        mShowRegFbkTextView.setText("");
                        break;
                }

                if (result.suc == result.rgn) {
                    showTip("注册成功");
                    if (PWD_TYPE_TEXT == mPwdType) {
                        showTip("您的文本密码声纹ID：\n" + result.vid);
                    } else if (PWD_TYPE_NUM == mPwdType) {
                        showTip("您的数字密码声纹ID：\n" + result.vid);
                    }
                } else {
                    int nowTimes = result.suc + 1;
                    int leftTimes = result.rgn - nowTimes;

                    vpbar.setProgress(nowTimes);
                    vpbar.setmTxtHint1("训练 第" + nowTimes + "遍");
                    vpbar.setmTxtHint2("剩余" + leftTimes + "遍");
                }
            } else {
                showTip("注册失败，请重新开始。");
            }
        }

        // 保留方法，暂不用
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
//            	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//            		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//            		Log.d(TAG, "session id =" + sid);
//            	}
        }

        @Override
        public void onError(SpeechError error) {
            if (error.getErrorCode() == ErrorCode.MSP_ERROR_ALREADY_EXIST) {
                showTip("模型已存在，如需重新注册，请先删除");
            } else {
                showTip("onError Code：" + error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
//            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
//            showTip("开始说话");
        }
    };

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (null != mVerifier) {
            mVerifier.stopListening();
            mVerifier.destroy();
        }
        super.onDestroy();
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    private boolean checkInstance() {
        if (null == mVerifier) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip("创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isreg) {
                    // 开始注册
                    mVerifier.startListening(mRegisterListener);

                } else {
                    // 开始验证
                    mVerifier.startListening(mVerifyListener);
                }
                rippleIntroView.start();
                break;
            case MotionEvent.ACTION_UP:
                mVerifier.stopListening();
                rippleIntroView.stop();
                break;
        }
        return true;
    }

    @Override
    public void onInit(int errorCode) {
        if (ErrorCode.SUCCESS == errorCode) {
//            showTip("引擎初始化成功");
            vvoice.setClickable(true);
        } else {
            showTip("引擎初始化失败，错误码：" + errorCode);
        }
    }
}
