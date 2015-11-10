package com.example.administrator.album;

import android.graphics.Bitmap;

import com.example.administrator.album.adapter.AlbumAdapter.ViewHolder;

import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class CompositionTask extends RecyclerViewTask implements ImageCompositionRunnable.ImageCompositionTaskMethod {
    private List<String> mImagePaths;
    private Bitmap mBitmap;

    public CompositionTask() {
        this(0,null,null);
    }

    public CompositionTask(int position,List<String> imagePaths,ViewHolder viewHolder) {
        mPosition = position;
        mViewHolder = viewHolder;
        mImagePaths = imagePaths;
        mImageManager = ImageManager.getInstance();
    }

    public List<String> getImagePaths() {
        return mImagePaths;
    }

    public void setImagePaths(List<String> mImagePaths) {
        this.mImagePaths = mImagePaths;
    }

    public ViewHolder getViewHolder() {
        return (ViewHolder)mViewHolder;
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

    @Override
    public void handleCompositionDone(Bitmap bitmap) {
        mImageManager.handleCompositionDone(this,bitmap);
    }
}
