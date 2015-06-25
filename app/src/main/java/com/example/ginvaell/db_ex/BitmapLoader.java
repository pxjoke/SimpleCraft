package com.example.ginvaell.db_ex;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * Created by pxjoke on 06.06.15.
 *
 */
public class BitmapLoader {
    private final LruCache<String, Bitmap> mMemoryCache;
    private final Context mContext;

    public BitmapLoader(Context context){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mContext = context;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void load(String resName, ImageView mImageView, int redWidth, int regHeight) {
        int resId = mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());
        final String imageKey = String.valueOf(resId);

        Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
            System.out.println("Image used.");

        } else {
            //mImageView.setImageResource(R.drawable.none);
            bitmap = decodeSampledBitmapFromResource(mContext.getResources(), resId, redWidth, regHeight);
            addBitmapToMemoryCache(String.valueOf(imageKey), bitmap);
            mImageView.setImageBitmap(bitmap);
            System.out.println("Image decoded.");

        }

    }

    public void loadWithoutCaching(String resName, ImageView mImageView, int redWidth, int regHeight){
        int resId = mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());
        Bitmap bitmap = decodeSampledBitmapFromResource(mContext.getResources(), resId, redWidth, regHeight);
        mImageView.setImageBitmap(bitmap);
        bitmap = null;
    }
    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
