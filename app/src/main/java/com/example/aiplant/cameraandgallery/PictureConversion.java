package com.example.aiplant.cameraandgallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Class that contains static method to transform Bitmaps to Byte arrays and vice versa.
 */
public class PictureConversion {
    private static final String TAG = "PictureConversion";


    public PictureConversion() {
    }

    /**
     * Method that transform a bitmap into a byte array.
     *
     * @param bmp bitmap
     * @return byte array transformed from the input bitmap.
     */


    public byte[] bitmapToByteArray(Bitmap bmp) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        } catch (Exception e) {
            Log.d(TAG, "bitmapToByteArray: ");
        }
        byte[] byteArray = stream.toByteArray();
        long lengthbmp = byteArray.length;
        Log.d("memory", " " + lengthbmp);
        bmp.recycle();

        return byteArray;
    }

    /**
     * Method that tansforrm a byte array into a bitmap.
     *
     * @param byteArray byte array.
     * @return bitmap transformed from the input byte array.
     */
    public Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
