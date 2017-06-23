package com.ks.safe.login.voiceprint.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Admin on 2017/6/23 0023 13:10.
 * Author: kang
 * Email: kangsafe@163.com
 */

public class WaterVoiceView extends View {
    private Paint paint;
    private int radius;
    private int alpha;
    private int width;

    public WaterVoiceView(Context context) {
        super(context);
        initPaint();
    }

    public WaterVoiceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public WaterVoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WaterVoiceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    /**
     * 初始化paint
     */
    private void initPaint() {
        alpha = 0;
        radius = 0;
        width = 2;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(width);
        // 设置是环形方式绘制
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(alpha);
        paint.setColor(Color.RED);
    }


    /**
     * 画出需要的图形的方法，这个方法比较关键
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getLeft() + getWidth() / 2, getTop() + getHeight() / 2, radius, paint);
    }
}
