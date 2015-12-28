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

import com.example.administrator.album.adapter.ImageAdapter;
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

    private int mEndline = ANCHOR;
    private int mThumbWidth;
    private int mThumbHeight;

    private Rect mDisplayBound;
    private GestureDetectorCompat mDetector;

    private ImageAdapter mAdapter;
    private OverScroller mScroller;

    private Callback mCallback;
    private Context mContext;

    private int mAlbumId = DEFAULT_TEST_ALBUM_ID;

    private boolean mInitFlag = true;

    public interface Callback {
        void headToImage(ImageAdapter adapter,int albumId, ImageArea item);
    }

    public AlbumPage(Context context, Callback callback) {
        super(context);
        init(context, callback);
    }

    private void init(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
        mAdapter = new ImageAdapter(context, DEFAULT_TEST_ALBUM_ID);
        mDetector = new GestureDetectorCompat(context, new AlbumGestureListener());
        mScroller = new OverScroller(context);
    }

    public void setAlbum(int albumId) {
        mAdapter = new ImageAdapter(mContext, albumId);
        mAlbumId = albumId;
        refreshParams();
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
        fadein(position);
    }

    /**
     * Shows the items(except which in the exception scope) with fade in animation
     *
     * @param exceptions
     */
    private void fadein(int... exceptions) {
        if (null != mChildren && !mChildren.isEmpty()) {
            for (LHItem item : mChildren) {
                if (null != item && item instanceof ImageArea
                        && inScope(((ImageArea) item).getPosition(), exceptions)) {
                    item.setHideFlag(true);
                }
            }
        }
        super.fadein();
    }

    /**
     * Checks if this number is in the exception scope
     *
     * @param target     number to be find
     * @param exceptions exception scope
     * @return if this number not in return false
     */
    private boolean inScope(int target, int[] exceptions) {
        for (int i : exceptions) {
            if (target == i) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mScroller.computeScrollOffset()) {
            invalidate();
        }

        if (mInitFlag) {
            refreshParams();
            mInitFlag = false;
        }

        super.onDraw(canvas);
    }


    private void refreshParams() {
        mDisplayBound = new Rect(0, 0, 0, 0);
        mDisplayBound.right = getWidth();
        mDisplayBound.bottom = getHeight();
        mThumbWidth = (mDisplayBound.width() - (COLUMN - 1) * COLUMN_MARGIN) / COLUMN;
        mThumbHeight = mThumbWidth;
        mEndline = (mAdapter.getCount() / COLUMN) * (mThumbHeight + LINE_MARGIN) + mThumbHeight;
        mEndline = mEndline < mDisplayBound.height() ? mDisplayBound.height() : mEndline;
        refreshDisplayingItem();
    }

    @Override
    public void refreshDisplayingItem() {
        int singlePageThumbAmount = COLUMN * (mDisplayBound.height() / (mThumbHeight + LINE_MARGIN))
                + COLUMN;
        int pastThumbAmount = COLUMN * ((mDisplayBound.top - ANCHOR) / (mThumbHeight + LINE_MARGIN));
        if (0 != (mDisplayBound.top - ANCHOR) % (mThumbHeight + LINE_MARGIN)) {
            pastThumbAmount -= COLUMN;
            singlePageThumbAmount += COLUMN * 2;
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
            ImageArea item = mAdapter.getImageArea(this, i, mThumbWidth, mThumbHeight);
            if (null != item) {
                item.setPosition(i);
                Rect dest = new Rect((int) x, (int) y, (int) (x + mThumbWidth),
                        (int) (y + mThumbHeight));
                item.setDestBound(dest);
                mChildren.add(item);
            }
        }
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

    public class AlbumGestureListener extends GestureDetector.SimpleOnGestureListener {
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
            ImageArea mTarget = null;
            for (LHItem child : mChildren) {
                if (position == ((ImageArea) child).getPosition()) {
                    mTarget = (ImageArea) child;
                    break;
                }
            }
            if (null == mTarget) {
                return true;
            }
            mCallback.headToImage(mAdapter,mAlbumId, new ImageArea(mTarget));
            fadeout(new AnimationCallback() {
                @Override
                public void animationFinished() {
                    setVisibility(GONE);
                }
            });
            mTarget.setAlpha(0);
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

    public ImageArea.ImageAreaAttribute getLocation(int position) {
        if (null != mChildren && !mChildren.isEmpty()) {
            for (LHItem child : mChildren) {
                if (position == ((ImageArea) child).getPosition()) {
                    return ((ImageArea) child).getAttribute();
                }
            }
        }
        return null;
    }
}
