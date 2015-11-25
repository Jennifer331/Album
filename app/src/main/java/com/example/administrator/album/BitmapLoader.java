package com.example.administrator.album;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.example.administrator.album.view.AlbumPage;
import com.example.administrator.album.view.ImageArea;
import com.example.administrator.album.view.ImagePage;
import com.example.administrator.album.view.LHView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lei Xiaoyue on 2015-11-16.
 */
public class BitmapLoader implements ThumbDecodeRunnable.Callback, FullSizeDecodeRunnable.Callback {
    private static final String TAG = "BitmapLoader";

    private static final int DEFAULT_MEM_SIZE = 1024 * 1024 * 100;// 100MB
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int THUMB_DECODE_DONE = 0;
    private static final int FULL_SIZE_DECODE_DONE = 1;

    private static final TimeUnit KEEP_ALICE_TIME_UNIT;

    private final BlockingQueue<Runnable> mDecodeWorkQueue;
    private final ThreadPoolExecutor mDecodeThreadPool;
    private LruCache<String, ImageArea> mThumbCache;
    private LruCache<String, ImageArea> mFullSizeCache;

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
        mThumbCache = new LruCache<String, ImageArea>(DEFAULT_MEM_SIZE) {
            @Override
            protected int sizeOf(String key, ImageArea value) {
                return value.getSrc().getByteCount();
            }
        };
        mFullSizeCache = new LruCache<String, ImageArea>(DEFAULT_MEM_SIZE) {
            @Override
            protected int sizeOf(String key, ImageArea value) {
                return value.getSrc().getByteCount();
            }
        };

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case THUMB_DECODE_DONE: {
                        BitmapAndView info = (BitmapAndView) msg.obj;
                        ((AlbumPage)info.getView()).refreshDisplayingItem();
                        info.getView().invalidate();
                        break;
                    }
                    case FULL_SIZE_DECODE_DONE: {
                        BitmapAndView info = (BitmapAndView) msg.obj;
                        ((ImagePage)info.getView()).refreshDisplayingItem();
                        info.getView().invalidate();
                        break;
                    }
                }
            }
        };
    }

    public ImageArea getThumb(LHView view, String path, int thumbWidth, int thumbHeight) {
        // find in memory first
        ImageArea item = mThumbCache.get(path);
        if (null != item)
            return item;

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

    public ImageArea getFullSizeImageArea(LHView view, String path, int width, int height) {
        // find in memory first
        ImageArea item = mFullSizeCache.get(path);
        if (null != item)
            return item;

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
    public void handleThumbDecodeDone(LHView view, String path, ImageArea item) {
        Log.v(TAG,null == item.getSrcBound()?"null rect" :item.getSrcBound().toString());
        if (null != mThumbCache && null != item) {
            synchronized (mThumbCache) {
                if (null == mThumbCache.get(path)) {
                    mThumbCache.put(path, item);
                }
            }
        }

        if (null != item) {
            BitmapAndView obj = new BitmapAndView(null, view);
            Message completeMessage = mHandler.obtainMessage(THUMB_DECODE_DONE, obj);
            completeMessage.sendToTarget();
        }
    }

    @Override
    public void handleFullSizeDecodeDone(LHView view, String path, ImageArea item) {
        if (null != mFullSizeCache && null != item) {
            synchronized (mFullSizeCache) {
                if (null == mFullSizeCache.get(path)) {
                    mFullSizeCache.put(path, item);
                }
            }
        }

        if (null != item) {
            BitmapAndView obj = new BitmapAndView(null, view);
            Message completeMessage = mHandler.obtainMessage(FULL_SIZE_DECODE_DONE, obj);
            completeMessage.sendToTarget();
        }
    }

    public class BitmapAndView {
        private ImageArea item;
        private LHView view;

        public BitmapAndView(ImageArea item, LHView view) {
            this.item = item;
            this.view = view;
        }

        public ImageArea getItem() {
            return item;
        }

        public void setItem(ImageArea bitmap) {
            this.item = bitmap;
        }

        public LHView getView() {
            return view;
        }

        public void setView(LHView view) {
            this.view = view;
        }
    }
}
