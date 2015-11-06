package com.example.administrator.album;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ImageManager {
    // Sets the amount of time an idle thread will wati for a task before
    // terminating
    private static final int KEEP_ALIVE_TIME = 1;

    //Handler message state
    private static final int START_DECODE = 0;
    private static final int DECODE_DONE = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALICE_TIME_UNIT;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final BlockingQueue<Runnable> mDecodeWorkQueue;

    private final ThreadPoolExecutor mDecodeThreadPool;

    // An Object that manages the Messages in the Thread
    private Handler mHandler;

    // A single instance of ImageManager,used to implement the singleton pattern
    private static ImageManager mInstance = null;

    // A static block that sets class fields
    static {
        // The time unit for "keep alive" is in seconds
        KEEP_ALICE_TIME_UNIT = TimeUnit.SECONDS;

        // Creates a single static instance of ImageManager
        mInstance = new ImageManager();
    }

    public static ImageManager getInstance() {
        return mInstance;
    }

    private ImageManager() {
        mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();
        mDecodeThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALICE_TIME_UNIT, mDecodeWorkQueue);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case START_DECODE:{
                        ImageTask task = (ImageTask)msg.obj;
                        ImageView imageView = task.getmImageView();
                        Drawable drawable= imageView.getDrawable();
                        if(drawable instanceof AsyncDrawable){
                            ((AsyncDrawable) drawable).setBitmapWorkerTask(task.getmCurrentThread());
                        }
                        break;
                    }
                    case DECODE_DONE: {
                        ImageTask task = (ImageTask) msg.obj;
                        ImageView imageView = task.getmImageView();
                        imageView.setImageBitmap(task.getmBitmap());
                        break;
                    }
                }
            }
        };
    }

    public static void cancelAll() {
        ImageDecodeRunnable[] runnableArray = new ImageDecodeRunnable[mInstance.mDecodeWorkQueue
                .size()];
        mInstance.mDecodeWorkQueue.toArray(runnableArray);
        synchronized (mInstance) {
            for (ImageDecodeRunnable runnable : runnableArray) {
                Thread thread = runnable.getmImageTask().getmCurrentThread();
                if (null != thread) {
                    thread.interrupt();
                }
            }
        }
    }

    public void setRunningThread(ImageTask task){
        Message updateThreadMessage = mHandler.obtainMessage(START_DECODE,task);
        updateThreadMessage.sendToTarget();
    }

    public void loadImage(String imagePath, ImageView imageview) {
        final ImageTask task = new ImageTask(imagePath, imageview);
        // add the task to the thread pool for execution
        mInstance.mDecodeThreadPool.execute(new ImageDecodeRunnable(task));
        Resources resources = imageview.getResources();
        AsyncDrawable asyncDrawable = new AsyncDrawable(resources,
                BitmapFactory.decodeResource(resources,R.drawable.empty_photo));
        // set the placeholder of the loading image
        imageview.setImageDrawable(asyncDrawable);
    }

    public void handleDone(ImageTask task,Thread currentThread,Bitmap bitmap) {
        ImageView imageView =getAttachedImageView(currentThread, task);
        if(bitmap != null && imageView != null) {
            task.setmBitmap(bitmap);
            Message comleteMessage = mHandler.obtainMessage(DECODE_DONE, task);
            comleteMessage.sendToTarget();
        }
    }

    private ImageView getAttachedImageView(Thread currentThread,ImageTask task){
        ImageView imageView = task.getmImageView();
        Drawable drawable = imageView.getDrawable();
        if(drawable instanceof  AsyncDrawable){
            Thread thread = ((AsyncDrawable) drawable).getBitmapWorkerTask();
            if(currentThread == thread){
                return imageView;
            }
        }
        return null;
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work
     * is in progress. Contains a reference to the actual worker task, so that
     * it can be stopped if a new binding is required, and makes sure that only
     * the last started worker process can bind its result, independently of the
     * finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private WeakReference<Thread> mImageTaskReference;

        public AsyncDrawable(Resources res,Bitmap bitmap){
            this(res,bitmap,null);
        }
        public AsyncDrawable(Resources res, Bitmap bitmap, Thread thread) {
            super(res, bitmap);
            mImageTaskReference = new WeakReference<Thread>(thread);
        }

        public void setBitmapWorkerTask(Thread thread){
            mImageTaskReference = new WeakReference<Thread>(thread);
        }
        public Thread getBitmapWorkerTask() {
            return mImageTaskReference.get();
        }
    }

}
