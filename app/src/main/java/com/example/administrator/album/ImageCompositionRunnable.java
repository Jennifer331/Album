package com.example.administrator.album;

import android.graphics.Bitmap;

import com.example.administrator.album.util.BitmapWorker;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ImageCompositionRunnable implements Runnable {
    private static final String TAG = "DecodeSample";
    private ImageTask mImageTask;
    public static final int REQUIRED_WIDTH = 500;
    public static final int REQUIRED_HEIGHT = 500;

    interface ImageCompositionTaskMethod {
        void compositionHandleDone(Thread currentThread, Bitmap bitmap);
    }

    public ImageCompositionRunnable(ImageTask imageTask) {
        mImageTask = imageTask;
    }

    public boolean hasImageTask() {
        return mImageTask == null ? false : true;
    }

    public ImageTask getImageTask() {
        return mImageTask;
    }

    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        Bitmap bitmap = BitmapWorker.decodeBitmapFromFile(mImageTask.getImagePath(), REQUIRED_WIDTH,
                REQUIRED_HEIGHT);

        mImageTask.decodeHandleDone(Thread.currentThread(), bitmap);
    }

}
