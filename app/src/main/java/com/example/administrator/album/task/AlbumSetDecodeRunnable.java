package com.example.administrator.album.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.administrator.album.R;
import com.example.administrator.album.model.Album;
import com.example.administrator.album.util.BitmapUtil;
import com.example.administrator.album.view.AlbumSetArea;
import com.example.administrator.album.view.LHView;

import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-05.
 */
public class AlbumSetDecodeRunnable extends LHImageDecodeRunnable {
    private Album mAlbum;
    private int mPileNum;
    private final Callback mCallback;

    public static interface Callback {
        public void handleAlbumSetDecodeDone(LHView view, int albumId, AlbumSetArea item);
    }

    public AlbumSetDecodeRunnable(LHView view, Callback callback, Album album, int pileNum,
            int width, int height) {
        this.view = view;
        this.mCallback = callback;
        mAlbum = album;
        mPileNum = pileNum;
        this.width = width;
        this.height = width;
    }

    public int getAlbumId() {
        return mAlbum.getAlbumId();
    }

    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        // if (mAlbum.getProfileImages().size() <= 0) {
        // BitmapFactory.decodeResource(view.getContext().getResources(),
        // R.drawable.empty_photo);
        // }
        List<String> profileImagePaths = mAlbum.getProfileImages();
        Bitmap[] bitmaps = new Bitmap[profileImagePaths.size() > mPileNum ? mPileNum
                : profileImagePaths.size()];
        for (int i = 0; i < profileImagePaths.size() && i < mPileNum; i++) {
            bitmaps[i] = BitmapUtil.decodeBitmapFromFile(profileImagePaths.get(i), width * 2, height * 2);
        }
        mCallback.handleAlbumSetDecodeDone(view, mAlbum.getAlbumId(),
                new AlbumSetArea(BitmapUtil.pileUpBitmaps(bitmaps, width, height)));
    }
}
