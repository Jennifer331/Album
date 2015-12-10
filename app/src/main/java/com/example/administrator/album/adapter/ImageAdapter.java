package com.example.administrator.album.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.administrator.album.BitmapLoader;
import com.example.administrator.album.model.Image;
import com.example.administrator.album.view.ImageArea;
import com.example.administrator.album.view.LHView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-24.
 */
public class ImageAdapter {
    private List<Image> mData;
    private Context mContext;
    private int mAlbumId;
    private BitmapLoader loader;

    public ImageAdapter(Context context, int albumId) {
        mData = new ArrayList<Image>();
        mContext = context;
        mAlbumId = albumId;
        loader = BitmapLoader.getInstance();
        loadData(mAlbumId);
    }

    public int getCount() {
        return mData.size();
    }

    public ImageArea getImageArea(LHView view, int position, int width, int height) {
        return loader.getThumb(view, mData.get(position).getImagePath(), width, height);
    }

    public ImageArea getFullSizeImageArea(LHView view, int position, int width, int height) {
        return loader.getFullSizeImageArea(view, mData.get(position).getImagePath(), width, height);
    }

    private void loadData(final int albumId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cur = null;
                ContentResolver contentResolver = mContext.getContentResolver();
                String where = MediaStore.Images.Media.BUCKET_ID + "=?";
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                try {
                    cur = contentResolver.query(uri, Image.PROJECTION, where,
                            new String[] { albumId + "" }, MediaStore.Images.Media.DATE_ADDED);
                    if (cur != null && cur.moveToLast()) {
                        do {
                            String data = cur.getString(Image.PROJECTION_DATA);
                            int albumId = cur.getInt(Image.PROJECTION_BUCKET_ID);
                            if (data != null) {
                                mData.add(new Image(data, albumId));
                            }
                        } while (cur.moveToPrevious());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(null != cur){
                        cur.close();
                    }
                }
            }
        }).run();
    }
}
