package com.example.administrator.album;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;

import com.example.administrator.album.ui.LHView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lei Xiaoyue on 2015-11-16.
 */
public class BitmapLoader implements ThumbDecodeRunnable.Callback, FullSizeDecodeRunnable.Callback {
    private static final int DEFAULT_MEM_SIZE = 1024 * 1024 * 100;// 100MB
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int THUMB_DECODE_DONE = 0;
    private static final int FULL_SIZE_DECODE_DONE = 1;

    private static final TimeUnit KEEP_ALICE_TIME_UNIT;

    private final BlockingQueue<Runnable> mDecodeWorkQueue;
    private final ThreadPoolExecutor mDecodeThreadPool;
    private LruCache<String, Bitmap> mThumbCache;
    private LruCache<String, Bitmap> mFullSizeCache;

    private static BitmapLoader mInstance = null;

    private static Handler mHandler;

    static {
        KEEP_ALICE_TIME_UNIT = TimeUnit.SECONDS;
        mInstance = new BitmapLoader();
    }

    public static BitmapLoader getInstance() {
        return mInstance;
    }

    private BitmapLoader() {
        mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
        mDecodeThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALICE_TIME_UNIT, mDecodeWorkQueue);
        mThumbCache = new LruCache<String, Bitmap>(DEFAULT_MEM_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mFullSizeCache = new LruCache<String, Bitmap>(DEFAULT_MEM_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case THUMB_DECODE_DONE: {
                        BitmapAndView info = (BitmapAndView) msg.obj;
                        info.getView().invalidate();
                        break;
                    }
                    case FULL_SIZE_DECODE_DONE: {
                        BitmapAndView info = (BitmapAndView) msg.obj;
                        info.getView().invalidate();
                        break;
                    }
                }
            }
        };
    }

    public Bitmap getThumb(LHView view, String path, int thumbWidth, int thumbHeight) {
        // find in memory first
        Bitmap bitmap = mThumbCache.get(path);
        if (null != bitmap)
            return bitmap;

        // then check if the task already existed
        LHImageDecodeRunnable[] runnableArray = new LHImageDecodeRunnable[mInstance.mDecodeWorkQueue
                .size()];
        mInstance.mDecodeWorkQueue.toArray(runnableArray);
        for (LHImageDecodeRunnable runnable : runnableArray) {
            if (null != runnable && runnable.getPath().equals(path)
                    && thumbWidth == runnable.getWidth() && thumbHeight == runnable.getHeight())
                return null;
        }

        // to load
        mInstance.mDecodeThreadPool
                .execute(new ThumbDecodeRunnable(view, this, path, thumbWidth, thumbHeight));
        return null;
    }

    public Bitmap getFullSizeBitmap(LHView view, String path, int width, int height) {
        // find in memory first
        Bitmap bitmap = mFullSizeCache.get(path);
        if (null != bitmap)
            return bitmap;

        // then check if the task already existed
        LHImageDecodeRunnable[] runnableArray = new LHImageDecodeRunnable[mInstance.mDecodeWorkQueue
                .size()];
        mInstance.mDecodeWorkQueue.toArray(runnableArray);
        for (LHImageDecodeRunnable runnable : runnableArray) {
            if (runnable.getPath().equals(path) && width == runnable.getWidth()
                    && height == runnable.getHeight())
                return null;
        }

        // to load
        mInstance.mDecodeThreadPool
                .execute(new FullSizeDecodeRunnable(view, this, path, width, height));
        return null;
    }

    @Override
    public void handleThumbDecodeDone(LHView view, String path, Bitmap bitmap) {
        if (null != mThumbCache && null != bitmap) {
            synchronized (mThumbCache) {
                if (null == mThumbCache.get(path)) {
                    mThumbCache.put(path, bitmap);
                }
            }
        }

        if (null != bitmap) {
            BitmapAndView obj = new BitmapAndView(null, view);
            Message completeMessage = mHandler.obtainMessage(THUMB_DECODE_DONE, obj);
            completeMessage.sendToTarget();
        }
    }

    @Override
    public void handleFullSizeDecodeDone(LHView view, String path, Bitmap bitmap) {
        if (null != mFullSizeCache && null != bitmap) {
            synchronized (mFullSizeCache) {
                if (null == mFullSizeCache.get(path)) {
                    mFullSizeCache.put(path, bitmap);
                }
            }
        }

        if (null != bitmap) {
            BitmapAndView obj = new BitmapAndView(null, view);
            Message completeMessage = mHandler.obtainMessage(FULL_SIZE_DECODE_DONE, obj);
            completeMessage.sendToTarget();
        }
    }

    public class BitmapAndView {
        private Bitmap bitmap;
        private LHView view;

        public BitmapAndView(Bitmap bitmap, LHView view) {
            this.bitmap = bitmap;
            this.view = view;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public LHView getView() {
            return view;
        }

        public void setView(LHView view) {
            this.view = view;
        }
    }
}
