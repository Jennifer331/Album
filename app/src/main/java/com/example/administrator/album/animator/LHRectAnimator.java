package com.example.administrator.album.animator;

import android.graphics.Rect;

/**
 * Created by Lei Xiaoyue on 2015-12-03.
 */
public class LHRectAnimator extends LHComputeAnimator {
    private static final int RECT_PARAM_NUM = 4;
    private Rect mCurrentRect;
    private Rect mDestRect;
    private float[] mCurrentValues = new float[RECT_PARAM_NUM];
    private float[] mDestValues = new float[RECT_PARAM_NUM];
    private final static float DEFAULT_FACTOR = 1E-1F;
    private final static float ERROR = 1E-3F;

    private float ref = 1.0f;
    private boolean mInvalidFlag = false;

    public LHRectAnimator(final Rect currentRect, final Rect destRect) {
        if(null == currentRect || null == destRect){
            mInvalidFlag = true;
            return;
        }
        mCurrentRect = currentRect;
        mDestRect = destRect;
        ref = getMax();
        refreshCurrentValues();
        refreshDestValues();
    }

    public boolean isInvalid(){
        return mInvalidFlag;
    }

    private int getMax() {
        int max = Math.abs(mDestRect.left - mCurrentRect.left);
        int value = Math.abs(mDestRect.top - mCurrentRect.top);
        if (value > max) {
            max = value;
        }
        value = Math.abs(mDestRect.right - mCurrentRect.right);
        if (value > max) {
            max = value;
        }
        value = Math.abs(mDestRect.bottom - mCurrentRect.bottom);
        if (value > max) {
            max = value;
        }
        return 0 == max ? 1 : max;
    }

    @Override
    public boolean compute() {
        if(mInvalidFlag){
            return false;
        }
        boolean hasNextFrame = false;
        if (Math.abs(mCurrentValues[0] - mDestValues[0]) > ERROR
                || Math.abs(mCurrentValues[1] - mDestValues[1]) > ERROR
                || Math.abs(mCurrentValues[2] - mDestValues[2]) > ERROR
                || Math.abs(mCurrentValues[3] - mDestValues[3]) > ERROR) {
            hasNextFrame = true;
            for (int i = 0; i < RECT_PARAM_NUM; i++) {
                mCurrentValues[i] = mCurrentValues[i]
                        + (mDestValues[i] - mCurrentValues[i]) * DEFAULT_FACTOR;
            }
        } else {
            for (int i = 0; i < RECT_PARAM_NUM; i++) {
                mCurrentValues[i] = mDestValues[i];
            }
        }
        return hasNextFrame;
    }

    private void refreshCurrentValues() {
        mCurrentValues[0] = (float) mCurrentRect.left / ref;
        mCurrentValues[1] = (float) mCurrentRect.top / ref;
        mCurrentValues[2] = (float) mCurrentRect.right / ref;
        mCurrentValues[3] = (float) mCurrentRect.bottom / ref;
    }

    private void refreshDestValues() {
        mDestValues[0] = (float) mDestRect.left / ref;
        mDestValues[1] = (float) mDestRect.top / ref;
        mDestValues[2] = (float) mDestRect.right / ref;
        mDestValues[3] = (float) mDestRect.bottom / ref;
    }

    private void copyRect(Rect from, Rect to) {
        if (null != from && null != to) {
            to.left = from.left;
            to.right = from.right;
            to.top = from.top;
            to.bottom = from.bottom;
        }
    }

    public void setDestRect(Rect rect) {
        if(mInvalidFlag){
            return;
        }
        copyRect(rect, mDestRect);
        refreshDestValues();
    }

    public void setCurrentRect(Rect rect) {
        if(mInvalidFlag){
            return;
        }
        copyRect(rect, mCurrentRect);
        refreshCurrentValues();
    }

    public Rect getCurrentRect() {
        if(mInvalidFlag){
            return null;
        }
        return new Rect((int) (mCurrentValues[0] * ref),
                (int) (mCurrentValues[1] * ref),
                (int) (mCurrentValues[2] * ref),
                (int) (mCurrentValues[3] * ref));
    }
}
