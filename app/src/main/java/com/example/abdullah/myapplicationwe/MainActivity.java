package com.example.abdullah.myapplicationwe;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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
import ai.snips.nlu.ontology.SlotValue;
import ai.snips.platform.SnipsPlatformClient;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

import static android.widget.Toast.*;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.getStructuringElement;

public class MainActivity extends AppCompatActivity  implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = MainActivity.class.getSimpleName();
    CameraBridgeViewBase cameraBridgeViewBase;
    private File assistantLocation;
    private SnipsPlatformClient client;

    Mat mat;
    Bitmap mBitmap;
    private static final double refSize = 3.55475628437;

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
        setContentView(R.layout.activity_main);

        //faceDetectionImageView = (ImageView) findViewById(R.id.faceDetectionJavaCameraView2);
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.faceDetectionJavaCameraView2);
        cameraBridgeViewBase.setVisibility(CameraBridgeViewBase.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        assistantLocation = new File(getFilesDir(), "snips");
        extractAssistantIfNeeded(assistantLocation);
        startSnips(assistantLocation);


        if (!OpenCVLoader.initDebug()){
            Log.d(TAG, "Internal OpenCv library found,Using OpenCV Manager for initialization ");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,baseLoaderCallback);
        }else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }
    @Override
    public void onResume() {
        //client.connect(this.getApplicationContext());
        //startSnips(assistantLocation);
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    @Override
    protected void onPause (){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }


    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:{
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


    //snips kram
    private void extractAssistantIfNeeded(File assistantLocation) {
        File versionFile = new File(assistantLocation,
                "android_version_" + BuildConfig.VERSION_NAME);

        if (versionFile.exists()) {
            return;
        }

        try {
            assistantLocation.delete();
            MainActivity.unzip(getBaseContext().getAssets().open("assistant.zip"),
                    assistantLocation);
            versionFile.createNewFile();
        } catch (IOException e) {
            return;
        }
    }
    private static void unzip(InputStream zipFile, File targetDirectory)
            throws IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipFile));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }
    private SnipsPlatformClient createClient(File assistantLocation) {
        File assistantDir  = new File(assistantLocation, "assistant");

        final SnipsPlatformClient client = new SnipsPlatformClient.Builder(assistantDir)
                .enableDialogue(true)
                .enableHotword(true)
                .enableSnipsWatchHtml(false)
                .enableLogs(true)
                .withHotwordSensitivity(0.5f)
                .enableStreaming(false)
                .enableInjection(false)
                .build();

        client.setOnPlatformReady(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Log.d(TAG, "Snips is ready. Say the wake word!");
                return null;
            }
        });

        client.setOnPlatformError(
                new Function1<SnipsPlatformClient.SnipsPlatformError, Unit>() {
                    @Override
                    public Unit invoke(final SnipsPlatformClient.SnipsPlatformError
                                               snipsPlatformError) {
                        // Handle error
                        Log.d(TAG, "Error: " + snipsPlatformError.getMessage());
                        return null;
                    }
                });

        client.setOnHotwordDetectedListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                // Wake word detected, start a dialog session
                Log.d(TAG, "Wake word detected!");
                client.startSession(null, new ArrayList<String>(),
                        false, null);
                return null;
            }
        });

        client.setOnIntentDetectedListener(new Function1<IntentMessage, Unit>() {
            @Override
            public Unit invoke(final IntentMessage intentMessage) {
                // Intent detected, so the dialog session ends here
                client.endSession(intentMessage.getSessionId(), null);
                Log.d(TAG, "Intent detected: " +
                        intentMessage.getIntent().getIntentName());
                // hier werden die verschiedenen handler aufgerufen
                switch (intentMessage.getIntent().getIntentName()){
                    case "lbug:TakePicture":
                        Log.d("Lukas", intentMessage.toString());
                        handleTakePicture(intentMessage);
                        break;

                    case "lbug:ConfirmAction":
                        Log.d("Lukas", intentMessage.toString());
                        handleConfirmAction(intentMessage);
                        break;
                    case "lbug:LocalizeWoundSector":
                        Log.d("Lukas", intentMessage.toString());
                        handleLocalizeWoundSector(intentMessage);
                        break;

                    case "lbug:WundverlaufAnzeigen":
                        Log.d("Lukas", intentMessage.toString());
                        handleWundverlaufAnzeigen(intentMessage);
                        break;

                    case "lbug:StartCamera":
                        Log.d("Lukas", intentMessage.toString());
                        handleStartCamera(intentMessage);
                        break;

                    case "lbug:Home":
                        Log.d("Lukas", intentMessage.toString());
                        handleHome(intentMessage);
                        break;

                    default:
                        Log.d("Lukas", "Default case");
                }
                return null;
            }
        });

        client.setOnSnipsWatchListener(new Function1<String, Unit>() {
            public Unit invoke(final String s) {
                Log.d(TAG, "Log: " + s);
                return null;
            }
        });

        return client;
    }


    private void startSnips(File snipsDir) {
        client = createClient(snipsDir);
        client.connect(this.getApplicationContext());
    }



    //voice commands


    private void handleHome(final IntentMessage intentMessage){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    private void handleStartCamera(final IntentMessage intentMessage) {
        showToast("StartCamera funktioniert!");
    }


    private void handleWundverlaufAnzeigen(final IntentMessage intentMessage) {
        Intent intent = new Intent(this, Woundhistory.class);
        startActivity(intent);
    }


    private void handleTakePicture(final IntentMessage intentMessage){
        makeText(this, "Command received", LENGTH_LONG).show();
        // convert to bitmap:
        mBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, mBitmap);

        java.util.Date date=new Date();
        String procpath = Environment.getExternalStorageDirectory() + "/Pictures/"+date+"image.jpg";

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(procpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        Log.d("Bild gemacht", "Es wurde ein Bild gemacht");

        detectWound(procpath);

        //cameraBridgeViewBase.setVisibility(View.GONE);
    }
    private void handleLocalizeWoundSector(final IntentMessage intentMessage) {
        List<Slot> slots = intentMessage.getSlots();
        int v1 = (int)((SlotValue.NumberValue)slots.get(0).getValue()).getValue();
        Localization.woundSector = v1;
        String message = "Wundsektor" + v1;
        Log.d("Lukas", message);
        makeText(this, message, LENGTH_LONG).show();
        Log.d("Lukas", "handleLocalizeWoundSector");
        Intent intent = new Intent(this, Localization.class);
        startActivity(intent);
    }

    private void handleConfirmAction(final IntentMessage intentMessage) {

        List<Slot> slots = intentMessage.getSlots();
        String v1 = slots.get(0).component1();
        String answer = getIntent().getStringExtra("ConfirmAction");
        Log.d("Confirmation",v1);

        switch(v1){
            case"ja":
                Intent localizationIntent = new Intent(this, Localization.class);
                makeText(this, "ConfirmAction", LENGTH_SHORT).show();
                startActivity(localizationIntent);
                break;

            case"nein":
                makeText(this, "ConfirmAction nein", LENGTH_SHORT).show();
                onBackPressed();
                break;

            default:
                break;
        }
    }


    //wound detector
    private void detectWound(String procpath) {

        Bitmap bitmap = BitmapFactory.decodeFile(procpath);

        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap, mat);

        Mat rgbMat = new Mat();
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_RGBA2BGR);

        Imgproc.blur(rgbMat, rgbMat, new Size(30, 30));
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

        double gcurrentMax = 0;
        for (MatOfPoint c: ghullList){
            double area= Imgproc.contourArea(c);
            if(area>gcurrentMax){
                gcurrentMax = area;
            }
        }

        Imgproc.drawContours(mat, ghullList, glargest_contour_index, new Scalar(0, 0, 255, 255), 3);

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

        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[rcontours.size()];
        Rect[] boundRect = new Rect[rcontours.size()];
        double rlargest_area =0;
        int rlargest_contour_index = 0;
        for (int contourIdx = 0; contourIdx < rcontours.size(); contourIdx++) {
            double contourArea = Imgproc.contourArea(rcontours.get(contourIdx));
            if (contourArea > rlargest_area) {
                rlargest_area = contourArea;
                rlargest_contour_index = contourIdx;
            }
        }

        double rcurrentMax = 0;
        for (MatOfPoint c: rhullList){
            double area= Imgproc.contourArea(c);
            if(area>rcurrentMax){
                rcurrentMax = area;
            }
        }

        Imgproc.drawContours(mat, rhullList, rlargest_contour_index, new Scalar(0, 255, 0, 255), 2);

        //lets draw a bounding rectangle
        contoursPoly[rlargest_contour_index] = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(rcontours.get(rlargest_contour_index).toArray()), contoursPoly[rlargest_contour_index], 3, true);
        boundRect[rlargest_contour_index] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[rlargest_contour_index].toArray()));
        Imgproc.rectangle(mat, boundRect[rlargest_contour_index].tl(), boundRect[rlargest_contour_index].br(), new Scalar(0, 255, 0, 255), 2);

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

        double woundSize = refSize * rcurrentMax/gcurrentMax;

        Woundhistory.procpath = procpath;
        sendAway(procpath,woundSize);
    }

    private void sendAway(String procpath, double woundSize){

        Intent intent = new Intent(this, Result.class);
        intent.putExtra("image", procpath);
        intent.putExtra("woundSize", woundSize);

        startActivity(intent);
    }


    //wound location
    public void locateWound(View view) {
        showImagePopup();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mat = new Mat(height,width, CvType.CV_8UC4);
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
    private void showToast(final String text) {
        runOnUiThread(() ->
                makeText(getApplicationContext(), text, LENGTH_SHORT).show()
        );
    }
    public void showImagePopup() {
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
        imageView.setImageResource(R.mipmap.img);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
    public void mediaScan(String picpath) {
        MediaScannerConnection.scanFile(this,
                new String[] { picpath }, null,
                (path, uri) -> {}
        );
    }
    /*private void showPic(String procpath) {

        mediaScan(procpath);
        Bitmap bitmap = BitmapFactory.decodeFile(procpath);

        ImageView imageview = findViewById(R.id.ImageView01); //sets imageview as the bitmap
        imageview.setImageBitmap(bitmap);


    }*/
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



}