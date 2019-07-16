package com.example.abdullah.myapplicationwe.Datenbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBDataSource {

    private static final String LOG_TAG = "Datenbank";

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] woundColumns = {DBHelper.COLUMN_ID};
    private String[] patientColumns = {DBHelper.COLUMN_ID};
    private String[] pictureColumns = {DBHelper.COLUMN_ID, DBHelper.COLUMN_TIMESTAMP, DBHelper.COLUMN_IMAGEPATH, DBHelper.COLUMN_HEIGTH, DBHelper.COLUMN_WIDTH};


    public DBDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new DBHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d("Datenbank", "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    //Bilddatensatz erstellen
    public void createPicture(String imagepath, double woundHeigth, double woundWidth) {
        ContentValues values = new ContentValues();

        values.put(DBHelper.COLUMN_IMAGEPATH, imagepath);
        values.put(DBHelper.COLUMN_HEIGTH, woundHeigth);
        values.put(DBHelper.COLUMN_WIDTH, woundWidth);
        //values.put(DBHelper.COLUMN_WOUNDSIZE, woundSize);

        long timestamp = System.currentTimeMillis() / 1000L; //Unixtime
        values.put(DBHelper.COLUMN_TIMESTAMP, timestamp);

        database.insert(DBHelper.TABLE_3_PICTURE, null, values);
    }

    //Zeile aus Query auslesen
    private Picture cursorToPicture(Cursor cursor) {
        Picture picture = null;
        int id = 0;
        long timestamp = 0;
        String imagepath = "test";
        double imageHeigth = 0.0;
        double imageWidth = 0.0;
        double woundSize = 0.0;

        if(cursor != null) {
            int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            int idTimestamp = cursor.getColumnIndex(DBHelper.COLUMN_TIMESTAMP);
            int idImagepath = cursor.getColumnIndex(DBHelper.COLUMN_IMAGEPATH);
            int idImageHeigth = cursor.getColumnIndex(DBHelper.COLUMN_HEIGTH);
            int idImageWidth = cursor.getColumnIndex(DBHelper.COLUMN_WIDTH);
            //int idWoundSize = cursor.getColumnIndexOrThrow(DBHelper.COLUMN_WOUNDSIZE);

            id = cursor.getInt(idIndex);
            timestamp = cursor.getLong(idTimestamp);
            imagepath = cursor.getString(idImagepath);
            imageHeigth = cursor.getDouble(idImageHeigth);
            imageWidth = cursor.getDouble(idImageWidth);
            //woundSize = cursor.getDouble(idWoundSize);

            picture = new Picture(id, timestamp, imagepath, imageHeigth, imageWidth);
        } else{
            Log.d("Datenbank", "Cursor ist null");
        }

        return picture;
    }

    //Die letzten 3 Bilder abfragen
    public List<Picture> lastThreePictures() {
        List<Picture> pictures = new ArrayList<>();
        Picture picture;
        Cursor cursor = null;
        cursor = database.query(DBHelper.TABLE_3_PICTURE, pictureColumns, null, null, null, null, DBHelper.COLUMN_TIMESTAMP + " DESC", "3");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            picture = cursorToPicture(cursor);
            pictures.add(picture);
            cursor.moveToNext();
        }
        return pictures;
    }
}