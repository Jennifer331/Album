package com.example.administrator.album.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.administrator.album.ImageManager;
import com.example.administrator.album.R;
import com.example.administrator.album.adapter.ImageAdapter;
import com.example.administrator.album.model.Album;

public class ImageActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageManager mImageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageManager = ImageManager.getInstance();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager = new StaggeredGridLayoutManager(4, 1);
        // mLayoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        int albumId = getIntent().getIntExtra(Album.ALBUM_ID + "",0);
        mAdapter = new ImageAdapter(this,albumId);
        mRecyclerView.setAdapter(mAdapter);

    }

}