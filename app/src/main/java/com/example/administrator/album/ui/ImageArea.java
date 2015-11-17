package com.example.administrator.album.ui;

import android.graphics.Canvas;

/**
 * Created by Lei Xiaoyue on 2015-11-17.
 */
public class ImageArea {
    private int mPositionX;
    private int mPositionY;

    public int getPositionX() {
        return mPositionX;
    }

    public void setPositionX(int mPositionX) {
        this.mPositionX = mPositionX;
    }

    public int getPositionY() {
        return mPositionY;
    }

    public void setPositionY(int mPositionY) {
        this.mPositionY = mPositionY;
    }

    public boolean draw(Canvas canvas,int x,int y){
        return true;
    }
}
