package com.example.abdullah.myapplicationwe;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdullah.myapplicationwe.Datenbank.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class WoundHistory extends AppCompatActivity {

    public static String procpath;
    public static ArrayList<String> filepaths;
    public static ArrayList<String> timestamps;
    public static ArrayList<String> heights;
    public static ArrayList<String> widths;
    public static int pictureIndex = 0;
    private static WoundHistory instance;
    Dashboard dashboard;
    public static int sector;
    private ImageView imageView3;

    public static WoundHistory getInstance() {
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
        setContentView(R.layout.activity_woundhistory);

        instance = this;

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
        timestamps = getIntent().getStringArrayListExtra("PictureTimestamps");
        heights = getIntent().getStringArrayListExtra("PictureHeights");
        widths = getIntent().getStringArrayListExtra("PictureWidths");
        this.setFirstImage(0);
    }

    public void setFirstImage(int pictureSet) {


        Long unixTime = Long.parseLong(timestamps.get(pictureIndex));
        Date zeit = new Date(unixTime * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String formattedDate = sdf.format(zeit);


        ImageView imageView = findViewById(R.id.imageView2);
        TextView textView = findViewById(R.id.pictureInfos);

        Log.d("Datenbank", "pictureSetter: " + pictureSet);

        pictureIndex += pictureSet;

        if (pictureIndex < filepaths.size() && pictureIndex >= 0) {
            try {

                returnSector();

                Cursor cursor = dashboard.getDataSourceInstance().getInstanceOfDatabase().rawQuery("SELECT woundSector FROM picture WHERE "+ DBHelper.COLUMN_IMAGEPATH + "=" + "'" + filepaths.get(pictureIndex) + "'",null);
                cursor.moveToFirst();
                int idSector = cursor.getColumnIndex(DBHelper.COLUMN_SECTOR);
                int sector = cursor.getInt(idSector);

                // show th right localization picturee
                Log.d("SQLMAGIC ", String.valueOf(sector));

                Bitmap bitmap = BitmapFactory.decodeFile(filepaths.get(pictureIndex));
                imageView.setImageBitmap(bitmap);
                textView.setText("Höhe: " + heights.get(pictureIndex) + "\nBreite: " + widths.get(pictureIndex) + "\nZeit: " + formattedDate);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Datenbank", "Abgestürzt");
            }
        }
    }

    public void setImage(int pictureSetter) {
        Long unixTime = Long.parseLong(timestamps.get(pictureIndex));
        Date zeit = new Date(unixTime*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String formattedDate = sdf.format(zeit);

        ImageView imageView = findViewById(R.id.imageView2);
        TextView textView = findViewById(R.id.pictureInfos);
        imageView3 = findViewById(R.id.imageView3);

        // show the right localization picture



        Log.d("Datenbank", "pictureSetter: " + pictureSetter);

        pictureIndex += pictureSetter;

        if(pictureIndex < filepaths.size() && pictureIndex >= 0) {
            try{
                returnSector();

                Cursor cursor = dashboard.getDataSourceInstance().getInstanceOfDatabase().rawQuery("SELECT woundSector FROM picture WHERE "+ DBHelper.COLUMN_IMAGEPATH + "=" + "'" + filepaths.get(pictureIndex) + "'",null);
                cursor.moveToFirst();
                int idSector = cursor.getColumnIndex(DBHelper.COLUMN_SECTOR);
                int sector = cursor.getInt(idSector);

                // show th right localization picturee
                Log.d("Datenbank ", "Wert von cursor: " + cursor.getInt(idSector));
                Log.d("Datenbank ", "Wert von idSector: " + cursor.getColumnIndex(DBHelper.COLUMN_SECTOR));
                Log.d("Datenbank", "SELECT woundSector FROM picture WHERE "+ DBHelper.COLUMN_IMAGEPATH + "=" + "'" + filepaths.get(pictureIndex) + "'",null);

                Bitmap bitmap = BitmapFactory.decodeFile(filepaths.get(pictureIndex));
                imageView.setImageBitmap(bitmap);
                textView.setText("Höhe: " + heights.get(pictureIndex) + "\nBreite: " + widths.get(pictureIndex) + "\nZeit: " + formattedDate);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(pictureIndex < 0){
            pictureIndex = 0;
            Toast.makeText(this, "Das ist bereits das aktuellste Bild", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Kein weiteres Bild vorhanden", Toast.LENGTH_LONG).show();
            pictureIndex -= 1;
        }
    }

    public void returnSector(){
        imageView3 = findViewById(R.id.imageView3);
        switch(sector){
            case 1:
                imageView3.setImageResource(R.mipmap.img1);
                break;
            case 2:
                imageView3.setImageResource(R.mipmap.img2);
                break;
            case 3:
                imageView3.setImageResource(R.mipmap.img3);
                break;
            case 4:
                imageView3.setImageResource(R.mipmap.img4);
                break;
            case 5:
                imageView3.setImageResource(R.mipmap.img5);
                break;
            case 6:
                imageView3.setImageResource(R.mipmap.img6);
                break;
            case 7:
                imageView3.setImageResource(R.mipmap.img7);
                break;
            case 8:
                imageView3.setImageResource(R.mipmap.img8);
                break;
            case 9:
                imageView3.setImageResource(R.mipmap.img9);
                break;
            case 10:
                imageView3.setImageResource(R.mipmap.img10);
                break;
            case 11:
                imageView3.setImageResource(R.mipmap.img11);
                break;
            case 12:
                imageView3.setImageResource(R.mipmap.img12);
                break;
            default:
                imageView3.setImageResource(R.mipmap.img);
                break;

        }
    }
}