package com.example.administrator.album;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.administrator.album.ui.LHView;
import com.example.administrator.album.util.BitmapUtil;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public abstract class LHImageDecodeRunnable implements Runnable {
    protected String path;
    protected int width;
    protected int height;
    protected LHView view;

    public abstract String getPath();
    public abstract int getWidth();
    public abstract int getHeight();
}
