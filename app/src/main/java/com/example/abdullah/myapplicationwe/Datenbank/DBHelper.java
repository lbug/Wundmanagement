package com.example.abdullah.myapplicationwe.Datenbank;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = DBHelper.class.getSimpleName();

    public static final String DB_NAME = "project_wound.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_1_PATIENT = "patient";
    public static final String TABLE_2_WOUND = "wound";
    public static final String TABLE_3_PICTURE = "picture";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "Timestamp";
    public static final String COLUMN_IMAGEPATH = "Imagepath";
    public static final String COLUMN_LENGTH = "woundLength";
    public static final String COLUMN_WIDTH = "woundWidth";
    public static final String COLUMN_PATIENTID = "PatientID";
    public static final String COLUMN_WOUNDID = "WoundID";


    public static final String SQL_CREATE_PATIENT =
            "CREATE TABLE " + TABLE_1_PATIENT + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT);";
    public static final String SQL_CREATE_WOUND =
            "CREATE TABLE " + TABLE_2_WOUND + "(" +
                    COLUMN_ID + " INTEGER, " +
                    COLUMN_PATIENTID + " INTEGER NOT NULL," +
                    "PRIMARY KEY(" + COLUMN_ID + "," + COLUMN_PATIENTID + ")," +
                    "FOREIGN KEY(" + COLUMN_PATIENTID + ") REFERENCES " + TABLE_1_PATIENT + "(_id));";
    public static final String SQL_CREATE_PICTURE =
            "CREATE TABLE " + TABLE_3_PICTURE + "(" +
                    COLUMN_ID + " INTEGER," +
                    COLUMN_TIMESTAMP + " LONG NOT NULL," +
                    COLUMN_IMAGEPATH + " TEXT NOT NULL," +
                    COLUMN_LENGTH + " REAL NOT NULL," +
                    COLUMN_WIDTH + " REAL NOT NULL," +
                    COLUMN_WOUNDID + " INTEGER NOT NULL," +
                    "PRIMARY KEY(" + COLUMN_ID + "," + COLUMN_TIMESTAMP + "," + COLUMN_WOUNDID + ")," +
                    "FOREIGN KEY(" + COLUMN_WOUNDID + ") REFERENCES " + TABLE_2_WOUND + "(_id));";




    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.i(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    public DBHelper(@android.support.annotation.Nullable Context context, @android.support.annotation.Nullable String name, @android.support.annotation.Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_PATIENT + " angelegt.");
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_WOUND + " angelegt.");
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_PICTURE + " angelegt.");
            db.execSQL(SQL_CREATE_PATIENT);
            db.execSQL(SQL_CREATE_WOUND);
            db.execSQL(SQL_CREATE_PICTURE);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}