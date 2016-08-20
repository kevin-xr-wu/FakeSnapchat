package com.example.kevinwu.fakesnapchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class EmailScreen extends AppCompatActivity {

    private EditText emailAddress;
    private File fileToSend;
    private Bitmap photoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailAddress = (EditText) findViewById(R.id.emailText);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            String filePath = extras.getString("path");
            loadImageFromStorage(filePath);
        }

    }

    @Override
    public void onBackPressed() {
    }

    private void loadImageFromStorage(String path)
    {

        try {
            File f =new File(path, "profile.jpg");
            photoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            SaveImage(photoBitmap);
            //ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            //imageView.setImageBitmap(photoBitmap);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            fileToSend = file;
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToEmail(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress.getText().toString()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "On The Job");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileToSend));

        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Choose how you want to email this"));

    }

    public void takeAnother(View view){
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
