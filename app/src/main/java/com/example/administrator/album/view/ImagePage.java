package com.example.administrator.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;

import com.example.administrator.album.adapter.ImageAdapter;
import com.example.administrator.album.animator.ScaleAnimator;
import com.example.administrator.album.util.RegionModify;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-24.
 */
public class ImagePage extends LHView {
    private final static String TAG = "ImagePage";
    private final static int DEFAULT_TEST_ALBUM_ID = -17_3977_3001;
    private static final int FLING_VELOCITY_DOWNSCALE = 3;

    private ImageAdapter mAdapter;
    private Rect mEndSrcBound;
    private Callback mCallback;
    private GestureDetectorCompat mDetector;
    private OverScroller mScroller;
    private Context mContext;

    private static final int ANCHOR = 0;
    private static final int MARGIN = 20;
    private int mScrollerBound;//limits the farest place the scroller can go

    private ImageArea mThumb;

    public interface Callback {
        void albumSync(int position);

        void backToAlbum(int position);

        ImageArea.ImageAreaAttribute getAnimationDestBound(int position);

        void animationFinished();
    }

    public ImagePage(Context context, ImageAdapter adapter, Callback callback, int albumId, ImageArea item) {
        super(context);
        mContext = context;
        mEndSrcBound = new Rect();
        mAdapter = adapter;
        refreshScrollBound();
        this.mCallback = callback;
        mDetector = new GestureDetectorCompat(context, new ImageGestureListener());
        mThumb = item;
        mScroller = new OverScroller(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        show(mThumb, true, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mScroller.computeScrollOffset()) {
            invalidate();
        }else {
            checkLimitBound();
        }
        refreshDisplayingItem();
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (MotionEvent.ACTION_CANCEL == action || MotionEvent.ACTION_UP == action) {
            springBack();
        }
        return mDetector.onTouchEvent(event);
    }

    @Override
    public void refreshDisplayingItem() {
        int mDisplayingBound = mScroller.getCurrX();
        List<LHItem> usefulItem = new ArrayList<LHItem>();

        int position = mDisplayingBound / (getWidth() + MARGIN);
        int counter = 1;
        ImageArea item;
        boolean thumbFlag;
        if (0 < mDisplayingBound % (getWidth() + MARGIN)) {
            counter++;
        }
        for (int i = 0; i < counter && position < mAdapter.getCount(); i++, position++) {
            item = null;
            thumbFlag = false;
            if (null != mChildren && !mChildren.isEmpty()) {
                for (LHItem child : mChildren) {
                    if (null != child && position == ((ImageArea) child).getPosition()) {
                        item = (ImageArea) child;
                        break;
                    }
                }
            }
            if (null != item) {
                if (item.isFullSize()) {
                    usefulItem.add(item);
                    int x = position * (getWidth() + MARGIN) - mDisplayingBound;
                    item.setDestBound(new Rect(x, 0, x + getWidth(), getHeight()));
                    adjustDestBound(item);
                } else {
                    thumbFlag = true;
                }
            }
            if (null == item || thumbFlag) {
                ImageArea bigItem = mAdapter.getFullSizeImageArea(this, position, getWidth(),
                        getHeight());
                if (null != bigItem) {
                    if (thumbFlag) {
                        item.setSrc(bigItem.getSrc());
                    } else {
                        item = bigItem;
                        item.setPosition(position);
                        int x = position * (getWidth() + MARGIN) - mDisplayingBound;
                        item.setDestBound(new Rect(x, 0, x + getWidth(), getHeight()));
                        adjustDestBound(item);
                    }
                    item.setFullSizeFlag(true);
                    usefulItem.add(item);
                } else {
                    if (thumbFlag) {
                        usefulItem.add(item);
                        int x = position * (getWidth() + MARGIN) - mDisplayingBound;
                        item.setDestBound(new Rect(x, 0, x + getWidth(), getHeight()));
                        adjustDestBound(item);
                    }
                }
            }
        }
        if (null != mChildren) {
            mChildren.clear();
            mChildren = (List<LHItem>) usefulItem;
        }
    }

    private void adjustDestBound(ImageArea item) {

        if (item.hasSrc() && null != item.getDestBound()) {
            Rect destBound = item.getDestBound();
            int destWidth = destBound.width();
            int destHeight = destBound.height();

            float realRatio = (float) destWidth / (float) destHeight;
            float targetRatio = item.getSrcRatio();

            if (realRatio > targetRatio && 0 != targetRatio) {
                int width = (int) (targetRatio * destHeight);
                int offsetX = (destWidth - width) / 2;
                destBound.left += offsetX;
                destBound.right -= offsetX;
                Log.v(TAG, "targetRatio:" + targetRatio + " destHeight:" + destHeight);
                Log.v(TAG, "width:" + width + " destWidth:" + destWidth + " " + destBound + "");
            } else {
                int height = (int) (destWidth / targetRatio);
                int offsetY = (destHeight - height) / 2;
                destBound.top += offsetY;
                destBound.bottom -= offsetY;
            }
        }
    }

    public void show(ImageAdapter adapter, int albumId, ImageArea item, boolean scale, boolean alpha) {
        mAdapter = adapter;
        refreshScrollBound();
        show(item, scale, alpha);
    }

    public void show(ImageArea item, boolean scale, boolean alpha) {
        // if(alpha){
        // item.setAlpha(HIDE_ALPHA);
        // AlphaAnimator alphaAnimator = new
        // AlphaAnimator(item.getAlpha(),SHOW_ALPHA);
        // item.addAnimator(alphaAnimator);
        // }
        if (scale) {
            Rect dest = new Rect(0, 0, getWidth(), getHeight());
            RegionModify.clipDestBoundBySrc(item, dest);
            ScaleAnimator scaleAnimator = new ScaleAnimator(item.getSrcBound(), item.getSrcSize(),
                    item.getDestBound(), dest);
            Log.v(TAG, item.getDestBound() + "");
            item.addAnimator(scaleAnimator);
        }

        mChildren.clear();
        mChildren.add(item);
        mScroller.forceFinished(true);
        mScroller.startScroll(item.getPosition() * (getWidth() + MARGIN), 0, 0, 0);
        invalidate();
    }

    /**
     * calculates and sets the variable mScrollerBound
     * by multipling the width of screen by the amount of pics
     */
    private void refreshScrollBound() {
        if (null != mAdapter) {
            mScrollerBound = (mAdapter.getCount() - 1) * getWidth();
        }
    }

    public class ImageGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            backToAlbum();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScroller.forceFinished(true);
            mScroller.startScroll(mScroller.getFinalX(), 0, (int) distanceX, 0);
            invalidate();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.forceFinished(true);
            mScroller.fling(mScroller.getCurrX(), 0, -(int) (velocityX / FLING_VELOCITY_DOWNSCALE),
                    0, 0, mScrollerBound, 0, 0);
            invalidate();
            return true;
        }

    }

    private void showNext() {
        int width = getWidth() + MARGIN;
        mScroller.forceFinished(true);
        int redundant = mScroller.getFinalX() % width;
        if (0 < redundant) {
            mScroller.startScroll(mScroller.getFinalX(), 0, redundant, 0);
        } else {
            mScroller.startScroll(mScroller.getFinalX(), 0, width, 0);
        }
        invalidate();
    }

    private void showLast() {
        int width = getWidth() + MARGIN;
        mScroller.forceFinished(true);
        int redundant = mScroller.getFinalX() % width;
        if (0 < redundant) {
            mScroller.startScroll(mScroller.getFinalX(), 0, -(width - redundant), 0);
        } else {
            mScroller.startScroll(mScroller.getFinalX(), 0, -width, 0);
        }
        invalidate();
    }

    private void checkLimitBound() {
        int width = getWidth() + MARGIN;
        int redundant = mScroller.getCurrX() % width;
        if (0 != redundant) {
            if (redundant > width / 2) {
                mScroller.forceFinished(true);
                mScroller.startScroll(mScroller.getFinalX(), 0, width - redundant, 0);
                invalidate();
            } else {
                mScroller.forceFinished(true);
                mScroller.startScroll(mScroller.getFinalX(), 0, -redundant, 0);
                invalidate();
            }
        }
    }

    private void springBack(){
        if(mScroller.springBack(mScroller.getCurrX(),0,0,mScrollerBound,0,0)) {
            invalidate();
        }
    }

    public void backToAlbum() {
        Log.v(TAG, "Scroller currentX:" + mScroller.getCurrX());
        final int position = (int) (mScroller.getCurrX() / (getWidth() + MARGIN));

        mCallback.albumSync(position);
        ImageArea.ImageAreaAttribute attribute = mCallback.getAnimationDestBound(position);
        if (null != mChildren && !mChildren.isEmpty() && null != attribute) {
            for (LHItem child : mChildren) {
                Log.v(TAG, ((ImageArea) child).getPosition() + "");
                if (null != child && position == ((ImageArea) child).getPosition()) {
                    Rect destSrc = ((ImageArea) child).getSrcSize();
                    int offsetX = (int) (destSrc.width() * (1 - attribute.mDisplayXPortion)
                            / 2);
                    int offsetY = (int) (destSrc.height() * (1 - attribute.mDisplayYPortion)
                            / 2);
                    destSrc.top += offsetY;
                    destSrc.bottom -= offsetY;
                    destSrc.left += offsetX;
                    destSrc.right -= offsetX;
                    ScaleAnimator scaleAnimator = new ScaleAnimator(
                            ((ImageArea) child).getSrcBound(), destSrc,
                            ((ImageArea) child).getDestBound(), attribute.mDestBound,
                            new ScaleAnimator.Callback() {
                                @Override
                                public void animationFinished() {
                                    mCallback.animationFinished();
                                }
                            });
                    child.addAnimator(scaleAnimator);
                }
            }
        }
        mCallback.backToAlbum(position);

        invalidate();
    }
}
