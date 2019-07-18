package com.example.abdullah.myapplicationwe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
        showSize.setText("Wundgröße: " + woundSize + " cm² \nHöhe: " + rectSizeHeight + " cm" + "\nBreite: " + rectSizeWidth + " cm");


        // don't ask don't tell
        Localization.procPath = procpath;

        Bitmap bitmap = BitmapFactory.decodeFile(procpath);
        ImageView imageview = findViewById(R.id.imageView); //sets imageview as the bitmap
        imageview.setImageBitmap(bitmap);


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
