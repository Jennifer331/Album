package com.example.administrator.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ImageDecodeRunnable implements Runnable{
    private ImageTask mImageTask;

    interface ImageProcessTaskMethod{
        void handleDone(Thread currentThread,Bitmap bitmap);
    }

    public ImageDecodeRunnable(ImageTask imageTask){
        mImageTask = imageTask;
    }

    public ImageTask getmImageTask(){
        return mImageTask;
    }

    @Override
    public void run() {
        //Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(mImageTask.getmImagePath(),options);

        mImageTask.handleDone(Thread.currentThread(),bitmap);
    }

}
