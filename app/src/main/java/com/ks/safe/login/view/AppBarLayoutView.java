package com.ks.safe.login.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

/**
 * Description: 水波纹动画引导view
 * User: chenzheng
 * Date: 2017/1/14 0014
 * Time: 18:01
 */

/**
 * 水波进度效果.
 */
public class AppBarLayoutView extends AppBarLayout {
    //组件的宽，高
    private int width, height;
    /**
     * 进度条最大值和当前进度值
     */
    private float max, progress;
    /**
     * 绘制波浪的画笔
     */
    private Paint progressPaint;

    public AppBarLayoutView(Context context) {
        super(context);
        init();
    }

    public AppBarLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //初始化
    private void init() {
        progressPaint = new Paint();
        progressPaint.setColor(Color.parseColor("#77ff0000"));
        progressPaint.setAntiAlias(true);
        autoRefresh();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (width == 0 || height == 0) {
            width = getWidth();
            height = getHeight();
        }
    }

    //x方向偏移量
    private int xOffset;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制水波曲线
        canvas.drawPath(getFristWavePath(xOffset), progressPaint);
    }

    //绘制水波的路径
    private Path wavePath;
    int waveWidth = 200;//水波个数
    int waveHeight = 80;//

    /**
     * 使用路径描绘绘制的区域     *
     * * @return
     */
    private Path getFristWavePath(int xOffset) {        // 绘制区域1的路径
        if (wavePath == null) {
            wavePath = new Path();
        } else {
            wavePath.reset();
        }
        int hh = (int) (height * (1 - progress / max));
        wavePath.moveTo(0, hh);// 移动到左下角的点
        for (int i = 1; i <= width / waveWidth; i++) {
            if (i % 2 == 0) {
                wavePath.quadTo(xOffset + (waveWidth / 2) * (i + 1), hh + waveHeight,
                        xOffset + waveWidth * (i + 1), hh);
            } else {
                wavePath.quadTo(xOffset + (waveWidth / 2) * i, hh - waveHeight,
                        xOffset + waveWidth * i, hh);
            }
        }
        wavePath.lineTo(width, hh);
        wavePath.lineTo(width, height);
        wavePath.lineTo(0, height);
        return wavePath;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    //直接设置进度值（同步）
    public void setProgressSync(float progress) {
        this.progress = progress;
        invalidate();
    }

    /**
     * 自动刷新页面，创造水波效果。组件销毁后该线城将自动停止。
     */
    private void autoRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!detached) {
                    xOffset += (waveWidth >> 4);
                    if (xOffset > waveWidth * 4) {
                        xOffset = 0;
                    }
                    SystemClock.sleep(100);
                    postInvalidate();
                }
            }
        }).start();
    }

    //标记View是否已经销毁
    private boolean detached = false;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detached = true;
    }
}