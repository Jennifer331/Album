package com.example.administrator.album.animator;

import android.graphics.Rect;
import android.util.Log;

import com.example.administrator.album.view.ImageArea;
import com.example.administrator.album.view.LHItem;

/**
 * Created by Lei Xiaoyue on 2015-11-18.
 */
public class ScaleAnimator extends LHAnimator {
    private final static String TAG = "ScaleAnimator";

    private LHRectAnimator mSrcBoundAnimator;
    private LHRectAnimator mDestBoundAnimator;

    private Callback mCallback;

    public interface Callback {
        void animationFinished();
    }

    public ScaleAnimator(final Rect curSrcBound, final Rect endSrcBound, final Rect curDestBound,
                         final Rect endDestBound) {
        this(curSrcBound,endSrcBound,curDestBound,endDestBound,null);
    }

    public ScaleAnimator(final Rect curSrcBound, final Rect endSrcBound, final Rect curDestBound,
                         final Rect endDestBound, final Callback callback) {
        mSrcBoundAnimator = new LHRectAnimator(curSrcBound,endSrcBound);
        mDestBoundAnimator = new LHRectAnimator(curDestBound,endDestBound);
        mCallback = callback;
    }

    public void setEndSrcBound(Rect rect) {
        mSrcBoundAnimator.setDestRect(rect);
    }

    public void setCurSrcBound(Rect rect){
        mSrcBoundAnimator.setCurrentRect(rect);
    }

    public void setEndDestBound(Rect rect) {
        mDestBoundAnimator.setDestRect(rect);
    }

    @Override
    public boolean hasNextFrame(LHItem object) {
        boolean hasNextFrame = false;
        hasNextFrame |= mSrcBoundAnimator.compute();
        hasNextFrame |= mDestBoundAnimator.compute();
        if(!mSrcBoundAnimator.isInvalid()) {
            ((ImageArea) object).setSrcBound(mSrcBoundAnimator.getCurrentRect());
        }
        Log.v(TAG, mDestBoundAnimator.getCurrentRect() + "");
        if(!mDestBoundAnimator.isInvalid()) {
            ((ImageArea) object).setDestBound(mDestBoundAnimator.getCurrentRect());
        }
        if(!hasNextFrame && null != mCallback){
            mCallback.animationFinished();
        }
        return hasNextFrame;
    }

    public Rect getCurSrcBound(){
        return mSrcBoundAnimator.getCurrentRect();
    }

    public Rect getCurDestBound(){
        return mDestBoundAnimator.getCurrentRect();
    }
}
