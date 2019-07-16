package com.example.abdullah.myapplicationwe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class WoundHistory extends AppCompatActivity {

    public static String procpath;
    public static ArrayList<String> filepaths;
    public static int pictureIndex = 0;

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
        setContentView(R.layout.activity_woundhistory);

        /*
        ImageView imageView = findViewById(R.id.imageView2);
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(procpath);
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        */

        filepaths = getIntent().getStringArrayListExtra("PictureFilepaths");
        this.setImage();
    }

    public void setImage() {
        ImageView imageView = findViewById(R.id.imageView2);
        if(pictureIndex < filepaths.size()) {
            try{
                Bitmap bitmap = BitmapFactory.decodeFile(filepaths.get(pictureIndex));
                imageView.setImageBitmap(bitmap);
                pictureIndex++;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}