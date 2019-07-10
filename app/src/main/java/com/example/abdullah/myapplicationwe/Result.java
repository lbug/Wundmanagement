package com.example.abdullah.myapplicationwe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String procpath = getIntent().getStringExtra("image");
        double woundSize = getIntent().getDoubleExtra("woundSize",0);

        TextView showSize = findViewById(R.id.textView);
        showSize.setText("Wound Size: "+ woundSize +" cm²" );


        Bitmap bitmap = BitmapFactory.decodeFile(procpath);
        ImageView imageview = findViewById(R.id.imageView); //sets imageview as the bitmap
        imageview.setImageBitmap(bitmap );






    }
}
