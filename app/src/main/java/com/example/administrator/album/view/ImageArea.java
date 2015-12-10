package com.example.administrator.album.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.administrator.album.animator.LHAnimator;
import com.example.administrator.album.animator.ScaleAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-17.
 */
public class ImageArea extends LHItem {
    private static final String TAG = "ImageArea";
    private int mPosition;
    private Bitmap mSrc;
    private Rect mSrcBound;
    private Rect mDestBound;
    private boolean mFullSizeFlag = false;
    public boolean isSubstituent = false;

    // the following two fields have a strong relationship with mSrcBound,change
    // anyone,the other should be changed!
    private float mDisplayXPortion = 1f;
    private float mDisplayYPortion = 1f;

    public ImageArea(Bitmap src, Rect srcBound) {
        this(-1, src, srcBound, null);
    }

    public ImageArea(int position, Bitmap src, Rect srcBound, Rect destBound) {
        this(position, src, srcBound, destBound, null);
    }

    public ImageArea(ImageArea item) {
        this(item.getPosition(), item.getSrc(), item.getSrcBound(), item.getDestBound(),
                item.getAnimators());
    }

    public ImageArea(int position, Bitmap src, Rect srcBound, Rect destBound,
            List<LHAnimator> animators) {
        mPosition = position;
        if (null != src) {
            mSrc = src;
        }
        Log.v(TAG, null == srcBound ? "null rect" : srcBound.toString());
        if (null != srcBound) {
            mSrcBound = new Rect(srcBound);
            refreshPortion();
        }
        if (null != destBound) {
            mDestBound = new Rect(destBound);
        }
        if (null != animators) {
            animators = new ArrayList<LHAnimator>(animators);
        } else {
            animators = new ArrayList<LHAnimator>();
        }
        init();
    }

    private void init() {
        paint = new Paint();
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
            canvas.drawBitmap(mSrc, mSrcBound, mDestBound, paint);
            canvas.restore();
        }
        if (!hasMoreFrame && null != animators) {
            animators.clear();
        }
        return hasMoreFrame;
    }

    // when a srcBound is set,the mDisplayXPortion and mDisplayYPortion should
    // be
    // changed!
    private void refreshPortion() {
        if (null != mSrc && null != mSrcBound) {
            mDisplayXPortion = (float) mSrcBound.width() / (float) mSrc.getWidth();
            mDisplayYPortion = (float) mSrcBound.height() / (float) mSrc.getHeight();
        }
    }

    // when a new src is set,the srcBound must be changed according to the XY
    // Portion
    private void refreshSrcBound() {
        if (null != mSrc && null != mSrcBound) {
            int srcWidth = mSrc.getWidth();
            int srcHeight = mSrc.getHeight();
            int displayWidth = (int) (srcWidth * mDisplayXPortion);
            int displayHeight = (int) (srcHeight * mDisplayYPortion);
            int offSetX = (srcWidth - displayWidth) / 2;
            int offSetY = (srcHeight - displayHeight) / 2;
            mSrcBound.left = offSetX;
            mSrcBound.right = srcWidth - offSetX;
            mSrcBound.top = offSetY;
            mSrcBound.bottom = srcHeight - offSetY;
            Log.v(TAG,
                    mSrcBound + " srcWidth:" + srcWidth + " srcHeight:" + srcHeight + " offsetX:"
                            + offSetX + " offsetY:" + offSetY + " xportion:" + mDisplayXPortion
                            + " yportion:" + mDisplayYPortion);
        }
    }

    private void refreshAnimator() {
        if (null != animators && !animators.isEmpty()) {
            for (int i = 0; i < animators.size(); i++) {
                LHAnimator animator = animators.get(i);
                if (null != animator && animator instanceof ScaleAnimator) {
                    ((ScaleAnimator) animator).setEndSrcBound(getSrcSize());
                    ((ScaleAnimator) animator).setCurSrcBound(mSrcBound);
                }
            }
        }
    }

    public void showIntegrity() {
        setPortion(1.0f, 1.0f);
    }

    private void setPortion(float xPortion, float yPortion) {
        this.mDisplayXPortion = xPortion;
        this.mDisplayYPortion = yPortion;
        int offsetX = (mSrc.getWidth() - (int) (mSrc.getWidth() * mDisplayXPortion)) / 2;
        int offsetY = (mSrc.getHeight() - (int) (mSrc.getHeight() * mDisplayYPortion)) / 2;
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

    public boolean isFullSize() {
        return mFullSizeFlag;
    }

    public void setFullSizeFlag(boolean flag) {
        this.mFullSizeFlag = flag;
    }

    public float getSrcRatio() {
        return (float) mSrc.getWidth() / (float) mSrc.getHeight();
    }

    public Rect getSrcSize() {
        return new Rect(0, 0, mSrc.getWidth(), mSrc.getHeight());
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
        refreshAnimator();
    }

    public boolean hasSrc() {
        return null == mSrc ? false : true;
    }

    public Rect getSrcBound() {
        return mSrcBound;
    }

    public void setSrcBound(Rect srcBound) {
        if (null == srcBound) {
            this.mSrcBound = new Rect(srcBound);
        } else {
            this.mSrcBound.left = srcBound.left;
            this.mSrcBound.top = srcBound.top;
            this.mSrcBound.right = srcBound.right;
            this.mSrcBound.bottom = srcBound.bottom;
        }
        refreshPortion();
    }

    public Rect getDestBound() {
        return mDestBound;
    }

    public void setDestBound(Rect destBound) {
        if (null == this.mDestBound) {
            this.mDestBound = new Rect(destBound);
        } else {
            this.mDestBound.left = destBound.left;
            this.mDestBound.top = destBound.top;
            this.mDestBound.right = destBound.right;
            this.mDestBound.bottom = destBound.bottom;
        }
    }

    public ImageAreaAttribute getAttribute() {
        return new ImageAreaAttribute(this.mPosition, this.mSrcBound, this.mDestBound,
                this.mDisplayXPortion, this.mDisplayYPortion, mFullSizeFlag);
    }

    public class ImageAreaAttribute {
        public int mPosition;
        public Rect mSrcBound;
        public Rect mDestBound;
        public float mDisplayXPortion = 1f;
        public float mDisplayYPortion = 1f;
        public boolean mFullSizeFlag = false;

        public ImageAreaAttribute(int position, Rect srcBound, Rect destBound, float xPortion,
                float yPortion, boolean fullFlag) {
            mPosition = position;
            if (null != srcBound) {
                mSrcBound = new Rect(srcBound);
            }
            if (null != destBound) {
                mDestBound = new Rect(destBound);
            }
            mDisplayXPortion = xPortion;
            mDisplayYPortion = yPortion;
            mFullSizeFlag = fullFlag;
        }
    }
}
