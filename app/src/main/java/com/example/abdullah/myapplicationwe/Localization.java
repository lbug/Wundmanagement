package com.example.abdullah.myapplicationwe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Localization extends AppCompatActivity {


    public static int woundSector=0;
    public static String procPath = "";

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
        setContentView(R.layout.activity_localization);

        if(Localization.woundSector==0){
            //locateWound();
            showImagePopup(R.mipmap.img);
        }
        else{
            switch(woundSector){
                case 1:
                    showImagePopup(R.mipmap.img1);
                    break;
                case 2:
                    showImagePopup(R.mipmap.img2);
                    break;
                case 3:
                    showImagePopup(R.mipmap.img3);
                    break;
                case 4:
                    showImagePopup(R.mipmap.img4);
                    break;
                case 5:
                    showImagePopup(R.mipmap.img5);
                    break;
                case 6:
                    showImagePopup(R.mipmap.img6);
                    break;
                case 7:
                    showImagePopup(R.mipmap.img7);
                    break;
                case 8:
                    showImagePopup(R.mipmap.img8);
                    break;
                case 9:
                    showImagePopup(R.mipmap.img9);
                    break;
                case 10:
                    showImagePopup(R.mipmap.img10);
                    break;
                case 11:
                    showImagePopup(R.mipmap.img11);
                    break;
                case 12:
                    showImagePopup(R.mipmap.img12);
                    break;
                default:
                    showImagePopup(R.mipmap.img);
                    break;

            }
        }

    }

    //wound location
    public void locateWound(View vLocaliiew) {
        showImagePopup(R.mipmap.img);
    }


    public void showImagePopup(int integer) {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(integer);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();


    }

}
