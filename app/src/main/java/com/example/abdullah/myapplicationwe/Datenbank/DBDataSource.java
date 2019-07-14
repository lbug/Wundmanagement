package com.example.abdullah.myapplicationwe.Datenbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBDataSource {

    private static final String LOG_TAG = DBDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] woundColumns = {DBHelper.COLUMN_ID};
    private String[] patientColumns = {DBHelper.COLUMN_ID};
    private String[] pictureColumns = {DBHelper.COLUMN_ID, DBHelper.COLUMN_TIMESTAMP, DBHelper.COLUMN_IMAGEPATH, DBHelper.COLUMN_LENGTH, DBHelper.COLUMN_WIDTH};


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
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    //Prüfen ob Test-Wunde bereits vorhanden ist
    public boolean woundHasRow() {
        List<Wound> shoppingMemoList = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_2_WOUND, woundColumns, null, null, null, null, null);
        //int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
        if (cursor.moveToNext()) {
            return false;
        }
        return true;
    }

    //Prüfen ob Test-Patient bereits vorhanden ist
    public boolean patientHasRow() {
        List<Wound> shoppingMemoList = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_1_PATIENT, patientColumns, null, null, null, null, null);
        //int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
        if (cursor.moveToNext()) {
            return false;
        }
        return true;
    }

    //Test-Datensätze erstellen
    public void createTestRows(DBDataSource dataSource) {
        long insertID = 1;
        if(!patientHasRow()) {
            insertID = database.insert(DBHelper.TABLE_1_PATIENT, null, null);
        } else {
            Log.d("TestRows", "Test Patient bereits vorhanden");
        }

        if(!woundHasRow()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_PATIENTID, insertID);
            database.insert(DBHelper.TABLE_2_WOUND, null, values);
        } else {
            Log.d("TestRows", "Test Wunde bereits vorhanden");
        }
    }

    //Bild erstellen
    public void createPicture(String imagepath, double woundLength, double woundWidth) {
        ContentValues values = new ContentValues();
        long woundID = 1; //Erste Datensätze sollten ID gleich eins sein
        long patientID = 1;

        values.put(DBHelper.COLUMN_IMAGEPATH, imagepath);
        values.put(DBHelper.COLUMN_LENGTH, woundLength);
        values.put(DBHelper.COLUMN_WIDTH, woundWidth);
        values.put(DBHelper.COLUMN_WOUNDID, woundID);
        values.put(DBHelper.COLUMN_PATIENTID, patientID);

        long timestamp = System.currentTimeMillis() / 1000L; //Unixtime
        values.put(DBHelper.COLUMN_TIMESTAMP, timestamp);

        database.insert(DBHelper.TABLE_3_PICTURE, null, values);

    }

    //Zeile aus Query auslesen
    private Picture cursorToPicture(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
        int idTimestamp = cursor.getColumnIndex(DBHelper.COLUMN_TIMESTAMP);
        int idImagepath = cursor.getColumnIndex(DBHelper.COLUMN_IMAGEPATH);
        int idImageWidth = cursor.getColumnIndex(DBHelper.COLUMN_WIDTH);
        int idImageLength = cursor.getColumnIndex(DBHelper.COLUMN_LENGTH);

        long id = cursor.getLong(idIndex);
        long timestamp = cursor.getLong(idTimestamp);
        String imagepath = cursor.getString(idImagepath);
        double imageWidth = cursor.getLong(idImageWidth);
        double imageLength = cursor.getLong(idImageLength);

        Picture picture = new Picture(id, timestamp, imagepath, imageLength, imageWidth);

        return picture;
    }

    //Die letzten 3 Bilder abfragen
    public List<Picture> lastThreePictures() {
        List<Picture> pictures = new ArrayList<>();
        Picture picture;
        Cursor cursor = database.query(DBHelper.TABLE_3_PICTURE, pictureColumns, null, null, null, null, DBHelper.COLUMN_TIMESTAMP, "3");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            picture = cursorToPicture(cursor);
            pictures.add(picture);
            cursor.moveToNext();
        }
        return pictures;
    }
}