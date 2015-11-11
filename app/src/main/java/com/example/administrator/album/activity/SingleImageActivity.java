package com.example.administrator.album.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.administrator.album.R;
import com.example.administrator.album.model.Image;
import com.example.administrator.album.util.BitmapWorker;

/**
 * Created by Lei Xiaoyue on 2015-11-11.
 */
public class SingleImageActivity extends Activity{
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_single_image_view);
        mImageView = (ImageView)findViewById(R.id.imageview);

        String path = getIntent().getStringExtra(Image.PROJECTION_DATA + "");
        Bitmap bitmap = BitmapWorker.decodeBitmapFromFile(path,1000,1000);
        mImageView.setImageBitmap(bitmap);
    }
}
