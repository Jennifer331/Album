package com.example.administrator.album;

import android.graphics.Bitmap;

import com.example.administrator.album.adapter.ImageAdapter.ViewHolder;
import com.example.administrator.album.ImageDecodeRunnable.ImageDecodeTaskMethod;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ImageTask implements ImageDecodeTaskMethod {
    private String mImagePath;
    private int mPosition;
    private ViewHolder mViewHolder;
    private Bitmap mBitmap;

    private ImageManager mImageManager;

    public ImageTask() {
        this(0, null, null);
    }

    public ImageTask(int position, String imagePath, ViewHolder viewHolder) {
        mPosition = position;
        mImagePath = imagePath;
        mViewHolder = viewHolder;
        mImageManager = ImageManager.getInstance();
    }

    @Override
    public void decodeHandleDone(Thread currentThread, Bitmap bitmap) {
        mImageManager.handleDone(this, currentThread, bitmap);
    }

    public String getImagePath() {
        return mImagePath;
    }

    public ViewHolder getViewHolder() {
        return mViewHolder;
    }

    public boolean hasBitmap() {
        return mBitmap == null ? false : true;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getPosition() {
        return mPosition;
    }
}
