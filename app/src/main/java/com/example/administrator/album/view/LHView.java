package com.example.administrator.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.administrator.album.animator.AlphaAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-13.
 */
public class LHView extends View {
    private final static String TAG = "MyCustomedView";

    private static final int SHOW_ALPHA = 255;
    private static final int HIDE_ALPHA = 0;

    protected List<ImageArea> mChildren;

    private boolean mFadeOutFlag = false;

    public LHView(Context context) {
        this(context, null);
    }

    public LHView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LHView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LHView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mChildren = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.v(TAG,"in onDraw");
        boolean hasMoreFrame = false;
        if(null != mChildren && !mChildren.isEmpty()){
            for(ImageArea item:mChildren){
                if(null != item) {
                    hasMoreFrame |= item.draw(canvas);
                }
            }
        }
        if (hasMoreFrame) {
            invalidate();
        }else{
            if(mFadeOutFlag) {
                this.setVisibility(GONE);
                Log.v(TAG,"gone");
                mFadeOutFlag = false;
            }
        }
    }

    protected void fadein() {
        this.setVisibility(VISIBLE);
        mFadeOutFlag = false;
        AlphaAnimator animator = new AlphaAnimator(SHOW_ALPHA);
        if (null != mChildren && !mChildren.isEmpty()) {
            for (ImageArea item : mChildren) {
                if (null != item) {
                    item.addAnimator(animator);
                }
            }
        }
        invalidate();
    }

    protected void fadeout() {
        mFadeOutFlag = true;
        Log.v(TAG,"fadeout");
        AlphaAnimator animator = new AlphaAnimator(HIDE_ALPHA);
        if (null != mChildren && !mChildren.isEmpty()) {
            for (ImageArea item : mChildren) {
                if (null != item) {
                    item.addAnimator(animator);
                }
            }
        }
        invalidate();
    }

    protected void refreshDisplayingItem(){

    }

}
