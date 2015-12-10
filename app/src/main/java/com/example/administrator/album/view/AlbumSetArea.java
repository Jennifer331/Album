package com.example.administrator.album.view;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.administrator.album.animator.LHAnimator;

/**
 * Created by Lei Xiaoyue on 2015-11-17.
 */
public class AlbumSetArea extends LHItem {
    private static final String TAG = "ImageArea";
    private int mPosition;
    private Bitmap mSrc;
    private int mLeft;
    private int mTop;

    public AlbumSetArea(AlbumSetArea item) {
        this(item.getPosition(), item.getSrc(), item.getAnimators(), item.getPaint());
    }

    public AlbumSetArea(Bitmap src) {
        this(-1, src, null, null);
    }

    public AlbumSetArea(int position, Bitmap src, List<LHAnimator> animators, Paint paint) {
        mPosition = position;

        if (null != src) {
            mSrc = src;
        }

        if (null != animators) {
            this.animators = new ArrayList<LHAnimator>(animators);
        } else {
            this.animators = new ArrayList<LHAnimator>();
        }

        if (null != paint) {
            this.paint = new Paint(paint);
        } else {
            this.paint = new Paint();
        }
    }

    @Override
    public boolean draw(Canvas canvas) {
        boolean hasMoreFrame = false;
        if (null != mSrc) {
            if (null != animators && !animators.isEmpty()) {
                for (LHAnimator animator : animators) {
                    if (null != animator) {
                        hasMoreFrame |= animator.hasNextFrame(this);
                    }
                }
            }
            canvas.save();
            canvas.drawBitmap(mSrc, mLeft, mTop, paint);
            canvas.restore();
        }
        if (!hasMoreFrame && null != animators) {
            animators.clear();
        }
        return hasMoreFrame;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public float getWidth() {
        return mSrc.getWidth();
    }

    public float getHeight() {
        return mSrc.getHeight();
    }

    public Bitmap getSrc() {
        return mSrc;
    }

    public void setSrc(Bitmap src) {
        this.mSrc = src;
    }

    public boolean hasSrc() {
        return null == mSrc ? false : true;
    }

    public void setLeft(int left) {
        this.mLeft = left;
    }

    public void setTop(int top) {
        this.mTop = top;
    }

    public Rect getBound() {
        return new Rect(mLeft, mTop, (int) (mLeft + getWidth()), (int) (mTop + getHeight()));
    }
}
