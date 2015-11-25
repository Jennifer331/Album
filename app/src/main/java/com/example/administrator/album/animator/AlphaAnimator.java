package com.example.administrator.album.animator;

import com.example.administrator.album.view.ImageArea;

/**
 * Created by Lei Xiaoyue on 2015-11-18.
 */
public class AlphaAnimator extends LHAnimator{
    private final static String TAG = "AlphaAnimator";
    private final static float DEFAULT_FACTOR = 1E-1F;
    private final static float ERROR = 3E-1F;

    private int mBeginAlpha = 255;
    private int mEndAlpha = 0;

    public AlphaAnimator(int start,int end) {
        mBeginAlpha = start;
        mEndAlpha = end;
    }

    public int getEndAlpha() {
        return mEndAlpha;
    }

    public void setEndAlpha(int mEndAlpha) {
        this.mEndAlpha = mEndAlpha;
    }

    public int getBeginAlpha() {
        return mBeginAlpha;
    }

    public void setBeginAlpha(int mBeginAlpha) {
        this.mBeginAlpha = mBeginAlpha;
    }

    @Override
    public boolean hasNextFrame(ImageArea object){
        boolean result = false;
        int alpha = object.getAlpha();
        if( Math.abs(alpha - mEndAlpha) > ERROR){
            result = true;
            object.setAlpha((int)(alpha + (mEndAlpha - alpha) * DEFAULT_FACTOR));
        }
        return result;
    }

    public void revert(){
        int temp = mBeginAlpha;
        mBeginAlpha = mEndAlpha;
        mEndAlpha = temp;
    }
}
