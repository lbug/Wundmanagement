package com.example.abdullah.myapplicationwe;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //variables
    private static final String TAG = "MainActivity";
    private static final double refSize = 3.55475628437;


    //states
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
        Log.d(TAG, "onCreate: started.");
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    //perms
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    //global detector
    protected Bitmap detect(String procpath) {

        Bitmap bitmap = BitmapFactory.decodeFile(procpath);

        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap, mat);

        Mat rgbMat = new Mat();
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_RGBA2BGR);

        Mat dilatedMat = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7));
        Imgproc.morphologyEx(rgbMat, dilatedMat, Imgproc.MORPH_OPEN, kernel);

        //red
        Mat greenMat = new Mat();
        Core.inRange(rgbMat, new Scalar(0, 120, 0), new Scalar(100, 255, 100), greenMat);

        //find contour
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(greenMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> hullList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            hullList.add(new MatOfPoint(hullPoints));
        }
        double largest_area =0;
        int largest_contour_index = 0;

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (contourArea > largest_area) {
                largest_area = contourArea;
                largest_contour_index = contourIdx;
            }
        }

        Imgproc.drawContours(mat, hullList, largest_contour_index, new Scalar(0, 0, 255, 255), 3);

        double currentMax = 0;
        for (MatOfPoint c: hullList){
            double area= Imgproc.contourArea(c);
            if(area>currentMax){
                currentMax = area;
            }
        }
        TextView size = findViewById(R.id.textView2);
        size.setText("Digital Size: "+ currentMax);



        Bitmap outputImage= Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, outputImage);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(procpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outputImage.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored


        /**ImageView imageview = findViewById(R.id.ImageView01); //sets imageview as the bitmap
        imageview.setImageBitmap(outputImage);**/

        double realSize = (refSize * detectWound(procpath))/currentMax;
        //detectWound(procpath);

        TextView showSize = findViewById(R.id.textView3);
        showSize.setText("Wound Size: "+realSize+" cm²" );


        return outputImage;
    }

    //wound detector
    protected double detectWound(String procpath) {

        Bitmap bitmap = BitmapFactory.decodeFile(procpath);

        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap, mat);

        Mat rgbMat = new Mat();
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_RGBA2BGR);

        Mat dilatedMat = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7));
        Imgproc.morphologyEx(rgbMat, dilatedMat, Imgproc.MORPH_OPEN, kernel);

        //red
        Mat redMat = new Mat();
        Core.inRange(rgbMat, new Scalar(0, 0, 120), new Scalar(100, 100, 255), redMat);

        //find contour
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(redMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> hullList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            hullList.add(new MatOfPoint(hullPoints));
        }
        double largest_area =0;
        int largest_contour_index = 0;

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (contourArea > largest_area) {
                largest_area = contourArea;
                largest_contour_index = contourIdx;
            }
        }

        Imgproc.drawContours(mat, hullList, largest_contour_index, new Scalar(0, 255, 0, 255), 3);

        double currentMax = 0;
        for (MatOfPoint c: hullList){
            double area= Imgproc.contourArea(c);
            if(area>currentMax){
                currentMax = area;
            }
        }
        TextView size = findViewById(R.id.textView);
        size.setText("Digital Size: "+ currentMax);



        Bitmap outputImage= Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, outputImage);


        ImageView imageview = findViewById(R.id.ImageView01); //sets imageview as the bitmap
        imageview.setImageBitmap(outputImage);


        return currentMax;
    }


    //file copier
    private String copy(String path, int copyNumber){
        String copy_path = path + "_copy" + copyNumber + ".png";
        try {
            FileUtils.copyFile(new File(path), new File(copy_path));
            System.out.println(copy_path);
            return copy_path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //frontend
    public void bckgRem(View view) {
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,1338);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                /**case 1337:
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();


                    EdgeDetector(imgDecodableString);

                    break;**/

                case 1338:
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage8 = data.getData();
                    String[] filePathColumn8 = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor8 = getContentResolver().query(selectedImage8, filePathColumn8, null, null, null);
                    // Move to first row
                    cursor8.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex8 = cursor8.getColumnIndex(filePathColumn8[0]);
                    //Gets the String value in the column
                    String imgDecodableString8 = cursor8.getString(columnIndex8);
                    cursor8.close();

                    System.out.println(imgDecodableString8);

                    detect(copy(imgDecodableString8, 2));

                    break;
            }
    }
}
