package com.ks.safe.login.voiceprint.view;

/**
 * Created by Admin on 2017/6/23 0023 13:18.
 * Author: kang
 * Email: kangsafe@163.com
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Description: 水波纹动画引导view
 * User: chenzheng
 * Date: 2017/1/14 0014
 * Time: 18:01
 */
public class RippleVoiceView extends RelativeLayout implements Runnable {
    private int mMaxRadius = 70;

    public void setProgress(int mInterval) {
        if (mInterval <= 0) {
            this.mInterval = 20;
        } else {
            this.mInterval = mInterval;
        }
    }

    private int mInterval = 20;
    private int count = 0;
    private Paint mRipplePaint;

    public void start() {
        this.isdraw = true;
        invalidate();
    }

    public void stop() {
        this.isdraw = false;
        invalidate();
    }

    private boolean isdraw = false;

    public RippleVoiceView(Context context) {
        this(context, null);
    }

    public RippleVoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleVoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setColor(Color.parseColor("#1296db"));
        mRipplePaint.setStrokeWidth(2.f);
    }

    /**
     * view大小变化时系统调用
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isdraw) {
            //获取加号图片view
            View mPlusChild = getChildAt(0);
            //获取加号图片大小
            final int pw = mPlusChild.getWidth();
            final int ph = mPlusChild.getHeight();
            if (pw == 0 || ph == 0) return;
            //加号图片中心点坐标
            final float px = mPlusChild.getX() + pw / 2;
            final float py = mPlusChild.getY() + ph / 2;
            final int rw = pw / 2;
            final int rh = ph / 2;
            //保存画布当前的状态
            int save = canvas.save();
            for (int step = count; step <= mMaxRadius; step += mInterval) {
                //step越大越靠外就越透明
                mRipplePaint.setAlpha(255 * (mMaxRadius - step) / mMaxRadius);
                canvas.drawCircle(px, py, (float) (rw + step), mRipplePaint);
            }
            //恢复Canvas的状态
            canvas.restoreToCount(save);
            //延迟80毫秒后开始运行
            postDelayed(this, 80);
        }
    }

    @Override
    public void run() {
        //把run对象的引用从队列里拿出来，这样，他就不会执行了，但 run 没有销毁
        removeCallbacks(this);
        count += 2;
        count %= mInterval;
        invalidate();//重绘
    }

    /**
     * 销毁view时调用，收尾工作
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
