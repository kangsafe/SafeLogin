package com.ks.lockpattern.view;

/**
 * 点类
 * Created by sgffsg on 17/4/24.
 */

public class Dot {

    private float centerX;
    private float centerY;

    public Dot(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    /**
     * 是否到达我这个点的区域
     * @param areaWidth 我的领土范围
     * @return 是否触摸到我
     */
    public boolean isTouchMe(float currentX,float currentY,float areaWidth){
        return (Math.abs(currentX-centerX)<areaWidth)&&(Math.abs(currentY-centerY)<areaWidth);
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }
}
