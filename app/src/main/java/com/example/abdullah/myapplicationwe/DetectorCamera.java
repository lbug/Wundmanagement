package com.example.abdullah.myapplicationwe;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.abdullah.myapplicationwe.Datenbank.DBDataSource;

import org.apache.commons.io.FileUtils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.widget.Toast.*;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.getStructuringElement;

public class DetectorCamera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = DetectorCamera.class.getSimpleName();
    CameraBridgeViewBase cameraBridgeViewBase;
    Mat mat;
    Bitmap mBitmap;
    private static final double refSize = 4.71;
    private static final double refLengthandWidth = 2.45;
    private static DetectorCamera instance;

    public static DetectorCamera getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);

        cameraBridgeViewBase = findViewById(R.id.faceDetectionJavaCameraView2);
        cameraBridgeViewBase.setVisibility(CameraBridgeViewBase.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);



        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCv library found,Using OpenCV Manager for initialization ");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        System.out.println("I JUST CREATED");

    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("I JUST RESUMED");

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("I JUST PAUSED");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("I JUST STOPPED");
    }

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraBridgeViewBase.enableView();
                }
                break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };


    public void handleTakePicture(DBDataSource dataSource) {
        makeText(this, "Command received", LENGTH_LONG).show();
        // convert to bitmap
        if (mat.size().height != 0 && mat.size().width != 0) {
            mBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, mBitmap);
        }

        java.util.Date date = new Date();
        String procpath = Environment.getExternalStorageDirectory() + "/Pictures/" + date + "image.jpg";

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(procpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        Log.d("Bild gemacht", "Es wurde ein Bild gemacht");

        detectWound(procpath,dataSource);
    }



    //wound detector
    private void detectWound(String procpath, DBDataSource dataSource) {

        Bitmap bitmap = BitmapFactory.decodeFile(procpath);

        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap, mat);

        Mat rgbMat = new Mat();
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_RGBA2BGR);

        Photo.fastNlMeansDenoising(rgbMat, rgbMat, 25, 7, 21);

        Imgproc.blur(rgbMat, rgbMat, new Size(15, 15));
        Imgproc.threshold(rgbMat,rgbMat,114,255,0);
        Mat element = getStructuringElement( MORPH_ELLIPSE,
                new Size( 30, 30 ),
                new Point( 3, 3 ) );
        Imgproc.dilate( rgbMat, rgbMat, element );

        /**Mat dilatedMat = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7));
        Imgproc.morphologyEx(rgbMat, dilatedMat, Imgproc.MORPH_OPEN, kernel);**/

        //green
        Mat greenMat = new Mat();
        Core.inRange(rgbMat, new Scalar(0, 120, 0), new Scalar(100, 255, 100), greenMat);
        Mat redMat = new Mat();
        Core.inRange(rgbMat, new Scalar(0, 0, 120), new Scalar(100, 100, 255), redMat);

        //find contour
        Mat ghierarchy = new Mat();
        List<MatOfPoint> gcontours = new ArrayList<>();
        Mat rhierarchy = new Mat();
        List<MatOfPoint> rcontours = new ArrayList<>();

        Imgproc.findContours(greenMat, gcontours, ghierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(redMat, rcontours, rhierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> ghullList = new ArrayList<>();
        for (MatOfPoint contour : gcontours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            ghullList.add(new MatOfPoint(hullPoints));
        }

        double glargest_area =0;
        int glargest_contour_index = 0;
        for (int contourIdx = 0; contourIdx < gcontours.size(); contourIdx++) {
            double contourArea = Imgproc.contourArea(gcontours.get(contourIdx));
            if (contourArea > glargest_area) {
                glargest_area = contourArea;
                glargest_contour_index = contourIdx;
            }
        }



        List<MatOfPoint> rhullList = new ArrayList<>();
        for (MatOfPoint contour : rcontours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            rhullList.add(new MatOfPoint(hullPoints));
        }

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[rcontours.size()];
        Rect[] boundRect = new Rect[rcontours.size()];
        Rect[] boundRectg = new Rect[gcontours.size()];
        double rlargest_area =0;
        int rlargest_contour_index = 0;
        for (int contourIdx = 0; contourIdx < rcontours.size(); contourIdx++) {
            double contourArea = Imgproc.contourArea(rcontours.get(contourIdx));
            if (contourArea > rlargest_area) {
                rlargest_area = contourArea;
                rlargest_contour_index = contourIdx;
            }
        }


        Imgproc.drawContours(mat, ghullList, glargest_contour_index, new Scalar(0, 0, 255, 255), 2);
        Imgproc.drawContours(mat, rhullList, rlargest_contour_index, new Scalar(0, 255, 0, 255), 2);

        //lets draw a bounding rectangle
        if (rcontours.isEmpty()) {
        } else {
            contoursPoly[rlargest_contour_index] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(rcontours.get(rlargest_contour_index).toArray()), contoursPoly[rlargest_contour_index], 3, true);
            boundRect[rlargest_contour_index] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[rlargest_contour_index].toArray()));
        //Imgproc.rectangle(mat, boundRect[rlargest_contour_index].tl(), boundRect[rlargest_contour_index].br(), new Scalar(0, 255, 0, 255), 2);
        }

        //lets create an imaginary bounding rectangle for the marker
        if (gcontours.isEmpty()) {
        } else {
            contoursPoly[glargest_contour_index] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(gcontours.get(glargest_contour_index).toArray()), contoursPoly[glargest_contour_index], 3, true);
            boundRectg[glargest_contour_index] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[glargest_contour_index].toArray()));
        //Imgproc.rectangle(mat, boundRectg[glargest_contour_index].tl(), boundRectg[glargest_contour_index].br(), new Scalar(0, 0, 255, 255), 2);
        }

        double realWoundLength = 0;
        double realWoundWidth = 0;
        double woundSize = 0;
        if (rcontours.isEmpty() || gcontours.isEmpty()) {
        } else {
            realWoundLength = (refLengthandWidth*boundRect[rlargest_contour_index].size().height)/(boundRectg[glargest_contour_index].size().height);
            realWoundWidth = (refLengthandWidth*boundRect[rlargest_contour_index].size().width)/(boundRectg[glargest_contour_index].size().width);
            woundSize = refSize * Imgproc.contourArea(rcontours.get(rlargest_contour_index))/Imgproc.contourArea(gcontours.get(glargest_contour_index));
        }

        Bitmap outputImage= Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, outputImage);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(procpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outputImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        mediaScan(procpath);

        WoundHistory.procpath = procpath;
        sendAway(procpath,round(woundSize,2),round(realWoundLength,2),round(realWoundWidth,2),dataSource);
    }


    //camera states
    @Override
    public void onCameraViewStarted(int width, int height) {
        mat = new Mat(height, width, CvType.CV_8UC4);
    }
    @Override
    public void onCameraViewStopped() {
        mat.release();
    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat = inputFrame.rgba();
        return mat;
    }

    //miscellaneous
    private void sendAway(String procpath, double woundSize, double rectSizeHeight, double rectSizeWidth,DBDataSource dataSource) {

        Intent intent = new Intent(this, Result.class);
        intent.putExtra("image", procpath);
        //intent.putExtra("woundSize", woundSize);
        intent.putExtra("rectSizeHeight", rectSizeHeight);
        intent.putExtra("rectSizeWidth", rectSizeWidth);

        dataSource.createPicture(procpath, rectSizeHeight, rectSizeWidth);

        startActivity(intent);
    }
    private void showToast(String text) {
        runOnUiThread(() ->
                makeText(getApplicationContext(), text, LENGTH_SHORT).show()
        );
    }
    public void mediaScan(String picpath) {
        MediaScannerConnection.scanFile(this,
                new String[]{picpath}, null,
                (path, uri) -> {
                }
        );
    }
    private String copy(String path, int copyNumber) {
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
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}