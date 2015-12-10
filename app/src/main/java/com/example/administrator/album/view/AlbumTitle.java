package com.example.administrator.album.view;

import android.graphics.Canvas;
import android.graphics.Color;

import com.example.administrator.album.animator.LHAnimator;

/**
 * Created by Lei Xiaoyue on 2015-12-07.
 */
public class AlbumTitle extends LHItem {
    private static final int DEFAULT_TEXT_SIZE = 30;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private float mX;
    private float mY;
    private float mWidth;
    private float mHeight;
    private String mTitle;
    private boolean mAlignCenterFlag;

    public AlbumTitle(float x, float y, float width, float height, String title, boolean center) {
        this(x, y, width, height, title, center, DEFAULT_TEXT_COLOR, DEFAULT_TEXT_SIZE);
    }

    public AlbumTitle(float x, float y, float width, float height, String title, boolean center, int color, float size) {
        super();
        this.mX = x;
        this.mY = y;
        this.mWidth = width;
        this.mHeight = height;
        this.mTitle = title;
        this.mAlignCenterFlag = center;
        this.paint.setColor(color);
        this.paint.setTextSize(size);
    }

    @Override
    public boolean draw(Canvas canvas) {
        boolean hasMoreFrame = false;
        if (null != mTitle && null != paint) {
            if(null != animators && !animators.isEmpty()){
                for(LHAnimator animator : animators){
                    if(null != animator) {
                        hasMoreFrame |= animator.hasNextFrame(this);
                    }
                }
            }
            if (mAlignCenterFlag) {
                float realWidth = paint.measureText(mTitle);
                float realHeight = paint.descent() - paint.ascent();
                float deltaX = mWidth - realWidth;
                float deltaY = mHeight - realHeight;
                canvas.drawText(mTitle, mX + deltaX / 2, mY + deltaY / 2, paint);
            } else {
                canvas.drawText(mTitle, mX, mY, paint);
            }
        }
        if(!hasMoreFrame && null != animators){
            animators.clear();
        }
        return hasMoreFrame;
    }
}
