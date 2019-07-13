package com.example.abdullah.myapplicationwe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ai.snips.hermes.IntentMessage;
import ai.snips.hermes.Slot;
import ai.snips.platform.SnipsPlatformClient;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class Result extends AppCompatActivity {

    private static final String TAG = Result.class.getSimpleName();
    private static Result instance;


    public static Result getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_result);

        String procpath = getIntent().getStringExtra("image");
        double woundSize = getIntent().getDoubleExtra("woundSize", 0);
        double rectSizeHeight = getIntent().getDoubleExtra("rectSizeHeight", 0);
        double rectSizeWidth = getIntent().getDoubleExtra("rectSizeWidth", 0);

        TextView showSize = findViewById(R.id.textView);
        showSize.setText("Wundgröße: " + woundSize + " cm² \n Höhe: " + rectSizeHeight + "\n Breite: " + rectSizeWidth);


        Bitmap bitmap = BitmapFactory.decodeFile(procpath);
        ImageView imageview = findViewById(R.id.imageView); //sets imageview as the bitmap
        imageview.setImageBitmap(bitmap);


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
