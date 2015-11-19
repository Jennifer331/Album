package com.example.administrator.album.ui;

import android.animation.AnimatorSet;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.album.BitmapLoader;
import com.example.administrator.album.R;
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
    private final static int DEFAULT_TEST_DECODE_LIMIT = 1000;

    private int mDisplayMode = ALBUM;
    private final static int SINGLE_IMAGE = 2;
    private final static int ALBUM = 1;
    private final static int ALBUMSET = 0;

    private int endLine = ANCHOR;
    private int thumbWidth;
    private int thumbHeight;

    private float lastPointY = 0;

    private Rect mDisplayBound;

    private MyImageAdapter mAdapter;

    private LruCache<Integer, ImageDrawable> memCache;

    private List<ImageArea> mChildren;

    private GestureDetectorCompat mDetector;

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
        mDisplayBound = new Rect(0, 0, 0, 0);
        mChildren = new ArrayList<ImageArea>();
        mAdapter = new MyImageAdapter(context, DEFAULT_TEST_ALBUM_ID);

        mDetector = new GestureDetectorCompat(context, new MyGestureListener());

    }

    @Override
    protected void onDraw(Canvas canvas) {
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
                    Bitmap bitmap = mAdapter.getBitmap(this, i, thumbWidth, thumbHeight);
                    mChildren.add(new ImageArea(i, x, y, bitmap));
                }

                canvas.save();
                for (ImageArea child : mChildren) {
                    if (null != child) {
                        child.draw(canvas);
                    }
                }
                canvas.restore();
                break;
            }
            case SINGLE_IMAGE: {
                Bitmap bitmap = mAdapter.getFullSizeBitmap(this, mChildren.get(0).getPosition(),
                        getWidth(), getHeight());
                if (null != bitmap) {
                    canvas.drawBitmap(bitmap, 0f, 0f, null);
                }
                canvas.drawText("i love you", 50f, 50f, new Paint());
                break;
            }
        }
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
            mData = new ArrayList<Image>();
            mContext = context;
            mAlbumId = albumId;
            loader = BitmapLoader.getInstance();
            loadData(mAlbumId);
        }

        public int getCount() {
            return mData.size();
        }

        public Bitmap getBitmap(LHView view, int position, int width, int height) {
            return loader.getThumb(view, mData.get(position).getImagePath(), width, height);
        }

        public Bitmap getFullSizeBitmap(LHView view, int position, int width, int height) {
            return loader.getFullSizeBitmap(view, mData.get(position).getImagePath(), width,
                    height);
        }

        private void loadData(final int albumId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Cursor cur = null;
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
                    }
                }
            }).run();
        }
    }

    @Override
    public void computeScroll() {
        // Toast.makeText(this.getContext(),"compiteScroll",Toast.LENGTH_SHORT).show();
        super.computeScroll();
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
                    int position = (int) (((mDisplayBound.top + event.getY())
                            / (thumbHeight + LINE_MARGIN))) * COLUMN
                            + (int) (event.getX() / (thumbWidth + COLUMN_MARGIN)) + 1;
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
                    Bitmap bitmap = mAdapter.getFullSizeBitmap(LHView.this, position, getWidth(),
                            getHeight());
                    // Bitmap bitmap = mAdapter.getBitmap(LHView.this, position,
                    // getWidth(),
                    // getHeight());
                    mTarget.setX(0f);
                    mTarget.setY(0f);
                    mTarget.setSrc(bitmap);
                    mChildren.add(mTarget);
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
            mDisplayBound.top += distanceY;
            mDisplayBound.bottom += distanceY;
            checkLimit();
            invalidate();
            return true;
        }
    }

    private void checkLimit() {
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
