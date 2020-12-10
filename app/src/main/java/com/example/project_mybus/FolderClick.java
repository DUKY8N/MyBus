package com.example.project_mybus;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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

    public void AddText(String busnum, String cityid, String startnm, String endnm) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 200;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        TextView busnum_tv = new TextView(this);
        TextView startnm_tv = new TextView(this);
        TextView endnm_tv = new TextView(this);
        ImageView busicon_v = new ImageView(this);
        if((cityid.equals("37020")) || (cityid.equals("31230"))){
            busnum_tv.setText(busnum + "번 버스");
        }
        else {
            busnum_tv.setText(busnum);
        }
        busnum_tv.setTextSize(25);
        busnum_tv.setEllipsize(TextUtils.TruncateAt.END);
        busnum_tv.setSingleLine(true);
        busnum_tv.setPadding(50, 30, 0, 0);

        startnm_tv.setText(startnm + " -> ");
        startnm_tv.setTextSize(15);
        endnm_tv.setText(endnm);
        endnm_tv.setTextSize(15);
        endnm_tv.setEllipsize(TextUtils.TruncateAt.END);
        endnm_tv.setSingleLine(true);

        LinearLayout layout_tv2 = new LinearLayout(this);
        layout_tv2.setOrientation(LinearLayout.HORIZONTAL);
        layout_tv2.setGravity(Gravity.CENTER);
        layout_tv2.setPadding(30, 0, 0, 0);
        layout_tv2.addView(startnm_tv);
        layout_tv2.addView(endnm_tv);

        LinearLayout layout_tv = new LinearLayout(this);
        layout_tv.setOrientation(LinearLayout.VERTICAL);
        layout_tv.setGravity(Gravity.CENTER);
        layout_tv.addView(busnum_tv);
        layout_tv.addView(layout_tv2);

        busicon_v.setImageResource(R.drawable.bus_icon3);
        busicon_v.setAdjustViewBounds(true);
        busicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        busicon_v.setPadding(60, 30, 0, 30);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(busicon_v);
        dynamicHori.addView(layout_tv);
        dynamicLayout.addView(dynamicHori);
    }
}