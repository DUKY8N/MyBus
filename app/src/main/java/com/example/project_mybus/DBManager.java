package com.example.project_mybus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context) {
        super(context, "DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table BusBookMark (folder text, busnum text, cityid text, startnm text, endnm text, busid text)");
        db.execSQL("create table folders (name text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
