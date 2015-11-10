package com.example.administrator.album.adapter;

import android.content.ContentResolver;
import android.content.Context;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.album.ImageManager;
import com.example.administrator.album.R;
import com.example.administrator.album.model.Album;
import com.example.administrator.album.util.BitmapWorker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lei Xiaoyue on 2015-11-10.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>
        implements View.OnClickListener {
    private static final String TAG = "AlbumAdapter";
    private static final int COVER_MERGE_PIC_NUM = 3;
    private static final int COVER_WIDTH = 500;
    private static final int COVER_HEIGHT = 500;

    private Context mContext;
    private List<Album> mData;
    private ImageManager mImageManager;
    private ContentResolver mContentResolver;
    private OnRecycledViewItemClickListener mListener;

    public interface OnRecycledViewItemClickListener {
        void onItemClick(View v, int tag);
    }

    public AlbumAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<Album>();
        mImageManager = ImageManager.getInstance();
        loadData();
    }

    public void setOnRecycledViewItemClickListener(OnRecycledViewItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(mData.get(position).getAlbumId());
        holder.textView.setText(mData.get(position).getAlbumName());

        holder.imageView.setImageDrawable(getAlbumCover(position));
    }

    private Drawable getAlbumCover(int position) {
        if (mData.get(position).getProfileImages().size() <= 0) {
            return mContext.getResources().getDrawable(R.drawable.empty_photo, null);
        }
        List<String> profileImagePaths = mData.get(position).getProfileImages();
        Log.v(TAG,profileImagePaths.size() + "");
        Bitmap[] bitmaps = new Bitmap[profileImagePaths.size() > COVER_MERGE_PIC_NUM
                ? COVER_MERGE_PIC_NUM : profileImagePaths.size()];
        for (int i = 0; i < profileImagePaths.size() && i < COVER_MERGE_PIC_NUM; i++) {
            bitmaps[i] = BitmapWorker.decodeBitmapFromFile(profileImagePaths.get(i), COVER_WIDTH,
                    COVER_HEIGHT);
        }

        Bitmap cover = BitmapWorker.pileUpBitmaps(bitmaps,COVER_WIDTH,COVER_HEIGHT);
        return new BitmapDrawable(mContext.getResources(),cover);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_album_view,
                parent, false);
        View cardView = mViewHolder.findViewById(R.id.card_view);
        cardView.setOnClickListener(this);
        AlbumAdapter.ViewHolder vh = new ViewHolder((CardView) cardView);
        return vh;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(v, (int) v.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(CardView view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
            textView = (TextView) view.findViewById(R.id.textview);
        }
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor albumCursor = null;
                mContentResolver = mContext.getContentResolver();

                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String where = "0==0) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID;
                albumCursor = mContentResolver.query(uri, Album.PROJECTION, where, null,
                        MediaStore.Images.Media.DATE_ADDED);
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
            }
        }).run();
    }

    private void addAlbumProfile(Album album, ContentResolver contentResolver, Uri uri) {
        String idWhere = MediaStore.Images.Media.BUCKET_ID + "=?";
        Cursor profileCursor = contentResolver.query(uri, Album.PROFILE_PROJECTION, idWhere,
                new String[] { album.getAlbumId() + "" },
                MediaStore.Images.Media.DATE_ADDED + " desc limit 4");
        if (profileCursor != null && profileCursor.moveToFirst()) {
            while (profileCursor.moveToNext()) {
                String imagePath = profileCursor.getString(Album.ALBUM_DATA);
                if (imagePath != null) {
                    album.addProfileImage(imagePath);
                }
            }
        }
    }
}
