package com.example.administrator.album;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.administrator.album.ImageDecodeRunnable.ImageProcessTaskMethod;
/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ImageTask implements ImageProcessTaskMethod{
    private String mImagePath;
    private ImageView mImageView;
    private Thread mCurrentThread;
    private Bitmap mBitmap;

    private ImageManager mImageManager;

    public ImageTask(){
        this(null,null);
    }
    public ImageTask(String imagePath,ImageView imageView){
        mImagePath = imagePath;
        mImageView = imageView;
        mImageManager = ImageManager.getInstance();
    }
    @Override
    public void setWorkingThread(Thread thread){
        mCurrentThread = thread;
        mImageManager.setRunningThread(this);
    }

    @Override
    public void handleDone(Thread currentThread,Bitmap bitmap) {
        mImageManager.handleDone(this,currentThread,bitmap);
    }

    public String getmImagePath(){
        return  mImagePath;
    }

    public ImageView getmImageView(){
        return mImageView;
    }


    public Thread getmCurrentThread(){
        return mCurrentThread;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }
}
