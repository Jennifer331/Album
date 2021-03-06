package com.example.administrator.album.task;

import android.util.Log;

import com.example.administrator.album.view.ImageArea;
import com.example.administrator.album.view.LHView;
import com.example.administrator.album.util.BitmapUtil;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class ThumbDecodeRunnable extends LHImageDecodeRunnable {
    private static final String TAG = "ThumbDecodeRunnable";
    private final Callback callback;

    public static interface Callback {
        public void handleThumbDecodeDone(LHView view, String path, ImageArea item);
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
        Log.v(TAG,path + "");
        callback.handleThumbDecodeDone(view, path,
                BitmapUtil.decodeWithFillRatioFromFileReturnImageArea(path, width, height));
    }
}
