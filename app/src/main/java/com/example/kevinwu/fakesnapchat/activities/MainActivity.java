package com.example.kevinwu.fakesnapchat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agsw.FabricView.FabricView;
import com.example.kevinwu.fakesnapchat.R;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.example.kevinwu.fakesnapchat.Utils.Utils;



public class MainActivity extends AppCompatActivity {

    public static int REQUEST_IMAGE_CAPTURE = 1;
    public static Context mainActivityContext;
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

        MainActivity.mainActivityContext = getApplicationContext();
        //allows you to drag the caption
        myContent.setOnTouchListener(myTouchListener);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(MainActivity.this, "You don't have a camera so you can't use this app",
                    Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            myText.setY(event.getY());
            return true;
        }
    };

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");

            photoView.setImageBitmap(Utils.rotateBitmap(photo, 90));

        }
    }

    public void clearScreen(View view) {
        fabricView.cleanPage();
        menuActions.collapse();
    }

    public void changeMode(View view) {
        if (fabricView.getInteractionMode() == FabricView.LOCKED_MODE) {
            fabricView.setInteractionMode(FabricView.DRAW_MODE);
            drawButton.setTitle("Draw Mode On");
        } else {
            fabricView.setInteractionMode(FabricView.LOCKED_MODE);
            drawButton.setTitle("Draw Something!");
        }

        menuActions.collapse();
    }


    public void sendToEmail(View view) {
        menuActions.collapse();
        Intent intent = new Intent(this, EmailScreen.class);

        menuActions.setVisibility(View.INVISIBLE);

        intent.putExtra("path", Utils.saveToInternalStorage(myContent, getApplicationContext()));
        startActivity(intent);
    }


}
