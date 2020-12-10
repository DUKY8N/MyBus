package com.example.project_mybus;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FolderClick extends AppCompatActivity {

    DBManager dbManager;
    SQLiteDatabase sqlitedb;
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    String str_folder = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_click);

        try {
            dbManager = new DBManager(this);
            sqlitedb = dbManager.getReadableDatabase();
            str_folder = ;
            Cursor cursor = sqlitedb.query("BusBookMark", null, "folder = ?", new String[]{str_folder}, null, null, null);
            while (cursor.moveToNext()) {
                String busnum = cursor.getString(cursor.getColumnIndex("busnum"));
                String cityid = cursor.getString(cursor.getColumnIndex("cityid"));
                String startnm = cursor.getString(cursor.getColumnIndex("startnm"));
                String endnm = cursor.getString(cursor.getColumnIndex("endnm"));
                String routeId = cursor.getString(cursor.getColumnIndex("busid"));

                AddText(busnum, cityid, startnm, endnm);
            }
            cursor.close();
            sqlitedb.close();
            dbManager.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}