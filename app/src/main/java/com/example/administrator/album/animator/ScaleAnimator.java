package com.example.administrator.album.animator;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.example.administrator.album.view.ImageArea;

/**
 * Created by Lei Xiaoyue on 2015-11-18.
 */
public class ScaleAnimator extends LHAnimator{
    private final static String TAG = "ScaleAnimator";
    private final static float DEFAULT_FACTOR = 1E-1F;
    private final static float SRC_FACTOR = 1E-1F;
    private final static float ERROR = 3E-1F;

    private int mBeginBgAlpha = 255;
    private Rect mBeginSrcBound;
    private Rect mEndSrcBound;
    private Rect mBeginDestBound;
    private Rect mEndDestBound;

    private static int i = 0;
    public ScaleAnimator(Rect endDestBound, Rect endSrcBound) {
        mEndDestBound = endDestBound;
        mEndSrcBound = endSrcBound;
    }

    public void init(ImageArea item){
        mBeginSrcBound = new Rect(item.getSrcBound());
        mBeginDestBound = new Rect(item.getDestBound());
    }

    public void copyRect(Rect from,Rect to){
        if(null != from && null != to){
            to.left = from.left;
            to.right = from.right;
            to.top = from.top;
            to.bottom = from.bottom;
        }
    }

    public void setEndSrcBound(Rect rect){
        copyRect(rect,mEndSrcBound);
    }

    public void setmEndDestBound(Rect rect){
        copyRect(rect,mEndDestBound);
    }

    public int getBgAlpha() {
        return mBeginBgAlpha;
    }

    @Override
    public boolean hasNextFrame(ImageArea object){
        Log.v(TAG,++i + "");
        boolean result = false;
        if( Math.abs(object.getDestBound().left - mEndDestBound.left) > ERROR
                || Math.abs(object.getDestBound().right - mEndDestBound.right) > ERROR
                || Math.abs(object.getDestBound().top - mEndDestBound.top) > ERROR
                || Math.abs(object.getDestBound().bottom - mEndDestBound.bottom) > ERROR
                || Math.abs(object.getSrcBound().left - mEndSrcBound.left) > ERROR
                || Math.abs(object.getSrcBound().right - mEndSrcBound.right) > ERROR
                || Math.abs(object.getSrcBound().top - mEndSrcBound.top) > ERROR
                || Math.abs(object.getSrcBound().bottom - mEndSrcBound.bottom) > ERROR){
            result = true;
            Rect destBound = object.getDestBound();
            if(null != destBound && null != mEndDestBound) {
                int left = (int) (destBound.left + (mEndDestBound.left - destBound.left) * SRC_FACTOR);
                int right = (int) (destBound.right + (mEndDestBound.right - destBound.right) * SRC_FACTOR);
                int top = (int) (destBound.top + (mEndDestBound.top - destBound.top) * SRC_FACTOR);
                int bottom = (int) (destBound.bottom + (mEndDestBound.bottom - destBound.bottom) * SRC_FACTOR);
                object.setDestBound(new Rect(left, top, right, bottom));
            }
            Rect srcBound = object.getSrcBound();
            if(null != srcBound && null != mEndSrcBound) {
                int left = (int) (srcBound.left + (mEndSrcBound.left - srcBound.left) * SRC_FACTOR);
                int right = (int) (srcBound.right + (mEndSrcBound.right - srcBound.right) * SRC_FACTOR);
                int top = (int) (srcBound.top + (mEndSrcBound.top - srcBound.top) * SRC_FACTOR);
                int bottom = (int) (srcBound.bottom + (mEndSrcBound.bottom - srcBound.bottom) * SRC_FACTOR);
                object.setSrcBound(new Rect(left, top, right, bottom));
            }
        }
        return result;
    }

    public void revert(){
        Rect temp = mEndDestBound;
        mEndDestBound = mBeginDestBound;
        mBeginDestBound = temp;

        temp = mEndSrcBound;
        mEndSrcBound = mBeginSrcBound;
        mBeginSrcBound = temp;
    }
}
