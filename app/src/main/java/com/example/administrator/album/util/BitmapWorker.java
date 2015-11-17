package com.example.administrator.album.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by Lei Xiaoyue on 2015-11-10.
 */
public class BitmapWorker {
    private static final String TAG = "BitmapWorker";

    public static Bitmap decodeBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize2(options, reqWidth, reqHeight);
        Log.v(TAG, "Decode Sample" + options.inSampleSize + "");
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height >> 1;
            final int halfWeight = width / 2;

            while (halfHeight / inSampleSize > reqHeight && halfWeight / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }

        long totalPixels = height * width / inSampleSize;
        final long reqTotalPixelsCap = reqHeight * reqWidth * 2;
        while (totalPixels > reqTotalPixelsCap) {
            totalPixels *= 0.5;
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    public static int calculateInSampleSize2(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height != 0 && reqHeight != 0) {
            if (width / height > reqWidth / reqHeight)
                inSampleSize = width / reqWidth;
            else
                inSampleSize = height / reqHeight;
        }
        return lastPowerOf2(inSampleSize * inSampleSize);
    }

    public static int lastPowerOf2(final int a) {
        int b = 1;
        while (b < a) {
            b = b << 1;
        }
        return b == 1 ? 1 : b >> 1;
    }

    public static Bitmap pileUpBitmaps(Bitmap bitmaps[], int destWidth, int destHeight) {
        Bitmap newBitmap = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        int size = bitmaps.length;
        if (bitmaps.length > 0) {
            Canvas canvas = new Canvas(newBitmap);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            Paint paint = new Paint();
            canvas.translate(30 * (size - 1), 0);
            for (int i = 0; i < bitmaps.length; i++) {
                // canvas.drawBitmap(bitmaps[i], 0, 0, paint);
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmaps[i], destWidth - 30 * (size - 1),
                        destHeight - 30 * (size - 1), false), 0, 0, paint);
                canvas.translate(-30, 30);
            }
            canvas.restore();
        }

        return newBitmap;
    }
}
