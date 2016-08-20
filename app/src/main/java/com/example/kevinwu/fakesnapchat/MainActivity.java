package com.example.kevinwu.fakesnapchat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agsw.FabricView.FabricView;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    public static int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView photoView;
    private EditText myText;
    private Bitmap photo;
    private RelativeLayout myContent;
    private FabricView fabricView;

    private FloatingActionsMenu menuActions;
    private com.getbase.floatingactionbutton.FloatingActionButton drawButton;
    private com.getbase.floatingactionbutton.FloatingActionButton clearButton;
    private com.getbase.floatingactionbutton.FloatingActionButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        drawButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.drawFab);
        clearButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearFab);
        sendButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendFab);
        photoView = (ImageView) findViewById(R.id.imageView);
        myContent = (RelativeLayout) findViewById(R.id.snapContent);
        myText = (EditText) findViewById(R.id.editText);
        fabricView = (FabricView) findViewById(R.id.faricView);

        fabricView.setInteractionMode(FabricView.LOCKED_MODE);
        fabricView.setBackground(photoView.getDrawable());
        fabricView.setBackgroundColor(Color.TRANSPARENT);

        //allows you to drag the caption
        myContent.setOnTouchListener(myTouchListener);

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            Toast.makeText(MainActivity.this, "You don't have a camera so you can't use this app",
                    Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            myText.setY( event.getY());
            return true;
        }
    };

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");

            photoView.setImageBitmap(rotateBitmap(photo, 90));

        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public void clearScreen (View view) {
        fabricView.cleanPage();
        menuActions.collapse();
    }

    public void changeMode(View view) {
        if(fabricView.getInteractionMode() == FabricView.LOCKED_MODE) {
            fabricView.setInteractionMode(FabricView.DRAW_MODE);
            drawButton.setTitle("Draw Mode On");
        }
        else{
            fabricView.setInteractionMode(FabricView.LOCKED_MODE);
            drawButton.setTitle("Draw Something!");
        }

        menuActions.collapse();
    }


    private String saveToInternalStorage(RelativeLayout content){
        Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
        //Don't want buttons in the photo
        menuActions.setVisibility(View.INVISIBLE);
        Canvas c = new Canvas(bitmap);
        content.layout(0, 0, content.getLayoutParams().width, content.getLayoutParams().height);
        content.draw(c);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

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
            } catch(IOException e){
                e.printStackTrace();
            }

        }
        return directory.getAbsolutePath();
    }

    public void sendToEmail(View view){
        menuActions.collapse();
        Intent intent = new Intent(this, EmailScreen.class);

        intent.putExtra("path", saveToInternalStorage(myContent));
        startActivity(intent);
    }

    public String saveToImage(RelativeLayout content){
        Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        content.layout(0, 0, content.getLayoutParams().width, content.getLayoutParams().height);
        content.draw(c);

        String path = "";
        try{
            File file, f = null;

            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            {
                file =new File(android.os.Environment.getExternalStorageDirectory(),"TTImages_cache");
                if(!file.exists())
                {
                    file.mkdirs();

                }
                f = new File(file.getAbsolutePath()+file.separator+ "filename"+".png");
                path = file.getAbsolutePath();
            }
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
            ostream.close();

        }


        catch (Exception e){
            e.printStackTrace();
        }
        return path;
    }

    public static Bitmap mergeBitmap(Bitmap baseImg, Bitmap addedImg){
        Bitmap finalImg = Bitmap.createBitmap(baseImg.getWidth(), baseImg.getHeight(), baseImg.getConfig());
        Canvas canvas = new Canvas(finalImg);
        canvas.drawBitmap(baseImg, new Matrix(), null);

        if(addedImg != null){
            Matrix matrix = new Matrix();
            matrix.setTranslate(0, baseImg.getHeight() - addedImg.getHeight());

            canvas.drawBitmap(addedImg, matrix, null);
        }


        return finalImg;
    }
}
