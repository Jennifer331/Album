package com.example.administrator.album.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.album.ImageManager;
import com.example.administrator.album.R;
import com.example.administrator.album.adapter.AlbumAdapter;
import com.example.administrator.album.model.Album;

public class AlbumActivity extends Activity {
    private RecyclerView mRecyclerView;
    private AlbumAdapter mAdapter;
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

        mAdapter = new AlbumAdapter(this);
        mAdapter.setOnRecycledViewItemClickListener(new AlbumAdapter.OnRecycledViewItemClickListener(){
            @Override
            public void onItemClick(View v,int tag) {
                Toast.makeText(AlbumActivity.this,String.valueOf(tag),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AlbumActivity.this,ImageActivity.class);
                intent.putExtra(Album.ALBUM_ID + "",tag);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }

}
