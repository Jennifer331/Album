package com.example.administrator.album;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.view.View;

import com.example.administrator.album.ui.AlbumView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lei Xiaoyue on 2015-11-16.
 */
public class BitmapLoader implements BitmapDecodeRunnable.Callback {
    private static final int DEFAULT_MEM_SIZE = 1024 * 1024 * 100;// 100MB
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final TimeUnit KEEP_ALICE_TIME_UNIT;

    private final BlockingQueue<Runnable> mDecodeWorkQueue;
    private final ThreadPoolExecutor mDecodeThreadPool;
    private LruCache<String, Bitmap> mMemoryCache;

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
        mMemoryCache = new LruCache<String, Bitmap>(DEFAULT_MEM_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 0:
                        BitmapAndView info = (BitmapAndView)msg.obj;
                        info.getView().invalidate();
                        break;
                }
            }
        };
    }

    public Bitmap getThumb(AlbumView view,String path, int thumbWidth, int thumbHeight) {
        //find in memory first
        Bitmap bitmap = mMemoryCache.get(path);
        if (null != bitmap)
            return bitmap;

        //then check if the task already existed
        BitmapDecodeRunnable[] runnableArray = new BitmapDecodeRunnable[mInstance.mDecodeWorkQueue.size()];
        mInstance.mDecodeWorkQueue.toArray(runnableArray);
        for(BitmapDecodeRunnable runnable:runnableArray){
            if(runnable.getPath().equals(path))
                return null;
        }

        //to load
        mInstance.mDecodeThreadPool
                .execute(new BitmapDecodeRunnable(view,this, path, thumbWidth, thumbHeight));
        return null;
    }

    @Override
    public void handleDecodeDone(AlbumView view,String path, Bitmap bitmap) {
        if (null != mMemoryCache && null != bitmap) {
            synchronized (mMemoryCache) {
                if (null == mMemoryCache.get(path)) {
                    mMemoryCache.put(path, bitmap);
                }
            }
        }

        if (null != bitmap) {
            BitmapAndView obj = new BitmapAndView(bitmap,view);
            Message completeMessage = mHandler.obtainMessage(0,obj);
            completeMessage.sendToTarget();
        }
    }

    public class BitmapAndView{
        private Bitmap bitmap;
        private AlbumView view;

        public BitmapAndView(Bitmap bitmap,AlbumView view){
            this.bitmap = bitmap;
            this.view = view;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public AlbumView getView() {
            return view;
        }

        public void setView(AlbumView view) {
            this.view = view;
        }
    }
}
