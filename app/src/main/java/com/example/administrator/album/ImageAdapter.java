package com.example.administrator.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-02.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private static final String TAG = "ImageAdapter";
    Context mContext;
    List<String> mData;
    String[] projection = new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, // add
                                                                                                    // DATA
                                                                                                    // column
            MediaStore.Images.Media.TITLE, };
    private static final int PROJECTION_ID = 0;
    private static final int PROJECTION_DATA = 1;
    private static final int PROJECTION_TITLE = 2;

    private ImageManager mImageManager;

    public ImageAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<String>();
        mImageManager = ImageManager.getInstance();
        loadData();
        // setHasStableIds(true);
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cast_view,
                parent, false);
        View v = cardView.findViewById(R.id.card_view);
        ImageAdapter.ViewHolder vh = new ViewHolder((CardView) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.v(TAG,"in onBindViewHolder " + position);
        mImageManager.loadImage(position,mData.get(position),holder);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(CardView view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
        }

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Log.v(TAG, "in onViewRecycled");
//        mImageManager.cancelTask(holder.imageView);
    }

    private void loadData() {
        Cursor cur = null;
        ContentResolver contentResolver = mContext.getContentResolver();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        cur = contentResolver.query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_ADDED);
//        if (cur.moveToFirst()) {
//            while (cur.moveToNext()) {
//                String data = cur.getString(PROJECTION_DATA);
//                if (data != null) {
//                    mData.add(data);
//                }
//            }
//        }
        if (cur != null && cur.moveToLast()) {
            while (cur.moveToPrevious()) {
                String data = cur.getString(PROJECTION_DATA);
                if (data != null) {
                    mData.add(data);
                }
            }
        }

        uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        cur = contentResolver.query(uri, projection, null, null, MediaStore.Images.Media.TITLE);
        if (cur != null && cur.moveToFirst()) {
            while (cur.moveToNext()) {
                String data = cur.getString(PROJECTION_DATA);
                if (data != null) {
                    mData.add(data);
                }
            }
        }
    }

}