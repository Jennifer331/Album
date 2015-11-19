package com.example.administrator.album.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Lei Xiaoyue on 2015-11-17.
 */
public class ImageArea {
    private int mPosition;
    private float mX;
    private float mY;
    private Bitmap mSrc;

    public ImageArea(int position,float x,float y,Bitmap src){
        mPosition = position;
        mX = x;
        mY = y;
        mSrc = src;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        this.mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        this.mY = y;
    }

    public Bitmap getSrc() {
        return mSrc;
    }

    public void setSrc(Bitmap src) {
        this.mSrc = src;
    }

    public boolean draw(Canvas canvas){
        if(null != mSrc) {
            canvas.drawBitmap(mSrc, mX, mY, null);
        }
        return true;
    }
}
