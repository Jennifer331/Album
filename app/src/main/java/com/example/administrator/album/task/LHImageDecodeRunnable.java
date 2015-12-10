package com.example.administrator.album.task;

import com.example.administrator.album.view.LHView;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public abstract class LHImageDecodeRunnable implements Runnable {
    protected String path;
    protected int width;
    protected int height;
    protected LHView view;

    public String getPath(){
        return path;
    };
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
}
