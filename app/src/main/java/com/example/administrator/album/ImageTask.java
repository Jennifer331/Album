package com.example.administrator.album;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.administrator.album.ImageAdapter.ViewHolder;
import com.example.administrator.album.ImageDecodeRunnable.ImageProcessTaskMethod;
/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ImageTask implements ImageProcessTaskMethod{
    private String mImagePath;
//    private ImageView mImageView;
    private int mPosition;
    private ViewHolder mViewHolder;
//    private Thread mCurrentThread;
    private Bitmap mBitmap;

    private ImageManager mImageManager;

    public ImageTask(){
        this(0,null,null);
    }
    public ImageTask(int position,String imagePath,ViewHolder viewHolder){
        mPosition = position;
        mImagePath = imagePath;
        mViewHolder = viewHolder;
        mImageManager = ImageManager.getInstance();
    }
//    @Override
//    public void setWorkingThread(Thread thread){
//        mCurrentThread = thread;
//        mImageManager.setRunningThread(this);
//    }

    @Override
    public void handleDone(Thread currentThread,Bitmap bitmap) {
        mImageManager.handleDone(this, currentThread, bitmap);
    }

    public String getmImagePath(){
        return  mImagePath;
    }

    public ViewHolder getmViewHolder() {
        return mViewHolder;
    }

//    public Thread getmCurrentThread(){
//        return mCurrentThread;
//    }

    public boolean hasmBitmap(){
        return mBitmap == null? false : true;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getmPosition() {
        return mPosition;
    }

//    public void setmCurrentThread(Thread mCurrentThread) {
//        this.mCurrentThread = mCurrentThread;
//    }
}
