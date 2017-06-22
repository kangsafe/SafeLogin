package com.ks.safe.login.faceprint.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Created by Admin on 2017/6/22 0022 10:16.
 * Author: kang
 * Email: kangsafe@163.com
 */

public class CircleSurfaceView extends SurfaceView {

    public CircleSurfaceView(Context context) {
        super(context);
    }

    public CircleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void draw(Canvas canvas) {
        Log.e("onDraw", "draw: height:" + this.getHeight() + ",width:" + this.getWidth() + ",x:" + this.getX() + ",y:" + this.getY() + ",left:" + getLeft() + ",top:" + getTop());
//        Path path = new Path();
//        //用矩形表示SurfaceView宽高
//        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
//        //15.0f即是圆角半径
//        path.addRoundRect(rect, 15.0f, 15.0f, Path.Direction.CCW);
//        //裁剪画布，并设置其填充方式
//        canvas.clipPath(path, Region.Op.REPLACE);
        Path path = new Path();
        int r = this.getWidth() > this.getHeight() ? this.getHeight() / 2 : this.getWidth() / 2;

        //设置裁剪的圆心，半径
        path.addCircle(getLeft() + r, getTop() + r, r, Path.Direction.CCW);
        //裁剪画布，并设置其填充方式
        canvas.clipPath(path, Region.Op.REPLACE);
        super.draw(canvas);
    }
}
