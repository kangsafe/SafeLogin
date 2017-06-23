package com.ks.safe.login.view;

/**
 * Created by Administrator on 2017/6/23.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

import com.ks.safe.login.R;

/**
 * Created by liruiyuan on 2015/12/21.
 */
public class WaveDynamicAppBar extends AppBarLayout {
    /**
     * 进度条最大值和当前进度值
     */
    private float max = 100, progress = 0;
    //组件的宽，高
    private int width, height;

    // y = Asin(wx+b)+h
    private static float STRETCH_FACTOR_A = 20;
    private static final int OFFSET_Y = -50;
    // 第一条水波移动速度
    private static int TRANSLATE_X_SPEED_ONE = 7;
    // 第二条水波移动速度
    private static int TRANSLATE_X_SPEED_TWO = 5;
    private float mCycleFactorW;

    private int mTotalWidth, mTotalHeight;
    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOneOffset;
    private int mXTwoOffset;

    private Paint mWavePaint;
    private DrawFilter mDrawFilter;

    public WaveDynamicAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveDynamicAppBarAttr);
        int taColor = ta.getColor(R.styleable.WaveDynamicAppBarAttr_waveColor, Color.parseColor("#1296db"));
        STRETCH_FACTOR_A = ta.getDimension(R.styleable.WaveDynamicAppBarAttr_waveHeight, 20);
        TRANSLATE_X_SPEED_ONE = ta.getDimensionPixelSize(R.styleable.WaveDynamicAppBarAttr_waveSpeed1, 7);
        TRANSLATE_X_SPEED_TWO = ta.getDimensionPixelSize(R.styleable.WaveDynamicAppBarAttr_waveSpeed2, 5);
        ta.recycle();
        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = TRANSLATE_X_SPEED_ONE;//Util.dipToPx(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = TRANSLATE_X_SPEED_TWO;//Util.dipToPx(context, TRANSLATE_X_SPEED_TWO);

        // 初始绘制波纹的画笔
        mWavePaint = new Paint();
        // 去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        // 设置风格为实线
        mWavePaint.setStyle(Paint.Style.FILL);
        // 设置画笔颜色
        mWavePaint.setColor(taColor);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (width == 0 || height == 0) {
            width = getWidth();
            height = getHeight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 从canvas层面去除绘制时锯齿
        canvas.setDrawFilter(mDrawFilter);
        resetPositonY();
        for (int i = 0; i < mTotalWidth; i++) {
            // 减400只是为了控制波纹绘制的y在屏幕的位置，大家可以改成一个变量，然后动态改变这个变量，从而形成波纹上升下降效果
            // 绘制第一条水波纹
            canvas.drawLine(i, mTotalHeight - mResetOneYPositions[i] - height * progress / max, i, mTotalHeight, mWavePaint);

            // 绘制第二条水波纹
            canvas.drawLine(i, mTotalHeight - mResetTwoYPositions[i] - height * progress / max, i, mTotalHeight, mWavePaint);
        }

        // 改变两条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;

        // 如果已经移动到结尾处，则重头记录
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }

        // 引发view重绘，一般可以考虑延迟20-30ms重绘，空出时间片
        postInvalidate();
    }

    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOneOffset;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoYPositions, 0, yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下view的宽高
        mTotalWidth = w;
        mTotalHeight = h;
        // 用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];
        // 用于保存波纹一的y值
        mResetOneYPositions = new float[mTotalWidth];
        // 用于保存波纹二的y值
        mResetTwoYPositions = new float[mTotalWidth];

        // 将周期定为view总宽度
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
    }

    public void setMax(int max) {
        this.max = max;
//        invalidate();
    }

    //直接设置进度值（同步）
    public void setProgressSync(float progress) {
        this.progress = progress;
//        invalidate();
    }

}