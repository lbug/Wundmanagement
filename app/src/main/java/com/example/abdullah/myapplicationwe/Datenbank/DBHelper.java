package com.example.abdullah.myapplicationwe.Datenbank;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{


    public static final String DB_NAME = "project_wound.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_3_PICTURE = "picture";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "Timestamp";
    public static final String COLUMN_IMAGEPATH = "Imagepath";
    public static final String COLUMN_HEIGTH = "woundLength";
    public static final String COLUMN_WIDTH = "woundWidth";
    public static final String COLUMN_WOUNDSIZE = "woundSize";

    public static final String SQL_CREATE_PICTURE_NEW =
            "CREATE TABLE " + TABLE_3_PICTURE + "(" +
                    COLUMN_ID + " INTEGER," +
                    COLUMN_TIMESTAMP + " LONG NOT NULL," +
                    COLUMN_IMAGEPATH + " TEXT NOT NULL," +
                    COLUMN_HEIGTH + " REAL NOT NULL," +
                    COLUMN_WIDTH + " REAL NOT NULL," +
                    "PRIMARY KEY(" + COLUMN_ID + "," + COLUMN_TIMESTAMP + "));";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.i("Datenbank", "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    public DBHelper(@android.support.annotation.Nullable Context context, @android.support.annotation.Nullable String name, @android.support.annotation.Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d("Datenbank", "Tabelle Picture neu wird erstellt");
            db.execSQL(SQL_CREATE_PICTURE_NEW);
        }
        catch (Exception ex) {
            Log.d("Datenbank", "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}