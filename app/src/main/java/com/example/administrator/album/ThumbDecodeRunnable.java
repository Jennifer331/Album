package com.example.administrator.album;

import com.example.administrator.album.view.ImageArea;
import com.example.administrator.album.view.LHView;
import com.example.administrator.album.util.BitmapUtil;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ThumbDecodeRunnable extends LHImageDecodeRunnable {
    private final Callback callback;

    public static interface Callback {
        public void handleThumbDecodeDone(LHView view, String path, ImageArea item);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getPath() {
        return path;
    }

    public ThumbDecodeRunnable(LHView view, Callback callback, String path, int thumbWidth,
            int thumbHeight) {
        this.view = view;
        this.callback = callback;
        this.path = path;
        this.width = thumbWidth;
        this.height = thumbHeight;
    }

    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        callback.handleThumbDecodeDone(view, path,
                BitmapUtil.decodeWithFillRatioFromFileReturnImageArea(path, width, height));
    }
}
