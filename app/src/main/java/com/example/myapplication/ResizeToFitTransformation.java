package com.example.myapplication;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class ResizeToFitTransformation extends BitmapTransformation {
    private final int targetWidth, targetHeight;

    public ResizeToFitTransformation(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        try {
            float aspectRatio = (float) source.getWidth() / source.getHeight();
            int newWidth, newHeight;

            // Calcul plus robuste avec vérification de division par zéro
            if (source.getHeight() == 0 || source.getWidth() == 0) {
                return source;
            }

            if (source.getWidth() > targetWidth || source.getHeight() > targetHeight) {
                float widthRatio = (float) targetWidth / source.getWidth();
                float heightRatio = (float) targetHeight / source.getHeight();
                float scale = Math.min(widthRatio, heightRatio);
                newWidth = (int) (source.getWidth() * scale);
                newHeight = (int) (source.getHeight() * scale);
            } else {
                // Si l'image est plus petite, ne pas l'agrandir
                return source;
            }

            // Utilisation du pool de bitmaps pour une meilleure performance
            Bitmap result = pool.get(newWidth, newHeight, source.getConfig() != null
                    ? source.getConfig() : Bitmap.Config.ARGB_8888);

            Bitmap resized = Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
            if (resized != source && !source.isRecycled()) {
                pool.put(source); // Recyclage propre via le pool
            }
            return resized;

        } catch (Exception e) {
            Log.e("TRANSFORM", "Error in transformation", e);
            return source;
        }
    }
    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(("ResizeToFit-" + targetWidth + "x" + targetHeight).getBytes());
    }
}