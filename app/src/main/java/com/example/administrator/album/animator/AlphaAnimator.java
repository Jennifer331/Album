package com.example.administrator.album.animator;

import android.util.Log;

import com.example.administrator.album.view.LHItem;

/**
 * Created by Lei Xiaoyue on 2015-11-18.
 */
public class AlphaAnimator extends LHAnimator {
    private final static String TAG = "AlphaAnimator";

    private final static float DEFAULT_FACTOR = 1E-1F;
    private final static float ERROR = 1E-2F;

    private float mStartAlpha;
    private float mCurrentAlpha;
    private float mEndAlpha;

    private static int counter = 0;

    public AlphaAnimator(int start, int end) {
        mStartAlpha = toFloatAlpha(start);
        mCurrentAlpha = mStartAlpha;
        mEndAlpha = toFloatAlpha(end);
    }

    /**
     *
     * @param integerAlpha
     *            [0, 255]
     * @return
     */
    private static float toFloatAlpha(int integerAlpha) {
        return (float) integerAlpha / 255.0f;
    }

    /**
     *
     * @param floatAlpha
     *            [0.0, 1.0]
     * @return
     */
    private static int toIntegerAlhpa(float floatAlpha) {
        return (int) (floatAlpha * 255.0f);
    }

    public int getEndAlpha() {
        return toIntegerAlhpa(mEndAlpha);
    }

    public void setEndAlpha(int endAlpha) {
        this.mEndAlpha = toFloatAlpha(endAlpha);
    }

    @Override
    public boolean hasNextFrame(LHItem object) {
        boolean hasNextFrame = false;
        if (Math.abs(mCurrentAlpha - mEndAlpha) > ERROR) {
            hasNextFrame = true;
            mCurrentAlpha = mCurrentAlpha + (mEndAlpha - mCurrentAlpha) * DEFAULT_FACTOR;
        } else {
            mCurrentAlpha = mEndAlpha;
        }
        object.setAlpha(toIntegerAlhpa(mCurrentAlpha));
        Log.v(TAG,mCurrentAlpha + "");
        return hasNextFrame;
    }

    public int getCurrentAlpha() {
        return toIntegerAlhpa(mCurrentAlpha);
    }
}
