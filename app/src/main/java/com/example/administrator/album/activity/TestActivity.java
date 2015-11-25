package com.example.administrator.album.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.administrator.album.view.AlbumPage;
import com.example.administrator.album.view.ImageArea;
import com.example.administrator.album.view.ImagePage;
import com.example.administrator.album.view.LHView;

/**
 * Created by Lei Xiaoyue on 2015-11-13.
 */
public class TestActivity extends Activity implements AlbumPage.Callback, ImagePage.Callback {
    private FrameLayout mLayout;
    private ImagePage mImagePage;
    private AlbumPage mAlbumPage;
    private LinearLayout.LayoutParams mPageParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayout.setLayoutParams(layoutparams);

        mPageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mAlbumPage = new AlbumPage(this, this);

        mLayout.addView(mAlbumPage, mPageParams);
        setContentView(mLayout);
    }

    @Override
    public void headToImage(ImageArea item) {
//        mAlbumPage.saveState();
        if (null == mImagePage) {
            mImagePage = new ImagePage(TestActivity.this, item, this);
            mLayout.addView(mImagePage, mPageParams);
        }else{
//            mImagePage.show(item);
        }
    }

    @Override
    public void headToAlbum(ImageArea item) {
        if (null == mAlbumPage) {
            mAlbumPage = new AlbumPage(this, this);
            mLayout.addView(mAlbumPage, mPageParams);
        }else{
//            mAlbumPage.show();
        }
    }
}
