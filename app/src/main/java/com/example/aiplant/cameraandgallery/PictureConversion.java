package com.example.aiplant.cameraAndGallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
     * @param bitmap bitmap
     * @return byte array transformed from the input bitmap.
     */
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
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
