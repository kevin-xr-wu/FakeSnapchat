package com.example.kevinwu.fakesnapchat.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.widget.RelativeLayout;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Kevin on 8/21/2016.
 */
public class Utils {

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static String saveToInternalStorage(RelativeLayout content, Context context) {
        Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
        //Don't want buttons in the photo

        Canvas c = new Canvas(bitmap);
        content.layout(0, 0, content.getLayoutParams().width, content.getLayoutParams().height);
        content.draw(c);

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return directory.getAbsolutePath();
    }

    public static File SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public String saveToImage(RelativeLayout content) {
        Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        content.layout(0, 0, content.getLayoutParams().width, content.getLayoutParams().height);
        content.draw(c);

        String path = "";
        try {
            File file, f = null;

            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                file = new File(android.os.Environment.getExternalStorageDirectory(), "TTImages_cache");
                if (!file.exists()) {
                    file.mkdirs();

                }
                f = new File(file.getAbsolutePath() + file.separator + "filename" + ".png");
                path = file.getAbsolutePath();
            }
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
            ostream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static Bitmap mergeBitmap(Bitmap baseImg, Bitmap addedImg) {
        Bitmap finalImg = Bitmap.createBitmap(baseImg.getWidth(), baseImg.getHeight(), baseImg.getConfig());
        Canvas canvas = new Canvas(finalImg);
        canvas.drawBitmap(baseImg, new Matrix(), null);

        if (addedImg != null) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(0, baseImg.getHeight() - addedImg.getHeight());

            canvas.drawBitmap(addedImg, matrix, null);
        }


        return finalImg;
    }
}
