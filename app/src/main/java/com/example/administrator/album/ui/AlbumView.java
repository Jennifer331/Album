package com.example.administrator.album.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.administrator.album.BitmapLoader;
import com.example.administrator.album.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-13.
 */
public class AlbumView extends View {
    private final static String TAG = "MyCustomedView";
    private final static int COLUMN = 4;
    private final static int LINE_MARGIN = 5;
    private final static int COLUMN_MARGIN = 5;
    private final static int ANCHOR = 0;
    private final static int DEFAULT_TEST_ALBUM_ID = -17_3977_3001;
    private final static int DEFAULT_TEST_DECODE_LIMIT = 1000;

    private int touchState = NO_STATE;
    private final static int MOVED = 2;
    private final static int CLICKED = 1;
    private final static int NO_STATE = 0;

    private int endLine = ANCHOR;
    private int thumbWidth;
    private int thumbHeight;

    private float lastPointY = 0;

    private Rect mDisplayBound;

    private MyImageAdapter mAdapter;

    private LruCache<Integer, ImageDrawable> memCache;

    private GestureDetectorCompat mDetector;

    public AlbumView(Context context) {
        this(context, null);
    }

    public AlbumView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AlbumView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mDisplayBound = new Rect(0, 0, 0, 0);

        mAdapter = new MyImageAdapter(context, DEFAULT_TEST_ALBUM_ID);

        mDetector = new GestureDetectorCompat(context, new MyGestureListener());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(0 == mDisplayBound.height()){
            mDisplayBound.right = getWidth();
            mDisplayBound.bottom = getHeight();
            thumbWidth = (mDisplayBound.width() - (COLUMN - 1) * COLUMN_MARGIN) / COLUMN;
            thumbHeight = thumbWidth;
            endLine = (mAdapter.getCount() / COLUMN) * (thumbHeight + LINE_MARGIN) + thumbHeight;
            endLine = endLine < mDisplayBound.height() ? mDisplayBound.height() : endLine;
        }
        int singlePageThumbAmount = COLUMN * (mDisplayBound.height() / (thumbHeight + LINE_MARGIN)) + COLUMN;
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
        canvas.save();
        {
            if (singlePageThumbAmount <= 0)
                return;

            for (int i = pastThumbAmount; mAdapter.getCount() > i
                    && i < pastThumbAmount + singlePageThumbAmount; i++) {
                float x = (i % COLUMN) * thumbWidth + (i % COLUMN - 1) * COLUMN_MARGIN;
                float y = (i / COLUMN) * (thumbHeight + LINE_MARGIN) + LINE_MARGIN
                        - mDisplayBound.top;
                Bitmap bitmap = mAdapter.getBitmap(this, i, thumbWidth, thumbHeight);
                if (null == bitmap) {
                    return;
                }
                canvas.drawBitmap(bitmap, x, y, null);
            }
        }
        canvas.restore();
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

        public Bitmap getBitmap(AlbumView view, int position, int width, int height) {
            return loader.getThumb(view, mData.get(position).getImagePath(), width, height);
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
//        Toast.makeText(this.getContext(),"compiteScroll",Toast.LENGTH_SHORT).show();
        super.computeScroll();
    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            int position = (int) (((mDisplayBound.top + event.getY())
                    / (thumbHeight + LINE_MARGIN))) * COLUMN
                    + (int) (event.getX() / (thumbWidth + COLUMN_MARGIN)) + 1;
            Log.v(TAG,
                    " Bound top :" + mDisplayBound.top + " event x:" + event.getRawX() + " event y:"
                            + event.getRawY() + " thumbWidth: " + thumbWidth + "thumbHeight"
                            + thumbHeight);
            Log.v(TAG,
                    "line :" + (int) (((mDisplayBound.top + event.getRawY())
                            / (thumbHeight + LINE_MARGIN)) * COLUMN) + " column :"
                            + (int) (event.getRawX() / (thumbWidth + COLUMN_MARGIN)) + "");
            Toast.makeText(AlbumView.this.getContext(),
                    "x:" + event.getX() + " " + "y:" + event.getY() + " " + position + "clicked",
                    Toast.LENGTH_SHORT).show();
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