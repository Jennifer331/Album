package com.example.administrator.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.Toast;

import com.example.administrator.album.activity.TestActivity;
import com.example.administrator.album.adapter.AlbumSetAdapter;

/**
 * Created by Lei Xiaoyue on 2015-11-24.
 * <p/>
 * ************thumbWidth*************
 * *                                 *
 * *         **************          *
 * *         *            *          *
 * *         *            *          *
 * *         *    IMAGE   *     thumbHeight
 * *         *            *          *
 * *          **************          *
 * *              TITLE              *
 * *                                 *
 * ***********************************
 */
public class AlbumSetPage extends LHView {
    private final static String TAG = "AlbumSetPage";

    private final static int COLUMN = 3;
    private final static int LINE_MARGIN = 10;
    private final static int COLUMN_MARGIN = 10;
    private final static int ANCHOR = 0;
    private static final int FLING_VELOCITY_DOWNSCALE = 3;
    private static final int TITLE_RATIO = 7;

    private int mEndline = ANCHOR;
    private int mThumbWidth;
    private int mThumbHeight;
    private int mTitleHeight;

    private Rect mDisplayBound;
    private GestureDetectorCompat mDetector;

    private AlbumSetAdapter mAdapter;
    private OverScroller mScroller;

    private Callback mCallback;

    private boolean mInitFlag = true;

    public interface Callback {
        void headToAlbum(int albumId,Rect from);
    }

    public AlbumSetPage(Context context, Callback callback) {
        super(context);
        init(context, callback);
    }

    private void init(Context context, Callback callback) {
        mCallback = callback;
        mDisplayBound = new Rect(0, 0, 0, 0);
        mAdapter = new AlbumSetAdapter(context);
        mDetector = new GestureDetectorCompat(context, new AlbumSetGestureListener());
        mScroller = new OverScroller(context);
    }

    public void show(int position) {
        int datum = (position / COLUMN) * (mThumbHeight + LINE_MARGIN);
        if (datum < mDisplayBound.top) {
            int delta = datum - mDisplayBound.top;
            adjustDisplayingBound(delta);
        } else if (datum > mDisplayBound.bottom) {
            int delta = datum - mDisplayBound.bottom + mThumbHeight;
            adjustDisplayingBound(delta);
        }
        refreshDisplayingItem();
        this.setVisibility(VISIBLE);
        fadein();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mScroller.computeScrollOffset()) {
            invalidate();
        }

        if (mInitFlag) {
            mDisplayBound.right = getWidth();
            mDisplayBound.bottom = getHeight();
            mThumbWidth = (mDisplayBound.width() - (COLUMN - 1) * COLUMN_MARGIN) / COLUMN;
            mThumbHeight = mThumbWidth;
            mTitleHeight = (int) ((float) mThumbHeight / TITLE_RATIO);
            mEndline = (mAdapter.getCount() / COLUMN) * (mThumbHeight + LINE_MARGIN) + mThumbHeight;
            mEndline = mEndline < mDisplayBound.height() ? mDisplayBound.height() : mEndline;
            refreshDisplayingItem();
            mInitFlag = false;
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
        if (mEndline < mDisplayBound.bottom) {
            int delta = mDisplayBound.bottom - mEndline;
            mDisplayBound.top -= delta;
            mDisplayBound.bottom -= delta;
        }
    }

    public class AlbumSetGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            int position = (int) (((mDisplayBound.top + event.getY())
                    / (mThumbHeight + LINE_MARGIN))) * COLUMN
                    + (int) (event.getX() / (mThumbWidth + COLUMN_MARGIN));
            Toast.makeText(getContext(), position + "clicked", Toast.LENGTH_SHORT).show();
            AlbumSetArea mTarget = null;
            for (LHItem child : mChildren) {
                if (child instanceof AlbumSetArea
                        && position == ((AlbumSetArea) child).getPosition()) {
                    mTarget = (AlbumSetArea) child;
                    break;
                }
            }
            if (null == mTarget) {
                return true;
            }
            mCallback.headToAlbum(mAdapter.getAlbumInfo(position).getAlbumId(),mTarget.getBound());
            fadeout(new AnimationCallback() {
                @Override
                public void animationFinished() {
                    setVisibility(GONE);
                }
            });
            return true;
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
        refreshDisplayingItem();
        invalidate();
    }

    @Override
    public void refreshDisplayingItem() {
        int singlePageThumbAmount = COLUMN * (mDisplayBound.height() / (mThumbHeight + LINE_MARGIN))
                + COLUMN;
        int pastThumbAmount = COLUMN
                * ((mDisplayBound.top - ANCHOR) / (mThumbHeight + LINE_MARGIN));
        if (0 != (mDisplayBound.top - ANCHOR) % (mThumbHeight + LINE_MARGIN)) {
            pastThumbAmount -= COLUMN;
            singlePageThumbAmount += 2 * COLUMN;
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
            float x = (i % COLUMN) * (mThumbWidth + COLUMN_MARGIN);
            float y = (i / COLUMN) * (mThumbHeight + LINE_MARGIN) - mDisplayBound.top;
            Log.v(TAG, "coordinate(" + x + "," + y + ")");
            AlbumSetArea item = mAdapter.getItem(this, i,
                    mThumbWidth - mTitleHeight * 2,
                    mThumbHeight - mTitleHeight * 2);
            if (null != item) {
                item.setPosition(i);
                item.setLeft((int) x + mTitleHeight);
                item.setTop((int) y);
                Log.v(TAG, item.getSrc() + "");
                mChildren.add(item);
                mChildren.add(new AlbumTitle(x, y + mThumbHeight - mTitleHeight, mThumbWidth, mTitleHeight, mAdapter.getItemTitle(i), true));
            }
        }
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

    public void setInitFlag(boolean init) {
        mInitFlag = init;
    }

}
