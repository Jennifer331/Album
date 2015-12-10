package com.example.administrator.album.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.administrator.album.BitmapLoader;
import com.example.administrator.album.model.Album;
import com.example.administrator.album.view.AlbumSetArea;
import com.example.administrator.album.view.LHView;

/**
 * Created by Lei Xiaoyue on 2015-11-24.
 */
public class AlbumSetAdapter {
    private static final String TAG = "AlbumSetAdapter";
    private static final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final int COVER_MERGE_PIC_NUM = 3;

    private List<Album> mData;
    private Context mContext;
    private BitmapLoader mLoader;
    private ContentResolver mContentResolver;

    public AlbumSetAdapter(Context context) {
        mData = new ArrayList<Album>();
        mContext = context;
        mLoader = BitmapLoader.getInstance();
        mContentResolver = context.getContentResolver();
        loadData();
    }

    public int getCount() {
        return mData.size();
    }

    public Album getAlbumInfo(int position){
        return mData.get(position);
    }

    public AlbumSetArea getItem(LHView view, int position, int width, int height) {
        return mLoader.getAlbumSet(view, mData.get(position),COVER_MERGE_PIC_NUM,width, height);
    }

    public String getItemTitle(int position){
        return mData.get(position).getAlbumName();
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor albumCursor = getAlbumCursor();
                try {
                    if (albumCursor != null && albumCursor.moveToLast()) {
                        while (albumCursor.moveToPrevious()) {
                            int albumId = albumCursor.getInt(Album.ALBUM_ID);
                            String albumName = albumCursor.getString(Album.ALBUM_NAME);
                            Album album = null;
                            if (albumId != 0 && albumName != null) {
                                album = new Album(albumId, albumName);
                            }

                            addAlbumProfile(album, mContentResolver, uri);

                            if (album != null) {
                                mData.add(album);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    albumCursor.close();
                }
            }
        }).run();
    }

    private Cursor getAlbumCursor() {
        String where = "0==0) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID;
        return mContentResolver.query(uri, Album.PROJECTION, where, null,
                MediaStore.Images.Media.DATE_ADDED);
    }

    /**
     * read 3 or less images from the album to make the cover of the album
     **/
    private void addAlbumProfile(Album album, ContentResolver contentResolver, Uri uri) {
        String idWhere = MediaStore.Images.Media.BUCKET_ID + "=?";
        Cursor profileCursor = contentResolver.query(uri, Album.PROFILE_PROJECTION, idWhere,
                new String[] { album.getAlbumId() + "" },
                MediaStore.Images.Media.DATE_ADDED + " desc limit 3");
        if (profileCursor != null && profileCursor.moveToFirst()) {
            do {
                String imagePath = profileCursor.getString(Album.ALBUM_DATA);
                if (imagePath != null) {
                    album.addProfileImage(imagePath);
                }
            } while (profileCursor.moveToNext());
        }
    }
}
