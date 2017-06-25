package com.ks.lockpattern.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * 图案手势解锁控件
 * Created by sgffsg on 17/4/24.
 */

public class PatternLockView extends View{

    //点颜色
    private static final int DOT_BLUE_COLOR = 0xffDFE0F1;
    //圆环颜色，也是点的第二种颜色，也是连接线的颜色
    private static final int CIRCLE_BLUE_COLOR = 0xff6C81FF;

    //点画笔
    private Paint dotPaint;
    //环画笔
    private Paint circlePaint;
    //路径画笔
    private Paint pathPaint;

    private RectF mBounds;
    //点半径
    private int dorRadius;
    //点平行距离
    private int dorDistance;
    //圆环宽
    private int circleStrokeWidth;
    //圆环半径
    private int circleRadius;
    //点领土半径
    private int areaWidth;
    //线宽
    private int lineWidth;

    //控件总宽度
    private int mWidth;
    //控件高度
    private int mHeight;

    private boolean mInputEnabled=true;
    private float currentX;//当前的位置
    private float currentY;
    private float stopX=-1;//停止的位置
    private float stopY=-1;

    private String password="";
    private String input="";
    private ArrayList<Dot> dots;
    private ArrayList<Dot> selectDots=new ArrayList<>();
    private PatternViewLintener patternLintener;
    private boolean isFirstHit=false;//第一次用户点击下的时候是否击中一个点，没有击中就没有后续动作

    private boolean mEnableHapticFeedback = true;//震动回馈
    private boolean isSetting=false;//默认是解锁状态，为true时是设置状态

    public PatternLockView(Context context) {
        this(context,null);
    }

    public PatternLockView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public PatternLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        dots=new ArrayList<>();
        dorRadius= (int) DisplayUtils.dpToPx(getContext(),10);
        circleStrokeWidth= (int) DisplayUtils.dpToPx(getContext(),2);
        circleRadius= (int) DisplayUtils.dpToPx(getContext(),30);
        areaWidth= (int) DisplayUtils.dpToPx(getContext(),34);
        lineWidth= (int) DisplayUtils.dpToPx(getContext(),2f);

        dotPaint=new Paint();
        dotPaint.setAntiAlias(true);
        dotPaint.setColor(DOT_BLUE_COLOR);

        circlePaint=new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setColor(CIRCLE_BLUE_COLOR);

        pathPaint=new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStrokeWidth(lineWidth);
        pathPaint.setColor(CIRCLE_BLUE_COLOR);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec); //获取高的尺寸
        int height ;

        //高度跟宽度处理方式一样
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height=widthSize;
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(widthMeasureSpec, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBounds=new RectF(getLeft(),getTop(),getRight(),getBottom());
        mWidth= (int) (mBounds.right-mBounds.left);
        mHeight= (int) (mBounds.bottom-mBounds.top);
        dorDistance=mWidth/4;
        for (int i=0;i<9;i++){
            dots.add(new Dot((i%3+1)*dorDistance,(i/3+1)*dorDistance));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (input.length()>0){
            for (int i=0;i<input.length();i++){
                Dot dot1=selectDots.get(i);
                if (i==input.length()-1){
                    if (stopX>0){
                        canvas.drawLine(dot1.getCenterX(),dot1.getCenterY(),stopX,stopY,pathPaint);
                    }
                }else {
                    Dot dot2=selectDots.get(i+1);
                    canvas.drawLine(dot1.getCenterX(),dot1.getCenterY(),dot2.getCenterX(),dot2.getCenterY(),pathPaint);
                }
            }
        }

        for (int i=0;i<9;i++){
            if (input.contains(""+i)){
                canvas.drawCircle((i%3+1)*dorDistance,(i/3+1)*dorDistance,circleRadius,circlePaint);
                circlePaint.setColor(Color.WHITE);
                canvas.drawCircle((i%3+1)*dorDistance, (i/3+1)*dorDistance, circleRadius-circleStrokeWidth, circlePaint);
                circlePaint.setColor(CIRCLE_BLUE_COLOR);
                canvas.drawCircle((i%3+1)*dorDistance,(i/3+1)*dorDistance,dorRadius,circlePaint);
            }else {
                canvas.drawCircle((i%3+1)*dorDistance,(i/3+1)*dorDistance,dorRadius,dotPaint);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mInputEnabled || !isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX=event.getX();
                currentY=event.getY();
                for (int i=0;i<9;i++){
                    Dot dot=dots.get(i);
                    if (dot.isTouchMe(currentX,currentY,areaWidth)){
                        if (input.length()==0){
                            if (mEnableHapticFeedback) {
                                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                                        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                                                | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            }
                            isFirstHit=true;
                            selectDots.add(dot);
                            input+=i;
                            invalidate();
                        }
                        break;
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                handleEventMove(event);
                return true;
            case MotionEvent.ACTION_UP:
                if (isSetting){
                    if (input!=null&&input.length()>3){//最少四个点
                        String result=input;
                        resetPatternView();
                        patternLintener.onSet(result);
                    }else {
                        resetPatternView();
                    }
                }else {
                    if (input!=null&&input.length()>0){
                        if (password.equals(input)){
                            patternLintener.onSuccess();
                        }else {
                            resetPatternView();
                            patternLintener.onError();
                        }
                    }
                }

                return true;
            case MotionEvent.ACTION_CANCEL:
                return true;
        }
        return false;
    }

    /**
     * 处理移动手势事件
     * @param event event
     */
    private void handleEventMove(MotionEvent event) {
        if (isFirstHit){//down时点击了一个点
            currentX=event.getX();
            currentY=event.getY();
            for (int i=0;i<9;i++){
                Dot dot=dots.get(i);
                if (dot.isTouchMe(currentX,currentY,areaWidth)){
                    if (!input.contains(""+i)){
                        selectDots.add(dot);
                        if (mEnableHapticFeedback) {
                            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                                    HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                                            | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        }
                        input+=i;
                        invalidate();
                    }
                    break;
                }else {
                    stopX=currentX;
                    stopY=currentY;
                    invalidate();
                }
            }
        }

    }


    /**
     * 设置图案控件监听器
     * @param listner listner
     */
    public void setPatternViewListener(PatternViewLintener listner){
        this.patternLintener=listner;
    }

    /**
     * 设置是否有震动回馈
     * @param mEnableHapticFeedback 是否
     */
    public void setmEnableHapticFeedback(boolean mEnableHapticFeedback) {
        this.mEnableHapticFeedback = mEnableHapticFeedback;
    }

    /**
     * 设置密码
     * @param pwd 密码
     */
    public void setPassword(String pwd){
        this.password=pwd;
    }

    /**
     * 设置手势锁状态，默认是解锁状态，为true时是设置状态
     * @param isSetting isSetting
     */
    public void setIsSetting(boolean isSetting) {
        this.isSetting = isSetting;
    }

    /**
     * 重置view
     */
    private void resetPatternView(){
        stopX=-1;
        stopY=-1;
        selectDots.clear();
        input="";
        isFirstHit=false;
        invalidate();
    }
}
