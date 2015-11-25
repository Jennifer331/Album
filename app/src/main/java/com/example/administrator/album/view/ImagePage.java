package com.example.administrator.album.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.administrator.album.adapter.MyImageAdapter;
import com.example.administrator.album.animator.AlphaAnimator;
import com.example.administrator.album.animator.ScaleAnimator;

/**
 * Created by Lei Xiaoyue on 2015-11-24.
 */
public class ImagePage extends LHView {
    private final static String TAG = "ImagePage";
    private final static int DEFAULT_TEST_ALBUM_ID = -17_3977_3001;
    private static final int FADE_OUT_BEGIN_ALPHA = 0;
    private static final int FADE_OUT_END_ALPHA = 255;

    private MyImageAdapter mAdapter;
    private Rect mEndSrcBound;
    private Callback mCallback;
    private GestureDetectorCompat mDetector;

    private ImageArea mThumb;

    public interface Callback {
        public void headToAlbum(ImageArea item);
    }

    public ImagePage(Context context, Callback callback, ImageArea item) {
        super(context);
        mEndSrcBound = new Rect();
        mAdapter = new MyImageAdapter(context, DEFAULT_TEST_ALBUM_ID);
        this.mCallback = callback;
        mDetector = new GestureDetectorCompat(context, new ImageGestureListener());
        mThumb = item;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        show(mThumb);
    }

    private void initAnimator() {
        // ImageArea item = mAdapter.getFullSizeImageArea(ImagePage.this,
        // mThumb.getPosition(),
        // getWidth(), getHeight());
        // ScaleAnimator scaleAnimator;
        // if (null != item) {
        // scaleAnimator = new ScaleAnimator(new Rect(0, 0, getWidth(),
        // getHeight()),
        // item.getSrcSize());
        // item.setPosition(mThumb.getPosition());
        // item.setDestBound(mThumb.getDestBound());
        // item.setPortion(mThumb.getDisplayXPortion(),
        // mThumb.getDisplayYPortion());
        // } else {
        // scaleAnimator = new ScaleAnimator(new Rect(0, 0, getWidth(),
        // getHeight()),
        // mThumb.getSrcSize());
        // item = new ImageArea(mThumb);
        // }
        // AlphaAnimator alphaAnimator = new AlphaAnimator(FADE_OUT_END_ALPHA);
        // scaleAnimator.init(item);
        //// item.addAnimator(scaleAnimator);
        // item.addAnimator(alphaAnimator);
        // mChildren.add(item);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public void refreshDisplayingItem() {
        if (null != mChildren && !mChildren.isEmpty()) {
            ImageArea item = mChildren.get(0);
            if (!item.isFullSize()) {
                ImageArea newItem = mAdapter.getFullSizeImageArea(this, item.getPosition(),
                        getWidth(), getHeight());
                // put the
                if (null != newItem) {
                    // item.setSrc(newItem.getSrc());
                    // item.setFullSizeFlag(true);
                    // mEndSrcBound.left = 0;
                    // mEndSrcBound.top = 0;
                    // mEndSrcBound.right = (int) newItem.getWidth();
                    // mEndSrcBound.bottom = (int) newItem.getHeight();
                    // if (item.hasScaleAnimator()) {
                    // item.getScaleAnimator().setEndSrcBound(mEndSrcBound);
                    // }
                    // adjustDestBound(item);
                    newItem.setDestBound(new Rect(0,0,getWidth(),getHeight()));
                    mChildren.clear();
                    mChildren.add(newItem);
                }
            }
            invalidate();
        }
    }

    private void adjustDestBound(ImageArea item) {
        if (item.hasSrc() && null != item.getDestBound()) {
            Rect destBound = item.getDestBound();
            int destWidth = destBound.width();
            int destHeight = destBound.height();

            float realRatio = (float) destWidth / (float) destHeight;
            float targetRatio = item.getSrcRatio();

            if (realRatio > targetRatio) {
                int width = (int) (targetRatio * destHeight);
                int offsetX = (destWidth - width) / 2;
                destBound.left += offsetX;
                destBound.right -= offsetX;
            } else {
                int height = (int) (destWidth / targetRatio);
                int offsetY = (destHeight - height) / 2;
                destBound.top += offsetY;
                destBound.bottom -= offsetY;
            }
        }
    }

    public void show(ImageArea item) {
        if (getWidth() > 0 && getHeight() > 0) {
            ImageArea fullSizeImage = mAdapter.getFullSizeImageArea(ImagePage.this,
                    item.getPosition(), getWidth(), getHeight());
            Log.v(TAG, "getWidth:" + getWidth() + "getHeight:" + getHeight());
            if (null == fullSizeImage) {
                fullSizeImage = new ImageArea(item);
            }
            if (null != mChildren) {
                mChildren.clear();
                fullSizeImage.setDestBound(new Rect(0, 0, getWidth(), getHeight()));
                mChildren.add(fullSizeImage);
            }
            invalidate();
        }
    }

    public class ImageGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // fadeout();
            ImagePage.this.setVisibility(GONE);
            mCallback.headToAlbum(null);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    }
}
