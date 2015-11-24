package com.example.administrator.album.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.Log;

import com.example.administrator.album.MyAnimator;

/**
 * Created by Lei Xiaoyue on 2015-11-17.
 */
public class ImageArea {
    private static final String TAG = "ImageArea";
    private int mPosition;
    private Bitmap mSrc;
    private Rect mSrcBound;
    private Rect mDestBound;
    private MyAnimator mAnimator;
    private Paint mBgPaint;

    // the following two fields have a strong relationship with mSrcBound,change
    // anyone,the other should be changed!
    private float displayXPortion = 1f;
    private float displayYPortion = 1f;

    public ImageArea(Bitmap src, Rect srcBound) {
        this(-1, src, srcBound, null);
    }

    public ImageArea(int position, Bitmap src, Rect srcBound, Rect destBound) {
        this(position, src, srcBound, destBound, null);
    }

    public ImageArea(ImageArea item) {
        this(item.getPosition(), item.getSrc(), item.getSrcBound(), item.getDestBound(),
                item.getAnimator());
    }

    public ImageArea(int position, Bitmap src, Rect srcBound, Rect destBound, MyAnimator animator) {
        mPosition = position;
        if (null != src) {
            mSrc = src;
        }
        Log.v(TAG,null == srcBound?"null rect" :srcBound.toString());
        if (null != srcBound) {
            mSrcBound = new Rect(srcBound);
            refreshPortion();
        }
        if (null != destBound) {
            mDestBound = new Rect(destBound);
        }
        if (null != animator) {
            mAnimator = animator;
        }
        init();
    }

    private void init() {
        mBgPaint = new Paint();
    }

    public boolean draw(Canvas canvas, boolean full) {
        if (full) {
            adjustDestBound();
        }
        if (null != mSrc) {
            {
                canvas.save();
                if (null != mAnimator) {
                    mBgPaint.setAlpha(mAnimator.getBgAlpha());
                    canvas.drawBitmap(mAnimator.getBackground(), 0, 0, mBgPaint);
                }
                canvas.drawBitmap(mSrc, mSrcBound, mDestBound, null);
                canvas.restore();
            }
            if (null != mAnimator) {
                while (mAnimator.hasNextFrame(this)) {
                    return true;
                }
                mAnimator = null;
            }
        }
        return false;
    }

    // when a srcBound is set,the displayXPortion and displayYPortion should be
    // changed!
    private void refreshPortion() {
        if (null != mSrc && null != mSrcBound) {
            displayXPortion = mSrcBound.width() / mSrc.getWidth();
            displayYPortion = mSrcBound.height() / mSrc.getHeight();
        }
    }

    // when a new src is set,the srcBound must be changed according to the XY
    // Portion
    private void refreshSrcBound() {
        if (null != mSrc && null != mSrcBound) {
            int width = (int) (mSrc.getWidth() * displayXPortion);
            int height = (int) (mSrc.getHeight() * displayYPortion);
            int offSetX = (mSrc.getWidth() - width) / 2;
            int offSetY = (mSrc.getHeight() - height) / 2;
            mSrcBound.left = offSetX;
            mSrcBound.right = mSrc.getWidth() - offSetX;
            mSrcBound.top = offSetY;
            mSrcBound.bottom = mSrc.getHeight() - offSetY;
        }
    }

    private void adjustDestBound() {
        if (null != mSrc && null != mDestBound) {
            float realRatio = (float) mDestBound.width() / (float) mDestBound.height();
            float targetRatio = (float) mSrc.getWidth() / (float) mSrc.getHeight();
            if (realRatio > targetRatio) {
                int width = (int) (targetRatio * mDestBound.height());
                int offsetX = (mDestBound.width() - width) / 2;
                mDestBound.left += offsetX;
                mDestBound.right -= offsetX;
            } else {
                int height = (int) (mDestBound.width() / targetRatio);
                int offsetY = (mDestBound.height() - height) / 2;
                mDestBound.top += offsetY;
                mDestBound.bottom -= offsetY;
            }
        }
    }

    public void setPortion(float xPortion, float yPortion) {
        this.displayXPortion = xPortion;
        this.displayYPortion = yPortion;
        int offsetX = (mSrc.getWidth() - (int) (mSrc.getWidth() * displayXPortion)) / 2;
        int offsetY = (mSrc.getHeight() - (int) (mSrc.getHeight() * displayYPortion)) / 2;
        if (null == mSrcBound) {
            mSrcBound = new Rect(offsetX, offsetY, mSrc.getWidth() - offsetX,
                    mSrc.getHeight() - offsetY);
        } else {
            mSrcBound.left = offsetX;
            mSrcBound.top = offsetY;
            mSrcBound.right = mSrc.getWidth() - offsetX;
            mSrcBound.bottom = mSrc.getHeight() - offsetY;
        }
    }

    @Override
    public String toString() {
        return "mPosition:" + mPosition + " X:" + mDestBound.left + " Y:" + mDestBound.top
                + " width:" + getWidth() + " height:" + getHeight() + " src "
                + (null == mSrc ? "is " : "is not") + " null";
    }

    public MyAnimator getAnimator() {
        return mAnimator;
    }

    public void setAnimator(MyAnimator Animator) {
        this.mAnimator = Animator;
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
        refreshSrcBound();
    }

    public Rect getSrcBound() {
        return mSrcBound;
    }

    public void setSrcBound(Rect mSrcBound) {
        this.mSrcBound = mSrcBound;
        refreshPortion();
    }

    public Rect getDestBound() {
        return mDestBound;
    }

    public void setDestBound(Rect mDestBound) {
        this.mDestBound = mDestBound;
    }

    public float getDisplayXPortion() {
        return displayXPortion;
    }

    public float getDisplayYPortion() {
        return displayYPortion;
    }
}
