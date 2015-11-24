package com.example.administrator.album.ui;

import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Toast;

import com.example.administrator.album.BitmapLoader;
import com.example.administrator.album.MyAnimator;
import com.example.administrator.album.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-13.
 */
public class LHView extends View {
    private final static String TAG = "MyCustomedView";
    private final static int COLUMN = 4;
    private final static int LINE_MARGIN = 5;
    private final static int COLUMN_MARGIN = 5;
    private final static int ANCHOR = 0;
    private final static int DEFAULT_TEST_ALBUM_ID = -17_3977_3001;
    public static final int FLING_VELOCITY_DOWNSCALE = 3;

    private int mDisplayMode = ALBUM;
    private final static int SINGLE_IMAGE = 2;
    private final static int ALBUM = 1;
    private final static int ALBUMSET = 0;

    private int endLine = ANCHOR;
    private int thumbWidth;
    private int thumbHeight;

    private Rect mEndDestBound = new Rect();

    private Rect mDisplayBound;

    private MyImageAdapter mAdapter;

    private List<ImageArea> mChildren;

    private GestureDetectorCompat mDetector;

    private Bitmap snapshot;

    // private Scroller mScroller;
    private OverScroller mScroller;
    private ValueAnimator mScrollAnimator;

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
        mDisplayBound = new Rect(0, 0, 0, 0);
        mChildren = new ArrayList<>();
        mAdapter = new MyImageAdapter(context, DEFAULT_TEST_ALBUM_ID);
        mDetector = new GestureDetectorCompat(context, new MyGestureListener());
        // mScroller = new Scroller(context, null, true);
        mScroller = new OverScroller(context);
        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tickScrollAnimation();
            }
        });
        setDrawingCacheEnabled(true);
    }

    private void tickScrollAnimation() {
        int mLastPosition;
        if (!mScroller.isFinished()) {
            mLastPosition = mScroller.getCurrY();
            mScroller.computeScrollOffset();
            adjustDisplayingBound(mLastPosition - mScroller.getCurrY());
        } else {
            mScrollAnimator.cancel();
        }
    }

    private void adjustDisplayingBound(int deltaY) {
        mDisplayBound.top += deltaY;
        mDisplayBound.bottom += deltaY;
        checkBoundLimit();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean hasMoreFrame = false;
        switch (mDisplayMode) {
            case ALBUMSET: {
                break;
            }
            case ALBUM: {
                if (0 == mDisplayBound.height()) {
                    mDisplayBound.right = getWidth();
                    mDisplayBound.bottom = getHeight();
                    thumbWidth = (mDisplayBound.width() - (COLUMN - 1) * COLUMN_MARGIN) / COLUMN;
                    thumbHeight = thumbWidth;
                    endLine = (mAdapter.getCount() / COLUMN) * (thumbHeight + LINE_MARGIN)
                            + thumbHeight;
                    endLine = endLine < mDisplayBound.height() ? mDisplayBound.height() : endLine;
                }
                int singlePageThumbAmount = COLUMN
                        * (mDisplayBound.height() / (thumbHeight + LINE_MARGIN)) + COLUMN;
                int pastThumbAmount = COLUMN
                        * ((mDisplayBound.top - ANCHOR) / (thumbHeight + LINE_MARGIN));
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
                    float y = (i / COLUMN) * (thumbHeight + LINE_MARGIN) + LINE_MARGIN
                            - mDisplayBound.top;
                    ImageArea item = mAdapter.getImageArea(this, i, thumbWidth, thumbHeight);
                    if (null != item) {
                        item.setPosition(i);
                        Rect dest = new Rect((int) x, (int) y, (int) (x + thumbWidth),
                                (int) (y + thumbHeight));
                        item.setDestBound(dest);
                        mChildren.add(item);
                    }
                }

                canvas.save();
                for (ImageArea child : mChildren) {
                    if (null != child) {
                        hasMoreFrame |= child.draw(canvas, false);
                    }
                }
                canvas.restore();
                break;
            }
            case SINGLE_IMAGE: {
                if (!mChildren.isEmpty()) {
                    ImageArea item = mChildren.get(0);
                    ImageArea newItem = mAdapter.getFullSizeImageArea(this, item.getPosition(),
                            getWidth(), getHeight());
                    // put the
                    if (null != newItem) {
                        item.setSrc(newItem.getSrc());
                        mEndDestBound.left = 0;
                        mEndDestBound.top = 0;
                        mEndDestBound.right = getWidth();
                        mEndDestBound.bottom = getHeight();
                    }
                    if (null != item) {
                        item.getAnimator().setEndSrcBound(mEndDestBound);
                        hasMoreFrame |= item.draw(canvas, true);
                    }
                }
                break;
            }
        }
        if (hasMoreFrame) {
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mDetector.onTouchEvent(event);
    }

    public class MyImageAdapter {
        private List<Image> mData;
        private Context mContext;
        private int mAlbumId;
        private BitmapLoader loader;

        public MyImageAdapter(Context context, int albumId) {
            mData = new ArrayList<>();
            mContext = context;
            mAlbumId = albumId;
            loader = BitmapLoader.getInstance();
            loadData(mAlbumId);
        }

        public int getCount() {
            return mData.size();
        }

        public ImageArea getImageArea(LHView view, int position, int width, int height) {
            return loader.getThumb(view, mData.get(position).getImagePath(), width, height);
        }

        public ImageArea getFullSizeImageArea(LHView view, int position, int width, int height) {
            return loader.getFullSizeImageArea(view, mData.get(position).getImagePath(), width,
                    height);
        }

        private void loadData(final int albumId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cursor cur;
                    ContentResolver contentResolver = mContext.getContentResolver();
                    String where = MediaStore.Images.Media.BUCKET_ID + "=?";
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    cur = contentResolver.query(uri, Image.PROJECTION, where,
                            new String[] { albumId + "" }, MediaStore.Images.Media.DATE_ADDED);
                    if (cur != null && cur.moveToLast()) {
                        do {
                            String data = cur.getString(Image.PROJECTION_DATA);
                            int albumId = cur.getInt(Image.PROJECTION_BUCKET_ID);
                            if (data != null) {
                                mData.add(new Image(data, albumId));
                            }
                        } while (cur.moveToPrevious());
                        cur.close();
                    }
                }
            }).run();
        }
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

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            switch (mDisplayMode) {
                case ALBUMSET: {
                    break;
                }
                case ALBUM: {
                    buildDrawingCache();
                    snapshot = getDrawingCache();
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
                    mDisplayMode = SINGLE_IMAGE;
                    mChildren.clear();
                    ImageArea item = mAdapter.getFullSizeImageArea(LHView.this, position,
                            getWidth(), getHeight());
                    MyAnimator animator;
                    if (null != item) {
                        animator = new MyAnimator(new Rect(0, 0, getWidth(), getHeight()),
                                item.getSrcBound(), snapshot);
                        item.setPosition(position);
                        // Rect dest = new Rect(0, 0, getWidth(), getHeight());
                        item.setDestBound(mTarget.getDestBound());
                        item.setPortion(mTarget.getDisplayXPortion(), mTarget.getDisplayYPortion());
                        item.setAnimator(animator);
                    } else {
                        animator = new MyAnimator(new Rect(0, 0, getWidth(), getHeight()),
                                mTarget.getSrcBound(), snapshot);
                        item = new ImageArea(mTarget);
                        item.setAnimator(animator);
                        animator.init(item);
                        Log.v(TAG,
                                "getWidth:" + getWidth() + " getHeight:" + getHeight()
                                        + " mTarget.getWidth:" + mTarget.getWidth()
                                        + " mTarget.getHeight:" + mTarget.getHeight());
                    }
                    mChildren.add(item);
                    invalidate();
                    break;
                }
                case SINGLE_IMAGE: {
                    mDisplayMode = ALBUM;
                    invalidate();
                    break;
                }

            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scroll(distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.forceFinished(true);
            mScroller.fling(mScroller.getCurrX(), mScroller.getCurrY(), 0,
                    (int) velocityY / FLING_VELOCITY_DOWNSCALE, 0, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 10, 10);
            // mScrollAnimator.setDuration(mScroller.getDuration());
            // mScrollAnimator.start();
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
}
