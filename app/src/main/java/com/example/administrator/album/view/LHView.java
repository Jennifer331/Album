package com.example.administrator.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-13.
 */
public class LHView extends View {
    private final static String TAG = "MyCustomedView";

    protected List<ImageArea> mChildren;
    private ImageArea mAnimatingItem;

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
        }
    }
}
