package com.example.kevinwu.fakesnapchat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;


import com.example.kevinwu.fakesnapchat.R;
import com.example.kevinwu.fakesnapchat.Utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class EmailScreen extends AppCompatActivity {

    private EditText emailAddress;
    private File savedImage;
    private Bitmap photoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailAddress = (EditText) findViewById(R.id.emailText);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String filePath = extras.getString("path");
            loadImageFromStorage(filePath);
            savedImage = Utils.SaveImage(photoBitmap);
        }

    }

    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path, "profile.jpg");
            photoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView imageView = (ImageView) findViewById(R.id.imageView2);
            //imageView.setImageBitmap(photoBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
    }


    public void sendToEmail(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress.getText().toString()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "On The Job");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(savedImage));

        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Choose how you want to email this"));

    }

    public void takeAnother(View view) {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
