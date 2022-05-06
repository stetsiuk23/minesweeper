package com.example.myminesweeper.ResultDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mineswiper_resultss";
    public static final int DATABASE_VERSION = 4;

    public static final String TABLE_NAME = "results";
    public static final String TABLE_COLUMN_ID = "_id";
    public static final String TABLE_COLUMN_LEVEL = "level";
    public static final String TABLE_COLUMN_NAME = "name";
    public static final String TABLE_COLUMN_RESULT = "result_time";
    public static final String TABLE_COLUMN_RESULT_SECONDS = "result_time_seconds";

    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " ("+TABLE_COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TABLE_COLUMN_NAME+" TEXT, "+
            TABLE_COLUMN_RESULT+" TEXT, "+TABLE_COLUMN_RESULT_SECONDS+" INTEGER, "+TABLE_COLUMN_LEVEL+" TEXT);";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);

        onCreate(db);
    }
}
