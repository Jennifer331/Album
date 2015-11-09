package com.example.administrator.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ImageDecodeRunnable implements Runnable {
    private ImageTask mImageTask;
    public static final int REQUIRED_WIDTH = 500;
    public static final int REQUIRED_HEIGHT = 500;

    interface ImageProcessTaskMethod {
        void handleDone(Thread currentThread, Bitmap bitmap);
    }

    public ImageDecodeRunnable(ImageTask imageTask) {
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

        Bitmap bitmap = decodeBitmapFromFile(mImageTask.getImagePath(), REQUIRED_WIDTH,
                REQUIRED_HEIGHT);

        mImageTask.handleDone(Thread.currentThread(), bitmap);
    }

    public Bitmap decodeBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        return BitmapFactory.decodeFile(pathName, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height >> 1;
            final int halfWeight = width / 2;

            while (halfHeight / inSampleSize > reqHeight && halfWeight / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }

        long totalPixels = height * width / inSampleSize;
        final long reqTotalPixelsCap = reqHeight * reqWidth * 2;
        while (totalPixels > reqTotalPixelsCap) {
            totalPixels *= 0.5;
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

}
