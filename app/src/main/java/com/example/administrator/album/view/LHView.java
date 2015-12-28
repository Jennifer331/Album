package com.example.administrator.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.administrator.album.animator.AlphaAnimator;
import com.example.administrator.album.animator.ScaleAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-13.
 */
public class LHView extends View {
    private final static String TAG = "MyCustomedView";

    private static final int SHOW_ALPHA = 255;
    private static final int HIDE_ALPHA = 0;

    protected List<LHItem> mChildren;
    private AnimationCallback mCallback;

    private boolean mAnimationFlag = false;

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

    public interface AnimationCallback {
        public void animationFinished();
    }

    public void showAll(){
        if (null != mChildren && !mChildren.isEmpty()) {
            for (LHItem item : mChildren) {
                if (null != item && item instanceof ImageArea) {
                    item.setHideFlag(false);
                    item.setAlpha(SHOW_ALPHA);
                }
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.v(TAG, "in onDraw");
        boolean hasMoreFrame = false;
        if (null != mChildren && !mChildren.isEmpty()) {
            for (LHItem item : mChildren) {
                if (null != item) {
                    hasMoreFrame |= item.draw(canvas);
                }
            }
        }
        if (hasMoreFrame) {
            invalidate();
        } else {
            if (mAnimationFlag) {
                if (null != mCallback) {
                    mCallback.animationFinished();
                    mCallback = null;
                }
                Log.v(TAG, "gone");
                mAnimationFlag = false;
            }
        }
    }

    public void fadein() {
        mAnimationFlag = false;
        if (null != mChildren && !mChildren.isEmpty()) {
            for (LHItem item : mChildren) {
                if (null != item) {
                    item.setAlpha(HIDE_ALPHA);
                    AlphaAnimator animator = new AlphaAnimator(item.getAlpha(), SHOW_ALPHA);
                    item.addAnimator(animator);
                }
            }
        }
        this.setVisibility(VISIBLE);
        invalidate();
    }

    protected void fadeout(AnimationCallback callback) {
        mCallback = callback;
        mAnimationFlag = true;
        Log.v(TAG, "fadeout");
        if (null != mChildren && !mChildren.isEmpty()) {
            for (LHItem item : mChildren) {
                if (null != item) {
                    AlphaAnimator animator = new AlphaAnimator(item.getAlpha(), HIDE_ALPHA);
                    item.addAnimator(animator);
                }
            }
        }
        invalidate();
    }

    public void radiation(Rect srcLocation) {
        mAnimationFlag = true;
        int counter = 0;
        if (null != mChildren && !mChildren.isEmpty()) {
            for (LHItem item : mChildren) {
                Log.v(TAG,counter + "");
                if (null != item && item instanceof ImageArea) {
                    Log.v(TAG,counter++ + "");
                    ScaleAnimator animator = new ScaleAnimator(null, null,
                            srcLocation, ((ImageArea) item).getDestBound());
                    item.addAnimator(animator);
                }
            }
        }
    }

    protected void refreshDisplayingItem() {

    }

}
