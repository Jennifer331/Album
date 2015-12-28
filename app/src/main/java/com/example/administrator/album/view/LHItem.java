package com.example.administrator.album.view;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.administrator.album.animator.LHAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-12-01.
 */
public class LHItem {
    private boolean mHideFlag;
    protected List<LHAnimator> animators;
    protected Paint paint;

    public LHItem(){
        this(null,null);
    }

    public LHItem(List<LHAnimator> animators,Paint paint){
        if(null == animators){
            this.animators = new ArrayList<LHAnimator>();
        }else {
            this.animators = new ArrayList<>(animators);
        }

        if(null == paint){
            this.paint = new Paint();
        }else {
            this.paint = new Paint(paint);
        }
    }

    public void setHideFlag(boolean hide){
        mHideFlag = hide;
    }

    public boolean isHide(){
        return mHideFlag;
    }

    public boolean draw(Canvas canvas) {
        return false;
    }

    public int getAlpha() {
        return paint.getAlpha();
    }

    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    public void addAnimator(LHAnimator animator) {
        if (null == animators) {
            animators = new ArrayList<LHAnimator>();
        }
        if (!animators.isEmpty()) {
            for (int i = 0; i < animators.size(); i++) {
                LHAnimator item = animators.get(i);
                if (item.getClass().equals(animator.getClass()))
                    animators.remove(item);
            }
        }
        animators.add(animator);
    }

    public boolean hasAnimator(Class type) {
        if (null != animators && !animators.isEmpty()) {
            for (LHAnimator animator : animators) {
                if (null != animator && null != type
                        && animator.getClass().equals(type.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    public LHAnimator getAnimator(Class type) {
        LHAnimator result = null;
        if (null != animators && !animators.isEmpty()) {
            for (LHAnimator animator : animators) {
                if (null != animator && null != type
                        && animator.getClass().equals(type.getClass())) {
                    result = animator;
                }
            }
        }
        return result;
    }

    public List<LHAnimator> getAnimators() {
        return animators;
    }

    public void setAnimators(List<LHAnimator> animators) {
        this.animators = new ArrayList<LHAnimator>(animators);
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = new Paint(paint);
    }
}
