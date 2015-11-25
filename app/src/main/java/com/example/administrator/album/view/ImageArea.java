package com.example.administrator.album.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.administrator.album.animator.AlphaAnimator;
import com.example.administrator.album.animator.LHAnimator;
import com.example.administrator.album.animator.ScaleAnimator;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;

/**
 * Created by Lei Xiaoyue on 2015-11-17.
 */
public class ImageArea {
    private static final String TAG = "ImageArea";
    private int mPosition;
    private Bitmap mSrc;
    private Rect mSrcBound;
    private Rect mDestBound;
    private List<LHAnimator> mAnimators;
    private Paint mPaint;
    private boolean mFullSizeFlag = false;

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
            mAnimators = animators;
        } else {
            mAnimators = new ArrayList<LHAnimator>();
        }
        init();
    }

    private void init() {
        mPaint = new Paint();
    }

    public boolean draw(Canvas canvas) {
        boolean hasMoreFrame = false;
        if (null != mSrc) {
            if (null != mAnimators && !mAnimators.isEmpty()) {
                for (LHAnimator animator : mAnimators) {
                    if (null != animator) {
                        hasMoreFrame |= animator.hasNextFrame(this);
                    }
                }
            }
            canvas.save();
            canvas.drawBitmap(mSrc, mSrcBound, mDestBound, mPaint);
            canvas.restore();
        }
        if(!hasMoreFrame){
            mAnimators.clear();
        }
        return hasMoreFrame;
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

    public boolean hasScaleAnimator(){
        if(null != mAnimators && !mAnimators.isEmpty()){
            for(LHAnimator animator:mAnimators){
                if(null != animator && animator instanceof ScaleAnimator){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "mPosition:" + mPosition + " X:" + mDestBound.left + " Y:" + mDestBound.top
                + " width:" + getWidth() + " height:" + getHeight() + " src "
                + (null == mSrc ? "is " : "is not") + " null";
    }

    public boolean isFullSize(){
        return mFullSizeFlag;
    }

    public void setFullSizeFlag(boolean flag){
        this.mFullSizeFlag = flag;
    }

    public float getSrcRatio() {
        return mSrc.getWidth() / mSrc.getHeight();
    }

    public Rect getSrcSize(){
        return new Rect(0,0,mSrc.getWidth(),mSrc.getHeight());
    }

    public int getAlpha() {
        return mPaint.getAlpha();
    }

    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    public List<LHAnimator> getAnimators() {
        return mAnimators;
    }

    public ScaleAnimator getScaleAnimator(){
        ScaleAnimator result = null;
        if(null != mAnimators && !mAnimators.isEmpty()){
            for(LHAnimator animator:mAnimators){
                if(null != animator && animator instanceof ScaleAnimator){
                    result = (ScaleAnimator)animator;
                }
            }
        }
        return result;
    }

    public void addAnimator(LHAnimator animator) {
        if (null == mAnimators) {
            mAnimators = new ArrayList<LHAnimator>();
        }
        if(!mAnimators.isEmpty()){
            for(int i = 0;i < mAnimators.size();i++){
                LHAnimator item = mAnimators.get(i);
                if(item.getClass().equals(animator.getClass()))
                    mAnimators.remove(item);
            }
        }
        mAnimators.add(animator);
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

    public boolean hasSrc() {
        return null == mSrc ? false : true;
    }

    public Rect getSrcBound() {
        return mSrcBound;
    }

    public void setSrcBound(Rect srcBound) {
        if(null == srcBound){
            this.mSrcBound = new Rect(srcBound);
        }else {
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
        if(null == this.mDestBound){
            this.mDestBound = new Rect(destBound);
        }else {
            this.mDestBound.left = destBound.left;
            this.mDestBound.top = destBound.top;
            this.mDestBound.right = destBound.right;
            this.mDestBound.bottom = destBound.bottom;
        }
    }

    public float getDisplayXPortion() {
        return displayXPortion;
    }

    public float getDisplayYPortion() {
        return displayYPortion;
    }
}
