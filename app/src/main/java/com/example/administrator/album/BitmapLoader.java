package com.example.administrator.album;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.example.administrator.album.model.Album;
import com.example.administrator.album.task.AlbumSetDecodeRunnable;
import com.example.administrator.album.task.FullSizeDecodeRunnable;
import com.example.administrator.album.task.LHImageDecodeRunnable;
import com.example.administrator.album.task.ThumbDecodeRunnable;
import com.example.administrator.album.view.AlbumPage;
import com.example.administrator.album.view.AlbumSetArea;
import com.example.administrator.album.view.AlbumSetPage;
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
public class BitmapLoader implements ThumbDecodeRunnable.Callback, FullSizeDecodeRunnable.Callback,
        AlbumSetDecodeRunnable.Callback {
    private static final String TAG = "BitmapLoader";

    private static final int DEFAULT_MEM_SIZE = 1024 * 1024 * 100;// 100MB
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final int THUMB_DECODE_DONE = 0;
    private static final int FULL_SIZE_DECODE_DONE = 1;
    private static final int ALBEMSET_DECODE_DONE = 2;

    private static final TimeUnit KEEP_ALICE_TIME_UNIT;

    private final BlockingQueue<Runnable> mDecodeWorkQueue;
    private final ThreadPoolExecutor mDecodeThreadPool;
    private final LruCache<String, ImageArea> mThumbCache;
    private final LruCache<String, ImageArea> mFullSizeCache;
    private final LruCache<Integer, AlbumSetArea> mAblumSetCache;

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
        mAblumSetCache = new LruCache<Integer, AlbumSetArea>(DEFAULT_MEM_SIZE) {
            @Override
            protected int sizeOf(Integer key, AlbumSetArea value) {
                return value.getSrc().getByteCount();
            }
        };

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case THUMB_DECODE_DONE: {
                        BitmapAndView info = (BitmapAndView) msg.obj;
                        ((AlbumPage) info.getView()).refreshDisplayingItem();
                        info.getView().invalidate();
                        break;
                    }
                    case FULL_SIZE_DECODE_DONE: {
                        BitmapAndView info = (BitmapAndView) msg.obj;
                        ((ImagePage) info.getView()).refreshDisplayingItem();
                        info.getView().invalidate();
                        break;
                    }
                    case ALBEMSET_DECODE_DONE: {
                        BitmapAndView info = (BitmapAndView) msg.obj;
                        ((AlbumSetPage) info.getView()).refreshDisplayingItem();
                        info.getView().invalidate();
                        break;
                    }
                }
            }
        };
    }

    public AlbumSetArea getAlbumSet(LHView view, Album album, int pileNum, int width, int height) {
        // find in memory first
        AlbumSetArea item = mAblumSetCache.get(album.getAlbumId());
        if (null != item) {
            return item;
        }

        // else check if the task already existed
        AlbumSetDecodeRunnable[] runnableArray = new AlbumSetDecodeRunnable[mInstance.mDecodeWorkQueue
                .size()];
        mInstance.mDecodeWorkQueue.toArray(runnableArray);
        for (AlbumSetDecodeRunnable runnable : runnableArray) {
            if (null != runnable && runnable.getAlbumId() == album.getAlbumId()
                    && width == runnable.getWidth() && height == runnable.getHeight()) {
                return null;
            }
        }

        //to load
        mInstance.mDecodeThreadPool.execute(new AlbumSetDecodeRunnable(view, this, album, pileNum, width, height));
        return null;
    }

    public ImageArea getThumb(LHView view, String path, int thumbWidth, int thumbHeight) {
        // find in memory first
        ImageArea item = mThumbCache.get(path);
        if (null != item && !item.isSubstituent) {
            return item;
        }

        if(null == item) {
            item = makeSubstituent(thumbWidth, thumbHeight);
        }
        // then check if the task already existed
        LHImageDecodeRunnable[] runnableArray = new LHImageDecodeRunnable[mInstance.mDecodeWorkQueue
                .size()];
        mInstance.mDecodeWorkQueue.toArray(runnableArray);
        for (LHImageDecodeRunnable runnable : runnableArray) {
            if (null != runnable && runnable.getPath().equals(path)
                    && thumbWidth == runnable.getWidth() && thumbHeight == runnable.getHeight())
                return item;
        }

        // to load
        mInstance.mDecodeThreadPool
                .execute(new ThumbDecodeRunnable(view, this, path, thumbWidth, thumbHeight));
        return item;
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
        if (null != mThumbCache && null != item) {
            synchronized (mThumbCache) {
                ImageArea check = mThumbCache.get(path);
                if (null == check) {
                    mThumbCache.put(path, item);
                }else if(check.isSubstituent){
                    item.setAnimators(check.getAnimators());
                    mThumbCache.put(path,item);
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
    public void handleAlbumSetDecodeDone(LHView view, int albumId, AlbumSetArea item) {
        if (null != mAblumSetCache && null != item) {
            synchronized (mAblumSetCache) {
                if (null == mAblumSetCache.get(albumId)) {
                    mAblumSetCache.put(albumId, item);
                }
            }
        }

        if (null != item) {
            BitmapAndView obj = new BitmapAndView(null, view);
            Message completeMessage = mHandler.obtainMessage(ALBEMSET_DECODE_DONE, obj);
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

    private ImageArea makeSubstituent(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Rect srcBound = new Rect(0, 0, width, height);
        ImageArea substituent = new ImageArea(bitmap, srcBound);
        substituent.isSubstituent = true;
        return substituent;
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
