package com.example.administrator.album.util;

import android.graphics.Rect;

import com.example.administrator.album.view.ImageArea;

/**
 * Created by Lei Xiaoyue on 2015-11-27.
 */
public class RegionModify {
    public static void clipDestBoundBySrc(final ImageArea item,Rect rect){
        if (item.hasSrc()) {
            int destWidth = rect.width();
            int destHeight = rect.height();

            float destRatio = (float) destWidth / (float) destHeight;
            float srcRatio = item.getSrcRatio();

            if (destRatio > srcRatio) {
                int width = (int) (srcRatio * destHeight);
                int offsetX = (destWidth - width) / 2;
                rect.left += offsetX;
                rect.right -= offsetX;
            } else {
                int height = (int) (destWidth / srcRatio);
                int offsetY = (destHeight - height) / 2;
                rect.top += offsetY;
                rect.bottom -= offsetY;
            }
        }
    }
}
