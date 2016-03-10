package com.example.administrator.album.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.administrator.album.adapter.ImageAdapter;
import com.example.administrator.album.view.AlbumPage;
import com.example.administrator.album.view.AlbumSetPage;
import com.example.administrator.album.view.ImageArea;
import com.example.administrator.album.view.ImagePage;

/**
 * Created by Lei Xiaoyue on 2015-11-13.
 */
public class TestActivity extends Activity implements AlbumSetPage.Callback, AlbumPage.Callback, ImagePage.Callback {
    private static final String TAG = "TestActivity";

    private FrameLayout mLayout;
    private ImagePage mImagePage;
    private AlbumPage mAlbumPage;
    private AlbumSetPage mAlbumSetPage;
    private LinearLayout.LayoutParams mPageParams;
    private static STATUS mStatus;

    public enum STATUS {
        IMAGE, ALBUM, ALBUMSET
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayout.setLayoutParams(layoutparams);

        mPageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mAlbumPage = new AlbumPage(getApplicationContext(), this);
        mAlbumSetPage = new AlbumSetPage(getApplicationContext(), this);
        mAlbumPage.setVisibility(View.INVISIBLE);
        mLayout.addView(mAlbumPage, mPageParams);
        mLayout.addView(mAlbumSetPage, mPageParams);
        mStatus = STATUS.ALBUMSET;
        setContentView(mLayout);
    }

    @Override
    public void headToImage(ImageAdapter adapter,int albumId, ImageArea item) {
        if (null == mImagePage) {
            mImagePage = new ImagePage(getApplicationContext(), adapter,this, albumId, item);
            mLayout.addView(mImagePage, mPageParams);
        }
//        else
        {
            mImagePage.setVisibility(View.VISIBLE);
            mImagePage.show(adapter,albumId, item, true, true);
            mStatus = STATUS.IMAGE;
        }

    }

    @Override
    public void albumSync(int position) {
        mAlbumPage.show(position);
    }

    @Override
    public void backToAlbum(int position) {
        if (null == mAlbumPage) {
            mAlbumPage = new AlbumPage(getApplicationContext(), this);
            mLayout.addView(mAlbumPage, mPageParams);
        }
//        else
        {
            Log.v(TAG, "show position" + position);
            mStatus = STATUS.ALBUM;
//            mAlbumPage.show(position);
            mAlbumPage.fadeIn();
        }
    }

    @Override
    public void animationFinished() {
        mAlbumPage.showAll();
        mImagePage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void headToAlbum(int albumId,Rect from) {
        mAlbumPage.setAlbum(albumId);
        mAlbumPage.fadein();
        if(null != from) {
            Log.v(TAG,"in radiation" + from);
            mAlbumPage.radiation(from);
        }
        mStatus = STATUS.ALBUM;
    }

    @Override
    public ImageArea.ImageAreaAttribute getAnimationDestBound(int position) {
        if (null != mAlbumPage) {
            return mAlbumPage.getLocation(position);
        }
        return null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.v(TAG, mStatus + "");
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            switch (mStatus) {
                case IMAGE: {
                    mImagePage.backToAlbum();
                    mStatus = STATUS.ALBUM;
                    return true;
                }
                case ALBUM: {
                    mAlbumPage.setVisibility(View.GONE);

                    mAlbumSetPage.fadein();
                    mAlbumSetPage.setVisibility(View.VISIBLE);

                    mStatus = STATUS.ALBUMSET;
                    return true;
                }
                case ALBUMSET: {
                    break;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}

