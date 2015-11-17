package com.example.administrator.album;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.administrator.album.ui.AlbumView;
import com.example.administrator.album.util.BitmapWorker;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class BitmapDecodeRunnable implements Runnable {
    public static final int LIMIT_WIDTH = 1000;
    public static final int LIMIT_HEIGHT = 1000;
    private final String path;
    private final int thumbWidth;
    private final int thumbHeight;
    private final Callback callback;
    private final AlbumView view;

    private BitmapLoader loader;

    public BitmapDecodeRunnable(AlbumView view,Callback callback,String path,int thumbWidth,int thumbHeight) {
        this.view = view;
        this.callback = callback;
        this.path = path;
        this.thumbWidth = thumbWidth;
        this.thumbHeight = thumbHeight;
    }

    public String getPath() {
        return path;
    }

    public static interface Callback{
        public void handleDecodeDone(AlbumView view,String path,Bitmap bitmap);
    }
    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        Bitmap bitmap = BitmapWorker.decodeBitmapFromFile(path, LIMIT_WIDTH,
                LIMIT_HEIGHT);

        float suggestedScale = 1f;
        float alignTranslateX = 0f;
        float alignTranslateY = 0f;
        float bitmapRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        float destinationRatio = (float) thumbWidth / (float) thumbHeight;
        if (bitmapRatio < destinationRatio) {
            suggestedScale = (float) thumbWidth / (float) bitmap.getWidth();
            alignTranslateX = 0f;
            alignTranslateY = -(bitmap.getHeight() * suggestedScale - thumbHeight) / 2;
        } else {
            suggestedScale = (float) thumbHeight / (float) bitmap.getHeight();
            alignTranslateX = -(bitmap.getWidth() * suggestedScale - thumbWidth) / 2;
            alignTranslateY = 0f;
        }
        Bitmap resultBitmap = Bitmap.createBitmap(thumbWidth, thumbHeight,
                Bitmap.Config.ARGB_8888);
        Canvas thumbCanvas = new Canvas(resultBitmap);
        thumbCanvas.scale(suggestedScale, suggestedScale);
        thumbCanvas.translate(alignTranslateX, alignTranslateY);
        thumbCanvas.drawBitmap(bitmap, 0, 0, null);

        callback.handleDecodeDone(view,path, resultBitmap);
    }

}
