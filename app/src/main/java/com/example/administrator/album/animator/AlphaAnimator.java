package com.example.administrator.album.animator;

import android.util.Log;

import com.example.administrator.album.view.ImageArea;

/**
 * Created by Lei Xiaoyue on 2015-11-18.
 */
public class AlphaAnimator extends LHAnimator{
    private final static String TAG = "AlphaAnimator";

    private int mEndAlpha = 0;

    private static int counter = 0;
    public AlphaAnimator(int end) {
        mEndAlpha = end;
    }

    public int getEndAlpha() {
        return mEndAlpha;
    }

    public void setEndAlpha(int mEndAlpha) {
        this.mEndAlpha = mEndAlpha;
    }

    @Override
    public boolean hasNextFrame(ImageArea object){
        Log.v(TAG,++counter + " object alpha:"+ object.getAlpha() + " endAlpha" + mEndAlpha);
        boolean hasNextFrame = false;
        int alpha = object.getAlpha();
        if(alpha == mEndAlpha) {
            return hasNextFrame;
        }else if(alpha < mEndAlpha){
            hasNextFrame = true;
            alpha += 3;
            object.setAlpha(alpha);
        }else{
            hasNextFrame = true;
            alpha -= 3;
            object.setAlpha(alpha);
        }
        return hasNextFrame;
    }
}
