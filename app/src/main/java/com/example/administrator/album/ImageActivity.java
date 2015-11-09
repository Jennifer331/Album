package com.example.administrator.album;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

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
        mImageManager.setContext(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

//        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager = new StaggeredGridLayoutManager(4,1);
//        mLayoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ImageAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

    }


}
