package com.example.administrator.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.Toast;

import com.example.administrator.album.adapter.MyImageAdapter;
import com.example.administrator.album.animator.AlphaAnimator;

/**
 * Created by Lei Xiaoyue on 2015-11-24.
 */
public class AlbumPage extends LHView {
    private final static String TAG = "AlbumPage";

    private final static int COLUMN = 4;
    private final static int LINE_MARGIN = 5;
    private final static int COLUMN_MARGIN = 5;
    private final static int ANCHOR = 0;
    private final static int DEFAULT_TEST_ALBUM_ID = -17_3977_3001;
    private static final int FLING_VELOCITY_DOWNSCALE = 3;
    private static final int FADE_OUT_BEGIN_ALPHA = 255;
    private static final int FADE_OUT_END_ALPHA = 0;

    private int endLine = ANCHOR;
    private int thumbWidth;
    private int thumbHeight;

    private Rect mDisplayBound;
    private GestureDetectorCompat mDetector;

    private MyImageAdapter mAdapter;
    private OverScroller mScroller;

    private Callback mCallback;

    public interface Callback {
        void headToImage(ImageArea item);
    }

    public AlbumPage(Context context, Callback callback) {
        super(context);
        init(context, callback);
    }

    private void init(Context context, Callback callback) {
        mCallback = callback;
        mDisplayBound = new Rect(0, 0, 0, 0);
        mAdapter = new MyImageAdapter(context, DEFAULT_TEST_ALBUM_ID);
        mDetector = new GestureDetectorCompat(context, new AlbumGestureListener());
        mScroller = new OverScroller(context);
        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mScroller.computeScrollOffset()) {
            invalidate();
        }

        if (0 == mDisplayBound.height()) {
            mDisplayBound.right = getWidth();
            mDisplayBound.bottom = getHeight();
            thumbWidth = (mDisplayBound.width() - (COLUMN - 1) * COLUMN_MARGIN) / COLUMN;
            thumbHeight = thumbWidth;
            endLine = (mAdapter.getCount() / COLUMN) * (thumbHeight + LINE_MARGIN) + thumbHeight;
            endLine = endLine < mDisplayBound.height() ? mDisplayBound.height() : endLine;
        }
        int singlePageThumbAmount = COLUMN * (mDisplayBound.height() / (thumbHeight + LINE_MARGIN))
                + COLUMN;
        int pastThumbAmount = COLUMN * ((mDisplayBound.top - ANCHOR) / (thumbHeight + LINE_MARGIN));
        if (0 != (mDisplayBound.top - ANCHOR) % (thumbHeight + LINE_MARGIN)) {
            pastThumbAmount -= 4;
            singlePageThumbAmount += 8;
        }
        if (pastThumbAmount < 0) {
            pastThumbAmount = 0;
        }
        if (mAdapter.getCount() < pastThumbAmount + singlePageThumbAmount) {
            // TODO
        }
        if (singlePageThumbAmount <= 0)
            return;
        mChildren.clear();
        for (int i = pastThumbAmount; mAdapter.getCount() > i
                && i < pastThumbAmount + singlePageThumbAmount; i++) {
            float x = (i % COLUMN) * thumbWidth + (i % COLUMN - 1) * COLUMN_MARGIN;
            float y = (i / COLUMN) * (thumbHeight + LINE_MARGIN) + LINE_MARGIN - mDisplayBound.top;
            ImageArea item = mAdapter.getImageArea(this, i, thumbWidth, thumbHeight);
            if (null != item) {
                item.setPosition(i);
                Rect dest = new Rect((int) x, (int) y, (int) (x + thumbWidth),
                        (int) (y + thumbHeight));
                item.setDestBound(dest);
                mChildren.add(item);
            }
        }
        super.onDraw(canvas);
    }

    private void adjustDisplayingBound(int deltaY) {
        mDisplayBound.top += deltaY;
        mDisplayBound.bottom += deltaY;
        checkBoundLimit();
        invalidate();
    }

    private void checkBoundLimit() {
        if (ANCHOR > mDisplayBound.top) {
            int delta = mDisplayBound.top - ANCHOR;
            mDisplayBound.top -= delta;
            mDisplayBound.bottom -= delta;
        }
        if (endLine < mDisplayBound.bottom) {
            int delta = mDisplayBound.bottom - endLine;
            mDisplayBound.top -= delta;
            mDisplayBound.bottom -= delta;
        }
    }

    public class AlbumGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            int position = (int) (((mDisplayBound.top + event.getY())
                    / (thumbHeight + LINE_MARGIN))) * COLUMN
                    + (int) (event.getX() / (thumbWidth + COLUMN_MARGIN));
            Toast.makeText(getContext(), position + "clicked", Toast.LENGTH_SHORT).show();
            ImageArea mTarget = null;
            for (ImageArea child : mChildren) {
                if (position == child.getPosition()) {
                    mTarget = child;
                    break;
                }
            }
            if (null == mTarget) {
                return true;
            }
            mCallback.headToImage(mTarget);
            fadeout();
            return true;
        }

        private void fadeout() {
            AlphaAnimator animator = new AlphaAnimator(FADE_OUT_BEGIN_ALPHA, FADE_OUT_END_ALPHA);
            if(null != mChildren && !mChildren.isEmpty()){
                for(ImageArea item : mChildren){
                    if(null != item){
                        item.addAnimator(animator);
                    }
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scroll(distanceY);
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.forceFinished(true);
            mScroller.fling(mScroller.getCurrX(), mScroller.getCurrY(), 0,
                    (int) velocityY / FLING_VELOCITY_DOWNSCALE, 0, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 10, 10);
            invalidate();
            return true;
        }
    }

    private void scroll(float distanceY) {
        mDisplayBound.top += distanceY;
        mDisplayBound.bottom += distanceY;
        checkBoundLimit();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mDetector.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        Log.v(TAG, "in computeScroll");
        if (null != mScroller) {
            int oldy = mScroller.getCurrY();
            if (mScroller.computeScrollOffset()) {
                scroll(oldy - mScroller.getCurrY());
            }
        }
    }
}
